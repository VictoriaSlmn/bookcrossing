package victoriaslmn.bookcrossing.view.common

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast
import rx.Notification
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

abstract class BasePresenter{
    fun <T> Observable<T>.execute(function: (Notification<T>) -> Unit) {
        this.materialize()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(function)
    }

    fun showError(@StringRes message: Int, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    abstract fun init()
}
