package com.dokja.mizumi.epub

import com.dokja.mizumi.epub.EpubBook.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream
import kotlin.io.path.invariantSeparatorsPathString


data class EpubFile(val absPath: String, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EpubFile

        if (absPath != other.absPath) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = absPath.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}


private suspend fun getZipFiles(
    inputStream: InputStream
): Map<String, EpubFile> = withContext(Dispatchers.IO) {
    ZipInputStream(inputStream).use { zipInputStream ->
        zipInputStream
            .entries()
            .filterNot { it.isDirectory }
            .map {
                EpubFile(absPath = it.name, data = zipInputStream.readBytes())
            }
            .associateBy { it.absPath }
    }
}


@Throws(Exception::class)
suspend fun epubParser(
    inputStream: InputStream
): EpubBook = withContext(Dispatchers.Default) {
    val files = getZipFiles(inputStream)


    val container = files["META-INF/container.xml"]
        ?: throw Exception("META-INF/container.xml file missing")

    val opfFilePath = parseXMLFile(container.data)
        ?.selectFirstTag("rootfile")
        ?.getAttributeValue("full-path")
        ?.decodedURL ?: throw Exception("Invalid container.xml file")
    // Extract rootPath
    val rootPath = opfFilePath.substringBefore('/', "") // Get the part before the first slash
    val opfFile = files[opfFilePath] ?: throw Exception(".opf file missing")

    val document = parseXMLFile(opfFile.data)
        ?: throw Exception(".opf file failed to parse data")
    val metadata = document.selectFirstTag("metadata")
        ?: throw Exception(".opf file metadata section missing")
    val manifest = document.selectFirstTag("manifest")
        ?: throw Exception(".opf file manifest section missing")
    val spine = document.selectFirstTag("spine")
        ?: throw Exception(".opf file spine section missing")
    val guide = document.selectFirstTag("guide")




    val metadataTitle = metadata.selectFirstChildTag("dc:title")?.textContent
        ?: "Unknown Title"
    val metadataCreator = metadata.selectFirstChildTag("dc:creator")?.textContent

    val metadataDesc = metadata.selectFirstChildTag("dc:description")?.textContent

    val metadataCoverId = metadata
        .selectChildTag("meta")
        .find { it.getAttributeValue("name") == "cover" }
        ?.getAttributeValue("content")


    val hrefRootPath = File(opfFilePath).parentFile ?: File("")
    fun String.hrefAbsolutePath() = File(hrefRootPath, this).canonicalFile
        .toPath()
        .invariantSeparatorsPathString
        .removePrefix("/")

    val manifestItems = manifest.selectChildTag("item").map {
        ManifestItem(
            id = it.getAttribute("id"),
            absPath = it.getAttribute("href").decodedURL.hrefAbsolutePath(),
            mediaType = it.getAttribute("media-type"),
            properties = it.getAttribute("properties")
        )
    }.associateBy { it.id }



    fun parseCoverImageFromXhtml(coverFile: EpubFile): EpubBook.Image? {
        val doc = Jsoup.parse(coverFile.data.inputStream(), "UTF-8", "")
        // Find the <img> tag within the XHTML file (adjust the selector if needed)
        val imgTag = doc.selectFirst("img")

        if (imgTag != null) {
            var imgSrc = imgTag.attribute("src").value.hrefAbsolutePath()
            if (!imgSrc.startsWith("$rootPath/")) {
                imgSrc = "$rootPath/$imgSrc" // Add the prefix
            }

            val imgFile = files[imgSrc]


            if (imgFile != null) {
                return Image(absPath = imgFile.absPath, image = imgFile.data)
            }
        }
        return null // Cover image not found in the XHTML
    }

    // 1. Primary Method: Try to get the cover image from the manifest
    var coverImage = manifestItems[metadataCoverId]
        ?.let { files[it.absPath] }
        ?.let { Image(absPath = it.absPath, image = it.data) }

    // 2. Fallback: Check the `<guide>` tag if the primary method didn't yield a cover
    if (coverImage == null) {
        var coverHref = guide?.selectChildTag("reference")
            ?.find { it.getAttribute("type") == "cover" }
            ?.getAttributeValue("href")?.decodedURL?.hrefAbsolutePath()

        if (guide == null) {
            val manifestCoverItem = manifestItems["cover"]
            coverHref = manifestCoverItem?.absPath
        }
        if (coverHref != null) {
            val coverFile = files[coverHref]
            if (coverFile != null) {
                coverImage = parseCoverImageFromXhtml(coverFile)
            }
        }


    }

    val ncxFilePath = manifestItems["ncx"]?.absPath
    val ncxFile = files[ncxFilePath] ?: throw Exception("ncx file missing")


    val doc = Jsoup.parse(ncxFile.data.inputStream(), "UTF-8", "")
    val navMap = doc.selectFirst("navMap") ?: throw Exception("Invalid NCX file: navMap not found")

    val tocEntries = navMap.select("navPoint").map { navPoint ->
        val title =  navPoint.selectFirst("navLabel")?.selectFirst("text")?.text() ?: ""
        var link = navPoint.selectFirst("content")?.attribute("src")?.value ?: "" // Add the prefix
        if (!link.startsWith(rootPath))
            link = "$rootPath/$link"
        ToCEntry(title, link)
    }

    // Function to check if a spine item is a chapter
    fun isChapter(item: ManifestItem): Boolean {
        val extension = item.absPath.substringAfterLast('.')
        return listOf("xhtml", "xml", "html").contains(extension)
    }

    fun findTocEntryForChapter(tocEntries: List<ToCEntry>, chapterUrl: String): ToCEntry? {
        // Remove any potential fragment identifier from chapterUrl
        val chapterUrlWithoutFragment = chapterUrl.substringBefore('#')

        return tocEntries.firstOrNull {
            it.chapterLink.substringBefore('#').equals(chapterUrlWithoutFragment, ignoreCase = true)
        }
    }

    // Iterate through spine items
    val chapters = mutableListOf<Chapter>()
    var currentChapterIndex = 0
    var currentChapterBody = ""

    spine.selectChildTag("itemref").forEach { itemRef ->
        val itemId = itemRef.getAttribute("idref")
        val spineItem = manifestItems[itemId]

        // Check if the spine item exists and is a chapter
        if (spineItem != null && isChapter(spineItem)) {
            var chapterUrl = spineItem.absPath
            if (!chapterUrl.startsWith(rootPath))
                chapterUrl = "$rootPath/$chapterUrl"
            val tocEntry = findTocEntryForChapter(tocEntries, chapterUrl)

            val parser = EpubXMLFileParser(chapterUrl, files[chapterUrl]?.data ?: ByteArray(0), files)
            val res = parser.parseAsDocument()
//            Log.d("res", "${res.body}")
            // Append the chapter content to the current chapter body
            currentChapterBody += if (res.body.isBlank()) "" else "\n\n" + res.body
            // If the ToC entry exists, create a new chapter or append to the current one
            if (tocEntry != null) {
                if(spineItem.mediaType.startsWith("image/")) {

                    chapters.add(Chapter("image_${spineItem.absPath}", null, parser.parseAsImage(spineItem.absPath)))
                }
                else if (currentChapterBody.isNotEmpty()) {
                    chapters.add(Chapter(chapterUrl, tocEntry.chapterTitle, currentChapterBody))
                    currentChapterBody = ""
                }
                currentChapterIndex++
            }
        }
    }

    // Add the last chapter (only if it's a real chapter)
    val lastSpineItem = spine.selectChildTag("itemref").lastOrNull()?.let { manifestItems[it.getAttribute("idref")] }
    if (lastSpineItem != null && isChapter(lastSpineItem)) {
        val lastChapterUrl = lastSpineItem.absPath
        val lastChapterTitle = if (tocEntries.last().chapterLink == lastChapterUrl) tocEntries.last().chapterTitle else "Chapter ${currentChapterIndex + 1}"
        chapters.add(Chapter(lastChapterUrl, lastChapterTitle, currentChapterBody))
    }


    val imageExtensions =
        listOf("png", "gif", "raw", "png", "jpg", "jpeg", "webp", "svg").map { ".$it" }
    val unlistedImages = files
        .asSequence()
        .filter { (_, file) ->
            imageExtensions.any { file.absPath.endsWith(it, ignoreCase = true) }
        }
        .map { (_, file) ->
            Image(absPath = file.absPath, image = file.data)
        }

    val listedImages = manifestItems.asSequence()
        .map { it.value }
        .filter { it.mediaType.startsWith("image") }
        .mapNotNull { files[it.absPath] }
        .map { Image(absPath = it.absPath, image = it.data) }

    val images = (listedImages + unlistedImages).distinctBy { it.absPath }


    return@withContext EpubBook(
        fileName = metadataTitle.asFileName(),
        title = metadataTitle,
        author = metadataCreator,
        description = metadataDesc,
        coverImage = coverImage,
        chapters = chapters.toList(),
        images = images.toList(),
    )
}

