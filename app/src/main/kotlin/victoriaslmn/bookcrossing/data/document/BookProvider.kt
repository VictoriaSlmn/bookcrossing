package victoriaslmn.bookcrossing.data.document

import rx.Observable
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.domain.User

class BookProvider(val documentsApi: DocumentsApi, val documentsCache: DocumentsCache) {

    val PAGE_SIZE = 20

    fun searchBooks(mask: String, page: Int, accessToken: String?): Observable<List<Book>> { //todo Page Object
        val findInCache = documentsCache.getMyDocuments()
                .flatMapIterable { it }
                .filter { it.title?.contains(mask, true) } //todo delete duplicates
                .toList()

        if (accessToken == null) {
            return findInCache.mapToBook()
        }
        return Observable.zip(documentsApi
                .searchDocuments(mask, PAGE_SIZE, PAGE_SIZE * page, accessToken)
                .map { it.response?.items ?: emptyList<DocumentDto>() }
                .onExceptionResumeNext(Observable.just(emptyList())),
                findInCache,
                mergeApiAndCacheBook { fromApi, fromCache -> fromApi.title == fromCache.title }).mapToBook()
    }

    fun getBooksByUser(user: User?, page: Int, accessToken: String?): Observable<List<Book>> {//todo Page Object
        val myDocuments = documentsCache.getMyDocuments()
        if (user == null || accessToken == null) {
            return myDocuments.mapToBook()
        }

        return Observable.zip(
                documentsApi.getDocumentsByUser(user.id, PAGE_SIZE, PAGE_SIZE * page, accessToken)
                        .map { it.response?.items!! }
                        .onExceptionResumeNext(Observable.just(emptyList())),
                myDocuments,
                mergeApiAndCacheBook { fromApi, fromCache -> fromApi.id == fromCache.id })
                .flatMapIterable { it -> it }
                .doOnNext { documentsCache.updateDocument(it) }
                .toList()
                .mapToBook()
    }

    fun mergeApiAndCacheBook(predicate: (DocumentDto, DocumentDto) -> Boolean): (List<DocumentDto>, List<DocumentDto>) -> List<DocumentDto> {
        val mergeApiAndCacheBook: (List<DocumentDto>, List<DocumentDto>) -> List<DocumentDto> = {
            fromApi, fromCache ->
            if (fromApi.isEmpty()) {
                fromCache
            } else {
                fromApi.map {
                    fromApi ->
                    val sameFromCache = fromCache.findLast({ predicate(fromApi, it) })
                    if (sameFromCache != null) {
                        fromApi.localURI = sameFromCache.localURI
                    }
                    fromApi
                }
            }
        }

        return mergeApiAndCacheBook
    }

    fun downloadBook(book: Book, user: User, accessToken: String): Observable<Book> {
        val document: Observable<DocumentDto>

        if (book.ownerId == user.id) {
            document = documentsCache.getDocumentById(book.id)
                    .switchIfEmpty(documentFromServerById("${user.id}_${book.id}", accessToken))
        } else {
            document = documentsApi.addDocument(book.ownerId, book.id, book.accessKey, accessToken)
                    .flatMap { documentFromServerById("${user.id}_${it.response!!}", accessToken) }
        }

        return document
                .flatMap { documentsCache.saveInInternalStorage(it) }
                .map { mapToBook(it) }
    }

    private fun documentFromServerById(id: String, accessToken: String): Observable<DocumentDto> {
        return documentsApi.getDocumentsById(setOf(id), accessToken).map { it.response?.first() }
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
                it.localURI,
                it.url,
                it.accessKey)
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

