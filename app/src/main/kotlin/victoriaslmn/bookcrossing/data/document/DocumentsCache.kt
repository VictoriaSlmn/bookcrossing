package victoriaslmn.bookcrossing.data.document

import android.content.Context
import android.os.Environment
import com.j256.ormlite.dao.Dao
import rx.Observable
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
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
                dto.localURI = loadAndSaveFileInDocumentDir(dto)
                updateDocument(dto)
                it.onNext(dto)
            } catch (e: Exception) {
                it.onError(e)
            } finally {
                it.onCompleted()
            }
        })
    }

    private fun loadAndSaveFileInDocumentDir(dto: DocumentDto): String {
        val u = URL(dto.url)
        val conn = u.openConnection()
        val contentLength = conn.getContentLength()

        val stream = DataInputStream(u.openStream())

        val buffer = ByteArray(contentLength)
        stream.readFully(buffer)
        stream.close()

        val dir = File(Environment.getExternalStorageDirectory(), "Bookcrossing")
        val dirExists = dir.exists() || dir.mkdirs()

        if (!dirExists) {
            throw RuntimeException("Couldn't create folder in external storage")
        }

        val documentFile = File(dir, dto.title)

        val fos = FileOutputStream(documentFile)
        fos.write(buffer)
        fos.close()

        return documentFile.absolutePath
    }

}