package victoriaslmn.bookcrossing.data.user

import com.j256.ormlite.dao.Dao
import rx.Observable

class UserCache(userDao: Dao<UserDto, String>) {
    private val userDao = userDao;

    fun getCurrentUser(): Observable<UserDto?> {
        return Observable.create<UserDto> {
            val users = getCurrent();
            it.onNext(users.singleOrNull())
            it.onCompleted()
        }
    }

    fun saveCurrentUser(currentUser: UserDto, accessToken: String) {
        getCurrent().forEach {
            it.accessToken = "";
            it.current = false;
            userDao.update(it)
        }
        currentUser.accessToken = accessToken;
        currentUser.current = true;
        userDao.createOrUpdate(currentUser)
    }

    fun getCurrent(): MutableList<UserDto> {
        return userDao.query(userDao
                .queryBuilder().where()
                .eq(UserDto.Field.CURRENT, true)
                .prepare());
    }

}
