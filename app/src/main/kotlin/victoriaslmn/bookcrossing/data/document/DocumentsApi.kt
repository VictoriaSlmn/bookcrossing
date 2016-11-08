package victoriaslmn.bookcrossing.data.document

import retrofit.http.GET
import retrofit.http.Query
import rx.Observable
import victoriaslmn.bookcrossing.data.common.PagingResponse
import victoriaslmn.bookcrossing.data.common.VkResponse

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
    fun getUploadServer(): Observable<VkResponse<UploadServerDto>>
    //POST
    // request: multipart/form-data
    // <input type="file" name="file" onchange="this.parentNode.submit(); show('dev_upload_iframe_wrap')" class="dev_upload_input">
    // response:{"file":"66748|0|0|10666|e73d7a2192|gif|1165|mono-iconset2.gif|c57c09d9758f82914a00d466b0717d8a|29043211ee803fe3f3d1808d58a2b3cc"}

    @GET("docs.save")
    fun saveDocument(@Query("file") file: String,
                     @Query("title") title: String,
                     @Query("tags") tags: String?): Observable<VkResponse<List<DocumentDto>>>

    @GET("docs.delete")
    fun deleteDocument(@Query("owner_id") ownerId: Long,
                       @Query("doc_id") docId: Long): Observable<VkResponse<Int>>


    @GET("docs.edit")
    fun editDocument(@Query("owner_id") ownerId: Long,
                     @Query("doc_id") docId: Long,
                     @Query("title") title: String,
                     @Query("tags") tags: String?): Observable<VkResponse<Int>>

    @GET("docs.add")
    fun addDocument(@Query("owner_id") ownerId: Long,
                    @Query("doc_id") docId: Long,
                    @Query("access_key") accessKey: String?,
                    @Query("access_token") accessToken: String,
                    @Query("v") version: String = "5.45"): Observable<VkResponse<Long>>
}
