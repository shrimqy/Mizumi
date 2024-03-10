package com.dokja.mizumi.epub

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream
import kotlin.io.path.invariantSeparatorsPathString

//Models
data class EpubChapter(
    val absPath: String,
    val title: String?,
    val body: String
)


data class EpubBook(
    val fileName: String,
    val title: String,
    val author: String?,
    val description: String?,
    val coverImage: EpubImage?,
    val chapters: List<EpubChapter>,
    val images: List<EpubImage>,
    val toc: List<ToCEntry> = emptyList()
)
data class EpubImage(val absPath: String, val image: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EpubImage

        if (absPath != other.absPath) return false
        return image.contentEquals(other.image)
    }

    override fun hashCode(): Int {
        var result = absPath.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}

data class EpubManifestItem(
    val id: String, val absPath: String, val mediaType: String, val properties: String
)


data class ToCEntry(
    val chapterTitle: String,
    val chapterLink: String)

data class TempEpubChapter(
    val url: String, val title: String?, var body: String
)

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
        EpubManifestItem(
            id = it.getAttribute("id"),
            absPath = it.getAttribute("href").decodedURL.hrefAbsolutePath(),
            mediaType = it.getAttribute("media-type"),
            properties = it.getAttribute("properties")
        )
    }.associateBy { it.id }



    fun parseCoverImageFromXhtml(coverFile: EpubFile): EpubImage? {
        val doc = Jsoup.parse(coverFile.data.inputStream(), "UTF-8", "")
        // Find the <img> tag within the XHTML file (adjust the selector if needed)
        val imgTag = doc.selectFirst("img")

        if (imgTag != null) {
            var imgSrc = imgTag.attribute("src").value.hrefAbsolutePath()
            if (!imgSrc.startsWith("$rootPath/")) {
                imgSrc = "$rootPath/$imgSrc" // Add the prefix
            }

            Log.d("Parser", "imgSrc: $imgSrc")
            val imgFile = files[imgSrc]


            if (imgFile != null) {
                return EpubImage(absPath = imgFile.absPath, image = imgFile.data)
            }
        }
        return null // Cover image not found in the XHTML
    }

    // 1. Primary Method: Try to get the cover image from the manifest
    var coverImage = manifestItems[metadataCoverId]
        ?.let { files[it.absPath] }
        ?.let { EpubImage(absPath = it.absPath, image = it.data) }
    Log.d("Parser", "coverImage: $coverImage")

    // 2. Fallback: Check the `<guide>` tag if the primary method didn't yield a cover
    if (coverImage == null) {
        var coverHref = guide?.selectChildTag("reference")
            ?.find { it.getAttribute("type") == "cover" }
            ?.getAttributeValue("href")?.decodedURL?.hrefAbsolutePath()

        if (guide == null) {
            val manifestCoverItem = manifestItems["cover"]
            coverHref = manifestCoverItem?.absPath
        }
        Log.d("Parser", "href: $coverHref")
        if (coverHref != null) {
            val coverFile = files[coverHref]
            Log.d("Parser", "coverFile: $coverFile")
            if (coverFile != null) {
                coverImage = parseCoverImageFromXhtml(coverFile)
            }
        }


    }
    Log.d("Parser", "final coverImage: $coverImage")


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
    fun isChapter(item: EpubManifestItem): Boolean {
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
    val chapters = mutableListOf<EpubChapter>()
    var currentChapterIndex = 0
    var currentChapterBody = ""

    spine.selectChildTag("itemref").forEach { itemRef ->
        val itemId = itemRef.getAttribute("idref")
        val spineItem = manifestItems[itemId]
        // Check if the spine item exists and is a chapter
        if (spineItem != null && isChapter(spineItem)) {
            var chapterUrl = spineItem.absPath
            Log.d("Parser", "chapter: $chapterUrl")
            if (!chapterUrl.startsWith(rootPath))
                chapterUrl = "$rootPath/$chapterUrl"
            val tocEntry = findTocEntryForChapter(tocEntries, chapterUrl)
            Log.d("Parser", "tocEntry: $tocEntries")
            // If the ToC entry exists, create a new chapter or append to the current one
            if (tocEntry != null) {
                if (currentChapterBody.isNotEmpty()) {
                    chapters.add(EpubChapter(chapterUrl, tocEntry.chapterTitle, currentChapterBody))
                    currentChapterBody = ""
                }
                currentChapterIndex++
            }

            val parser = EpubXMLFileParser(chapterUrl, files[chapterUrl]?.data ?: ByteArray(0), files)
            val res = parser.parseAsDocument()

            // Append the chapter content to the current chapter body
            currentChapterBody += if (res.body.isBlank()) "" else "\n\n" + res.body
        }
    }

    // Add the last chapter (only if it's a real chapter)
    val lastSpineItem = spine.selectChildTag("itemref").lastOrNull()?.let { manifestItems[it.getAttribute("idref")] }
    if (lastSpineItem != null && isChapter(lastSpineItem)) {
        val lastChapterUrl = lastSpineItem.absPath
        val lastChapterTitle = if (tocEntries.last().chapterLink == lastChapterUrl) tocEntries.last().chapterTitle else "Chapter ${currentChapterIndex + 1}"
        chapters.add(EpubChapter(lastChapterUrl, lastChapterTitle, currentChapterBody))
    }


    val imageExtensions =
        listOf("png", "gif", "raw", "png", "jpg", "jpeg", "webp", "svg").map { ".$it" }
    val unlistedImages = files
        .asSequence()
        .filter { (_, file) ->
            imageExtensions.any { file.absPath.endsWith(it, ignoreCase = true) }
        }
        .map { (_, file) ->
            EpubImage(absPath = file.absPath, image = file.data)
        }

    val listedImages = manifestItems.asSequence()
        .map { it.value }
        .filter { it.mediaType.startsWith("image") }
        .mapNotNull { files[it.absPath] }
        .map { EpubImage(absPath = it.absPath, image = it.data) }

    val images = (listedImages + unlistedImages).distinctBy { it.absPath }



    val coverImageBm: Bitmap? = if (coverImage?.image != null) {
        BitmapFactory.decodeByteArray(coverImage.image, 0, coverImage.image.size)
    } else {
        null
    }
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

class EpubXMLFileParser(
    fileAbsolutePath: String,
    val data: ByteArray,
    private val zipFile: Map<String, EpubFile>
) {
    data class Output(val body: String, val title: String?)

    val fileParentFolder: File = File(fileAbsolutePath).parentFile ?: File("")

    fun parseAsDocument(): Output {
        val body = Jsoup.parse(data.inputStream(), "UTF-8", "").body()
        val chapterTitle = body.selectFirst("h1, h2, h3, h4, h5, h6")?.text()
        body.selectFirst("h1, h2, h3, h4, h5, h6")?.remove()
        return Output(
            title = chapterTitle,
            body = getNodeStructuredText(body)
        )
    }


    fun parseAsImage(absolutePathImage: String): String {
        // Use run catching so it can be run locally without crash
        val bitmap = zipFile[absolutePathImage]?.data?.runCatching {
            BitmapFactory.decodeByteArray(this, 0, this.size)
        }?.getOrNull()

        val text = BookTextMapper.ImgEntry(
            path = absolutePathImage,
            yrel = bitmap?.let { it.height.toFloat() / it.width.toFloat() } ?: 1.45f
        ).toXMLString()

        return "\n\n$text\n\n"
    }

    // Rewrites the image node to xml for the next stage.
    private fun declareImgEntry(node: org.jsoup.nodes.Node): String {
        val attrs = node.attributes().associate { it.key to it.value }
        val relPathEncoded = attrs["src"] ?: attrs["xlink:href"] ?: ""

        val absolutePathImage = File(fileParentFolder, relPathEncoded.decodedURL)
            .canonicalFile
            .toPath()
            .invariantSeparatorsPathString
            .removePrefix("/")

        return parseAsImage(absolutePathImage)
    }

    private fun getPTraverse(node: org.jsoup.nodes.Node): String {
        fun innerTraverse(node: org.jsoup.nodes.Node): String =
            node.childNodes().joinToString("") { child ->
                when {
                    child.nodeName() == "br" -> "\n"
                    child.nodeName() == "img" -> declareImgEntry(child)
                    child.nodeName() == "image" -> declareImgEntry(child)
                    child is TextNode -> child.text()
                    else -> innerTraverse(child)
                }
            }

        val paragraph = innerTraverse(node).trim()
        return if (paragraph.isEmpty()) "" else innerTraverse(node).trim() + "\n\n"
    }

    private fun getNodeTextTraverse(node: org.jsoup.nodes.Node): String {
        val children = node.childNodes()
        if (children.isEmpty())
            return ""

        return children.joinToString("") { child ->
            when {
                child.nodeName() == "p" -> getPTraverse(child)
                child.nodeName() == "br" -> "\n"
                child.nodeName() == "hr" -> "\n\n"
                child.nodeName() == "img" -> declareImgEntry(child)
                child.nodeName() == "image" -> declareImgEntry(child)
                child is TextNode -> {
                    val text = child.text().trim()
                    if (text.isEmpty()) "" else text + "\n\n"
                }

                else -> getNodeTextTraverse(child)
            }
        }
    }

    private fun getNodeStructuredText(node: org.jsoup.nodes.Node): String {
        val children = node.childNodes()
        if (children.isEmpty())
            return ""

        return children.joinToString("") { child ->
            when {
                child.nodeName() == "p" -> getPTraverse(child)
                child.nodeName() == "br" -> "\n"
                child.nodeName() == "hr" -> "\n\n"
                child.nodeName() == "img" -> declareImgEntry(child)
                child.nodeName() == "image" -> declareImgEntry(child)
                child is TextNode -> child.text().trim()
                else -> getNodeTextTraverse(child)
            }
        }
    }
}


@Throws(Exception::class)
suspend fun epubCoverParser(
    inputStream: InputStream
): EpubImage? = withContext(Dispatchers.Default) {
    val files = getZipFiles(inputStream)

    val container = files["META-INF/container.xml"]
        ?: throw Exception("META-INF/container.xml file missing")

    val opfFilePath = parseXMLFile(container.data)
        ?.selectFirstTag("rootfile")
        ?.getAttributeValue("full-path")
        ?.decodedURL ?: throw Exception("Invalid container.xml file")

    val opfFile = files[opfFilePath] ?: throw Exception(".opf file missing")

    val document = parseXMLFile(opfFile.data)
        ?: throw Exception(".opf file failed to parse data")
    val metadata = document.selectFirstTag("metadata")
        ?: throw Exception(".opf file metadata section missing")
    val manifest = document.selectFirstTag("manifest")
        ?: throw Exception(".opf file manifest section missing")


    val metadataCoverId = metadata
        .selectChildTag("meta")
        .find { it.getAttributeValue("name") == "cover" }
        ?.getAttributeValue("content")

    val hrefRootPath = File(opfFilePath).parentFile ?: File("")
    fun String.hrefAbsolutePath() = File(hrefRootPath, this).canonicalFile
        .toPath()
        .invariantSeparatorsPathString
        .removePrefix("/")

    data class EpubManifestItem(
        val id: String,
        val absoluteFilePath: String,
        val mediaType: String,
        val properties: String
    )

    val manifestItems = manifest
        .selectChildTag("item").map {
            EpubManifestItem(
                id = it.getAttribute("id"),
                absoluteFilePath = it.getAttribute("href").decodedURL.hrefAbsolutePath(),
                mediaType = it.getAttribute("media-type"),
                properties = it.getAttribute("properties")
            )
        }.associateBy { it.id }

    manifestItems[metadataCoverId]
        ?.let { files[it.absoluteFilePath] }
        ?.let { EpubImage(absPath = it.absPath, image = it.data) }
}