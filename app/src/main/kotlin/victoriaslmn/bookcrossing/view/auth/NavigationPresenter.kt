package victoriaslmn.bookcrossing.view.auth

import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.api.VKError
import rx.Notification
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import victoriaslmn.bookcrossing.MainActivity
import victoriaslmn.bookcrossing.R
import victoriaslmn.bookcrossing.data.user.UserProvider
import victoriaslmn.bookcrossing.domain.User
import victoriaslmn.bookcrossing.view.CircleTransform

class NavigationPresenter(activity: MainActivity, userProvider: UserProvider) {
    val userProvider = userProvider;
    val mainActivity = activity;
    val navView: NavigationView;
    val header: View;

    init {
        navView = activity.findViewById(R.id.nav_view) as NavigationView
        header = navView.getHeaderView(0)
        navView.setNavigationItemSelectedListener {
            when (it.getItemId()) {
                R.id.nav_exit -> logout()
                R.id.nav_folders -> {
                }
                R.id.nav_friends -> {
                }
                R.id.nav_main -> {
                }
                R.id.nav_recommendations -> {
                }
            }
            mainActivity.closeDrawer(GravityCompat.START)
            true
        }

        initNavView();
    }

    private fun initNavView(){
        userProvider
                .getCurrentUser()
                .execute {
                    when (it.kind) {
                        Notification.Kind.OnError -> mainActivity.showError(R.string.auth_error)
                        Notification.Kind.OnNext -> resolveNavView(it.value)
                        else -> {
                        }
                    }
                }
    }

    private fun logout(){
        userProvider.logOut()
                .execute {
                    when (it.kind) {
                        Notification.Kind.OnError -> mainActivity.showError(R.string.logout_error)
                        Notification.Kind.OnCompleted -> {
                            resolveNavView(null)
                        }
                        else -> {
                        }
                    }
                }
    }

    private fun resolveNavView(user: User?) {
        val userPhoto = header.findViewById(R.id.imageView) as ImageView;
        val userName = header.findViewById(R.id.textView) as TextView;
        val menu = navView.getMenu()
        menu.clear()
        if(user == null){
            Picasso.with(mainActivity)
                    .load(R.drawable.ic_account_circle_white_48dp)
                    .into(userPhoto)
            userName.setText(R.string.enter)
            mainActivity.getMenuInflater().inflate(R.menu.activity_main_drawer, menu)
            header.setOnClickListener{ mainActivity.openVKAuthActivity()}
        }else{
            Picasso.with(mainActivity)
                    .load(user.photo)
                    .transform(CircleTransform())
                    .into(userPhoto)
            userName.setText(user.name)
            mainActivity.getMenuInflater().inflate(R.menu.activity_main_auth_drawer, menu)
            header.setOnClickListener(null)
        }
    }

    fun authCallback(): AuthCallback {
        return AuthCallback();
    }

    inner class AuthCallback : VKCallback<VKAccessToken> {
        override fun onResult(res: VKAccessToken) {
            userProvider.login(res.accessToken, res.userId)
                    .execute {
                        when (it.kind) {
                            Notification.Kind.OnError -> mainActivity.showError(R.string.auth_error)
                            Notification.Kind.OnNext -> resolveNavView(it.value)
                            else -> {
                            }
                        }
                    }
        }

        override fun onError(error: VKError) {
            mainActivity.showError(R.string.auth_error);
        }
    }
}

fun <T> Observable<T>.execute(function: (Notification<T>) -> Unit) {
    this.materialize()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(function)
}
