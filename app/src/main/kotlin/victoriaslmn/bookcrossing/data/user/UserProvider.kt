package victoriaslmn.bookcrossing.data.user

import rx.Observable
import victoriaslmn.bookcrossing.domain.User

class UserProvider(userApi: UserApi, userCache: UserCache) {
    private val api = userApi;
    private val cache = userCache;

    fun getCurrentUser(): Observable<User?> {
        return cache.getCurrentUser()
                .map {
                    if (it == null) {
                        null
                    } else {
                        userMap(it)
                    }
                }
    }

    fun login(accessToken: String, userId: String): Observable<User> {
        return api.getUsers(userId).map { it.response?.single() }
                .doOnNext {
                    cache.saveCurrentUser(it!!, accessToken)
                }.map { userMap(it!!) }
    }

    fun userMap(dto: UserDto): User {
        return User(dto.id, "${dto.firstName} ${dto.lastName}", dto.photoBig)
    }

    fun logOut(): Observable<Void> {
        return cache.removeCurrentUser()
    }
}
