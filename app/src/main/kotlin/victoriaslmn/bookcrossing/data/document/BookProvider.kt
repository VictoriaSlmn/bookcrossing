package victoriaslmn.bookcrossing.data.document

import android.os.Environment
import rx.Observable
import victoriaslmn.bookcrossing.data.common.PagingResponse
import victoriaslmn.bookcrossing.data.common.VkResponse
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.domain.BookFilter
import java.io.*
import java.net.URL

class BookProvider(val documentsApi: DocumentsApi, val documentsCache: DocumentsCache) {

    val PAGE_SIZE = 20;

    fun getBooks(bookFilter: BookFilter): Observable<List<Book>> {
        val downloadedDocuments = documentsCache.getDownloadedDocuments(bookFilter).cache();
        if (bookFilter.mask == null || bookFilter.mask.length < 3) {
            return downloadedDocuments.mapToBook()
        }
        return Observable.zip(downloadedDocuments,
                documentsApi
                        .searchDocuments(bookFilter.mask, PAGE_SIZE, PAGE_SIZE * bookFilter.page, bookFilter.accessToken),
                { downloaded: List<DocumentDto>, finded: VkResponse<PagingResponse<DocumentDto>> ->
                    val fromVkDocuments = finded.response?.items;
                    if (fromVkDocuments == null) {
                        downloaded
                    } else {
                        downloaded.plus(fromVkDocuments)
                    }
                }).mapToBook()
    }

    fun downloadBook(book: Book): Observable<Book> {
        documentsApi.addDocument(book.ownerId, book.id, null /*todo asseskey*/)
        //   .map { Book() } todo
        //   return Observable.zip()

        return Observable.empty<Book>()
    }

    private fun saveInInternalStorage(book: Book): Observable<String> {
        if (book.remoteURI == null) {
            return Observable.empty()
        }
        return Observable.create(Observable.OnSubscribe<String> {
            val localURI = Environment.getExternalStorageDirectory().toString() + "/bookcrossing/" + book.title;
            try {
                saveInInternalStorage(localURI, book.remoteURI)
                it.onNext(localURI)
            } catch (e: Exception) {
                it.onError(e)
            } finally {
                it.onCompleted()
            }
        });
    }

    private fun saveInInternalStorage(localURI: String, remoteURI: String) {
        val u = URL(remoteURI);
        val conn = u.openConnection();
        val contentLength = conn.getContentLength();

        val stream = DataInputStream(u.openStream())

        val buffer = ByteArray(contentLength)
        stream.readFully(buffer)
        stream.close()

        val fos = DataOutputStream(FileOutputStream(File(localURI)))
        fos.write(buffer)
        fos.flush()
        fos.close()
    }


    private fun Observable<List<DocumentDto>>.mapToBook(): Observable<List<Book>> {
        return this.map {
            it.map {
                Book(it.id,
                        it.title ?: "no name",
                        getFormat(it.ext),
                        it.owerId,
                        it.dir,
                        it.url)
            }.filter { !it.format.equals(Book.Format.NONAME) }
        }
    }

    private fun convert(book: Book, localURI: String): DocumentDto {
        val dto = DocumentDto()
        dto.url = book.remoteURI;
        dto.accessKey
        dto.date
        dto.dir = localURI;
        dto.ext
        dto.folder = "main";//todo default folder
        dto.owerId //todo I'm
        dto.title = book.title
        dto.id = book.id
        dto.size
        dto.type
        return dto;
    }


    private fun getFormat(ext: String?): Book.Format {
        when (ext) {
            "txt" -> return Book.Format.TXT
            "rtf" -> return Book.Format.RTF
            "pdf" -> return Book.Format.PDF
            "fb2" -> return Book.Format.FB2
            "epub" -> return Book.Format.EPUB
            "doc" -> return Book.Format.DOC
            "docx" -> return Book.Format.DOCX
        }
        return Book.Format.NONAME
    }
}

