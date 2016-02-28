package victoriaslmn.bookcrossing.data.document

import rx.Observable
import victoriaslmn.bookcrossing.data.common.PagingResponse
import victoriaslmn.bookcrossing.data.common.VkResponse
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.domain.BookFilter

const val PAGE_SIZE = 20;

class BookProvider(val documentsApi: DocumentsApi, val documentsCache: DocumentsCache) {

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


    fun Observable<List<DocumentDto>>.mapToBook(): Observable<List<Book>> {
        return this.map {
            it.map {
                Book(it.id,
                        it.title ?: "no name",
                        getFormat(it.ext),
                        it.dir,
                        it.url)
            }
        }
    }

    private fun getFormat(ext: String?): Book.Format {
        when (ext) {
            "txt" -> return Book.Format.TXT
            "rtf" -> return  Book.Format.RTF
            "pdf" -> return Book.Format.PDF
            "fb2" -> return Book.Format.FB2
            "epub" -> return Book.Format.EPUB
            "doc" -> return Book.Format.DOC
            "docx" -> return Book.Format.DOCX
        }
        return Book.Format.NONAME
    }
}

