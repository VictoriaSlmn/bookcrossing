package victoriaslmn.bookcrossing.data.document

import com.j256.ormlite.dao.Dao
import rx.Observable
import victoriaslmn.bookcrossing.data.user.UserDto
import victoriaslmn.bookcrossing.domain.Book

class DocumentsCache(val documentsDao: Dao<DocumentDto, Long>) {
    fun getMyDocuments(): Observable<List<DocumentDto>> {
        return Observable.just(documentsDao.queryForAll());
    }

    fun updateDocument(dto: DocumentDto): Boolean {
        val status = documentsDao.createOrUpdate(dto)
        return status.isCreated || status.isUpdated
    }
}