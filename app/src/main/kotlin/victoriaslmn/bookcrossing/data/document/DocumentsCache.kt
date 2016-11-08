package victoriaslmn.bookcrossing.data.document

import android.content.Context
import com.j256.ormlite.dao.Dao
import rx.Observable
import java.io.DataInputStream
import java.net.URL

class DocumentsCache(val documentsDao: Dao<DocumentDto, Long>, val context: Context) {
    fun getMyDocuments(): Observable<List<DocumentDto>> {
        return Observable.just(documentsDao.queryForAll());
    }

    fun updateDocument(dto: DocumentDto): Boolean {
        val status = documentsDao.createOrUpdate(dto)
        return status.isCreated || status.isUpdated
    }

    fun getDocumentById(id: Long): Observable<DocumentDto> {
        return Observable.just(documentsDao.queryForId(id))
    }

    fun saveInInternalStorage(dto: DocumentDto): Observable<DocumentDto> {
        if (dto.url == null) {
            return Observable.empty()
        }

        return Observable.create({
            try {
                loadAndSaveFileInInternalStorage(dto)
                dto.downloaded = true
                updateDocument(dto)
                it.onNext(dto)
            } catch (e: Exception) {
                it.onError(e)
            } finally {
                it.onCompleted()
            }
        })
    }

    private fun loadAndSaveFileInInternalStorage(dto: DocumentDto) {
        val u = URL(dto.url)
        val conn = u.openConnection()
        val contentLength = conn.getContentLength()

        val stream = DataInputStream(u.openStream())

        val buffer = ByteArray(contentLength)
        stream.readFully(buffer)
        stream.close()

        val fOut = context.openFileOutput(dto.title, Context.MODE_PRIVATE)
        fOut.write(buffer)
        fOut.close()
    }
}