package victoriaslmn.bookcrossing.data.user

import retrofit.http.GET
import retrofit.http.Query
import rx.Observable
import victoriaslmn.bookcrossing.data.common.Fields
import victoriaslmn.bookcrossing.data.common.PagingResponse
import victoriaslmn.bookcrossing.data.common.VkResponse

interface UserApi {
    @GET("users.get?fields=${Fields.PHOTO_200},${Fields.PHOTO_100}")
    fun getUsers(@Query("user_ids") userIds: String): Observable<VkResponse<List<UserDto>>>

    @GET("friends.get?fields=${Fields.PHOTO_200},${Fields.PHOTO_100}")
    fun getFriends(@Query("user_id") userId: Long,
                   @Query("count") count: Int,
                   @Query("offset") offset: Int): Observable<VkResponse<PagingResponse<UserDto>>>
}
