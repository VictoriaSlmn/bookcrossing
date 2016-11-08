package victoriaslmn.bookcrossing.data.document

import rx.Observable
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.domain.User

class BookProvider(val documentsApi: DocumentsApi, val documentsCache: DocumentsCache) {

    val PAGE_SIZE = 20;

    fun searchBooks(mask: String, page: Int, accessToken: String?): Observable<List<Book>> { //todo Page Object
        if (accessToken == null) {
            return documentsCache.getMyDocuments()
                    .flatMapIterable { it }
                    .filter { it.title?.contains(mask, true) } //todo delete duplicates
                    .toList()
                    .mapToBook()
        }
        return documentsApi
                .searchDocuments(mask, PAGE_SIZE, PAGE_SIZE * page, accessToken)
                .map { it.response?.items ?: emptyList<DocumentDto>() }
                .onExceptionResumeNext(documentsCache.getMyDocuments()
                        .flatMapIterable { it }
                        .filter { it.title?.contains(mask, true) }//todo delete duplicates
                        .toList())
                .mapToBook()
    }

    fun getBooksByUser(user: User?, page: Int, accessToken: String?): Observable<List<Book>> {//todo Page Object
        if (user == null || accessToken == null) {
            return documentsCache.getMyDocuments().mapToBook()//todo delete duplicates
        }
        return documentsApi.getDocumentsByUser(user.id, PAGE_SIZE, PAGE_SIZE * page, accessToken)
                .flatMapIterable { it.response?.items!! }
                .doOnNext { documentsCache.updateDocument(it) }
                .toList()
                .onExceptionResumeNext(documentsCache.getMyDocuments())
                .mapToBook()
    }

    fun downloadBook(book: Book, user: User, accessToken: String): Observable<Book> {
        val document: Observable<DocumentDto>

        if (book.ownerId == user.id) {
            document = documentsCache.getDocumentById(book.id)
                    .switchIfEmpty(documentFromServerById(book.id, accessToken))
        } else {
            document = documentsApi.addDocument(book.ownerId, book.id, accessToken)
                    .flatMap { documentFromServerById(it.response!!, accessToken) }

        }

        return document
                .flatMap { documentsCache.saveInInternalStorage(it) }
                .map { mapToBook(it) }
    }

    private fun documentFromServerById(id: Long, accessToken: String): Observable<DocumentDto> {
        return documentsApi.getDocumentsById(setOf(id), accessToken).map { it.response?.items?.first() }
    }


    private fun Observable<List<DocumentDto>>.mapToBook(): Observable<List<Book>> {
        return this.map {
            it.map { mapToBook(it) }.filter { it.format != Book.Format.NONAME }
        }
    }

    private fun mapToBook(it: DocumentDto): Book {
        return Book(it.id,
                it.title ?: "no name",
                getFormat(it.ext),
                it.owerId,
                it.downloaded,
                it.url)
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

