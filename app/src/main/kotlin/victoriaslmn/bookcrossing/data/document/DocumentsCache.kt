package victoriaslmn.bookcrossing.data.document

import com.j256.ormlite.dao.Dao
import rx.Observable
import victoriaslmn.bookcrossing.data.user.UserDto
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.domain.BookFilter

class DocumentsCache(val documentsDao: Dao<DocumentDto, Long>) {
    fun getDownloadedDocuments(bookFilter: BookFilter): Observable<List<DocumentDto>> {
        //todo filter
        return Observable.just(documentsDao.queryForAll());
    }

    fun setDocumentDownload(dto: DocumentDto): Boolean {
        return documentsDao.update(dto) == 1
    }
}