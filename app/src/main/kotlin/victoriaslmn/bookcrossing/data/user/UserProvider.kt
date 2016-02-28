package victoriaslmn.bookcrossing.data.user

import rx.Observable
import victoriaslmn.bookcrossing.domain.User

class UserProvider(val userApi: UserApi, val userCache: UserCache) {

    fun getCurrentUser(): Observable<User?> {
        return userCache.getCurrentUser()
                .map {
                    if (it == null) {
                        null
                    } else {
                        userMap(it)
                    }
                }
    }

    fun login(accessToken: String, userId: String): Observable<User> {
        return userApi.getUsers(userId).map { it.response?.single() }
                .doOnNext {
                    userCache.saveCurrentUser(it!!, accessToken)
                }.map { userMap(it!!) }
    }

    private fun userMap(dto: UserDto): User {
        return User(dto.id, "${dto.firstName} ${dto.lastName}", dto.photoBig ?: "")
    }

    fun logOut(): Observable<Void> {
        return userCache.removeCurrentUser()
    }

    fun getAccessToken(): Observable<String?> {
        return userCache.getCurrentUser().map { it?.accessToken }
    }
}
