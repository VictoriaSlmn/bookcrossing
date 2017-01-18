package victoriaslmn.bookcrossing.data.document

import com.google.gson.Gson
import retrofit.http.GET
import retrofit.http.Query
import rx.Observable
import victoriaslmn.bookcrossing.data.common.PagingResponse
import victoriaslmn.bookcrossing.data.common.VkResponse
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

interface DocumentsApi {

    @GET("docs.search")
    fun searchDocuments(@Query("q") mask: String,
                        @Query("count") count: Int,
                        @Query("offset") offset: Int,
                        @Query("access_token") accessToken: String,
                        @Query("v") version: String = "5.45"): Observable<VkResponse<PagingResponse<DocumentDto>>>

    @GET("docs.getById")
    fun getDocumentsById(@Query("docs") ownerId_docId: Set<String>,
                         @Query("access_token") accessToken: String,
                         @Query("v") version: String = "5.45"): Observable<VkResponse<List<DocumentDto>>>

    @GET("docs.get")
    fun getDocumentsByUser(@Query("owner_id") ownerId: Long,
                           @Query("count") count: Int,
                           @Query("offset") offset: Int,
                           @Query("access_token") accessToken: String,
                           @Query("v") version: String = "5.45"): Observable<VkResponse<PagingResponse<DocumentDto>>>

    @GET("docs.getUploadServer")
    fun getUploadServer(@Query("access_token") accessToken: String): Observable<VkResponse<UploadServerDto>>


    @GET("docs.save")
    fun saveDocument(@Query("file") file: String,
                     @Query("title") title: String,
                     @Query("access_token") accessToken: String,
                     @Query("tags") tags: String? = null): Observable<VkResponse<List<DocumentDto>>>

    @GET("docs.delete")
    fun deleteDocument(@Query("owner_id") ownerId: Long,
                       @Query("doc_id") docId: Long,
                       @Query("access_token") accessToken: String): Observable<VkResponse<Int>>


    @GET("docs.edit")
    fun editDocument(@Query("owner_id") ownerId: Long,
                     @Query("doc_id") docId: Long,
                     @Query("title") title: String,
                     @Query("access_token") accessToken: String,
                     @Query("tags") tags: String?): Observable<VkResponse<Int>>

    @GET("docs.add")
    fun addDocument(@Query("owner_id") ownerId: Long,
                    @Query("doc_id") docId: Long,
                    @Query("access_key") accessKey: String?,
                    @Query("access_token") accessToken: String,
                    @Query("v") version: String = "5.45"): Observable<VkResponse<Long>>

}

object DocumentsUpload {
    fun uploadFile(selectedFile: File, serverUrl: String): Observable<UploadFileDto> {
        return Observable.create<UploadFileDto> {
            try {
                val response = uploadFile0(selectedFile, serverUrl)
                val dto = Gson().fromJson<UploadFileDto>(response, UploadFileDto::class.java)
                it.onNext(dto)
                it.onCompleted()
            } catch (t: Throwable) {
                it.onError(t)
            }
        }
    }


    fun uploadFile0(selectedFile: File, serverUrl: String): String {
        val responseBody = StringBuffer()

        if (!selectedFile.isFile()) {
            throw RuntimeException("It not file")
        } else {
            try {
                val fileInputStream = FileInputStream(selectedFile);
                val url = URL(serverUrl);
                val lineEnd = "\r\n"
                val twoHyphens = "--"
                val boundary = "*****"

                val maxBufferSize = 1 * 1024 * 1024

                val connection = url.openConnection() as HttpURLConnection
                connection.setDoInput(true)
                connection.setDoOutput(true)
                connection.setUseCaches(false)
                connection.setRequestMethod("POST")
                connection.setRequestProperty("Connection", "Keep-Alive")
                connection.setRequestProperty("ENCTYPE", "multipart/form-data")
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary)
                connection.setRequestProperty("file", selectedFile.absolutePath);

                val dataOutputStream = DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + selectedFile.absolutePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                var bytesAvailable = fileInputStream.available()
                var bufferSize = Math.min(bytesAvailable, maxBufferSize)
                val buffer = ByteArray(bufferSize)

                var bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dataOutputStream.write(buffer, 0, bufferSize)
                    bytesAvailable = fileInputStream.available()
                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                }

                dataOutputStream.writeBytes(lineEnd)
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    throw RuntimeException("HTTP code: " + responseCode)
                }

                val inputStream = BufferedReader(
                        InputStreamReader(connection.getInputStream()))
                var inputLine: String? = inputStream.readLine()

                while (inputLine != null) {
                    responseBody.append(inputLine)
                    inputLine = inputStream.readLine()
                }

                inputStream.close()
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (e: FileNotFoundException) {
                throw RuntimeException("File Not Found")
            } catch (e: MalformedURLException) {
                throw RuntimeException("URL error!")

            } catch (e: IOException) {
                throw RuntimeException("Cannot Read/Write File!")
            }

            return responseBody.toString()
        }

    }
}

