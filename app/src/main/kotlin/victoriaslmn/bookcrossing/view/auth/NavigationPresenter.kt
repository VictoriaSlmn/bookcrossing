package victoriaslmn.bookcrossing.view.auth

import android.support.design.widget.NavigationView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.api.VKError
import rx.Notification
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
    var user: User? = null;

    init {
        navView = activity.findViewById(R.id.nav_view) as NavigationView
        navView.setNavigationItemSelectedListener {
            false ///todo
        }

        header = navView.getHeaderView(0)
        userProvider
                .getCurrentUser()
                .materialize()
                .subscribeOn(Schedulers.newThread())///todo
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it.kind) {
                        Notification.Kind.OnError -> mainActivity.showError(R.string.auth_error)
                        Notification.Kind.OnNext ->
                            if (it.value.isEmpty()) {
                                header.setOnClickListener {
                                    view ->
                                    auth()
                                }
                            } else {
                                resolveNavView(it.value)
                            }
                        else -> {
                        }
                    }
                }
    }

    fun auth() {
        mainActivity.openVKAuthActivity()
    }

    private fun resolveNavView(user: User) {
        this.user = user
        val userPhoto = header.findViewById(R.id.imageView) as ImageView;
        val userName = header.findViewById(R.id.textView) as TextView;
        Picasso.with(mainActivity)
                .load(user.photo)
                .transform(CircleTransform()).into(userPhoto)
        userName.setText(user.name)
        val menu = navView.getMenu()
        menu.clear()
        mainActivity.getMenuInflater().inflate(R.menu.activity_main_auth_drawer, menu)
        header.setOnClickListener(null)
    }

    fun authCallback(): AuthCallback {
        return AuthCallback();
    }

    inner class AuthCallback : VKCallback<VKAccessToken> {
        override fun onResult(res: VKAccessToken) {
            userProvider.login(res.accessToken, res.userId)
                    .materialize()
                    .subscribeOn(Schedulers.newThread())///todo
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
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
