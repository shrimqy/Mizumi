import com.dokja.mizumi.data.local.chapter.Chapter
import com.dokja.mizumi.data.local.chapter.ChapterBody
import com.dokja.mizumi.data.local.library.LibraryItem
import com.dokja.mizumi.epub.EpubBook
import com.dokja.mizumi.repository.AppFileResolver
import com.dokja.mizumi.repository.AppRepository
import com.dokja.mizumi.utils.fileImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

suspend fun epubImporter(
    storageFolderName: String,
    appRepository: AppRepository,
    appFileResolver: AppFileResolver,
    epub: EpubBook,
    addToLibrary: Boolean
): Unit = withContext(Dispatchers.IO) {
    val localBookUrl = appFileResolver.getLocalBookPath(storageFolderName)

    // First clean any previous entries from the book
    appRepository.bookChapters.chapters(localBookUrl)
        .map { it.url }
        .let { appRepository.chapterBody.removeRows(it) }
    appRepository.bookChapters.removeAllFromBook(localBookUrl)
    appRepository.libraryBooks.remove(localBookUrl)

    if (epub.coverImage != null) {
        fileImporter(
            targetFile = appFileResolver.getStorageBookCoverImageFile(storageFolderName),
            imageData = epub.coverImage.image
        )
    }

    // Insert new book data
    LibraryItem(
        title = storageFolderName,
        url = localBookUrl,
        coverImageUrl = appFileResolver.getLocalBookCoverPath(),
        inLibrary = addToLibrary
    ).let { appRepository.libraryBooks.insert(it) }

    epub.chapters.mapIndexed { i, chapter ->
        Chapter(
            title = chapter.title,
            url = appFileResolver.getLocalBookChapterPath(storageFolderName, chapter.absPath),
            bookUrl = localBookUrl,
            position = i
        )
    }.let { appRepository.bookChapters.insert(it) }

    epub.chapters.map { chapter ->
        ChapterBody(
            url = appFileResolver.getLocalBookChapterPath(storageFolderName, chapter.absPath),
            body = chapter.body
        )
    }.let { appRepository.chapterBody.insertReplace(it) }

    epub.images.map {
        async {
            fileImporter(
                targetFile = appFileResolver.getStorageBookImageFile(storageFolderName, it.absPath),
                imageData = it.image
            )
        }
    }.awaitAll()
}