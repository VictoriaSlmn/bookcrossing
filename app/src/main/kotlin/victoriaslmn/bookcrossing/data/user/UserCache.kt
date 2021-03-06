package victoriaslmn.bookcrossing.data.user

import com.j256.ormlite.dao.Dao
import rx.Observable

class UserCache(val userDao: Dao<UserDto, Long>) {

    fun getCurrentUser(): Observable<UserDto?> {
        return Observable.create<UserDto> {
            val users = getCurrent();
            it.onNext(users.singleOrNull())
            it.onCompleted()
        }
    }

    fun saveCurrentUser(currentUser: UserDto, accessToken: String) {
        clearCurrent();
        currentUser.accessToken = accessToken;
        currentUser.current = true;
        userDao.createOrUpdate(currentUser)
    }

    private fun getCurrent(): MutableList<UserDto> {
        return userDao.query(userDao
                .queryBuilder().where()
                .eq(UserDto.Field.CURRENT, true)
                .prepare());
    }

    private fun clearCurrent(){
        getCurrent().forEach {
            it.accessToken = "";
            it.current = false;
            userDao.update(it)
        }
    }

    fun removeCurrentUser(): Observable<Void> {
        return Observable.create {
            clearCurrent();
            it.onCompleted()
        }
    }

}
