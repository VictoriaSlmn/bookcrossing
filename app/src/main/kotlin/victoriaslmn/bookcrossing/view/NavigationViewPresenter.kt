package victoriaslmn.bookcrossing.view

import android.content.res.ColorStateList
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.api.VKError
import rx.Notification
import victoriaslmn.bookcrossing.MainActivity
import victoriaslmn.bookcrossing.R
import victoriaslmn.bookcrossing.data.user.UserProvider
import victoriaslmn.bookcrossing.domain.User
import victoriaslmn.bookcrossing.view.common.BasePresenter
import victoriaslmn.bookcrossing.view.common.CircleTransform

class NavigationViewPresenter(val activity: MainActivity,
                              val userProvider: UserProvider,
                              val navigationItemSelectedListener: NavigationView.OnNavigationItemSelectedListener,
                              val authAction: () -> Unit) : BasePresenter() {

    val drawerLayout: DrawerLayout
    val navView: NavigationView
    val header: View

    val authCallback = AuthCallback()

    init {
        drawerLayout = activity.findViewById(R.id.drawer_layout) as DrawerLayout
        navView = activity.findViewById(R.id.nav_view) as NavigationView
        header = navView.getHeaderView(0)
        initNavView()
    }

    override fun init() {
        userProvider
                .getCurrentUser()
                .execute {
                    when (it.kind) {
                        Notification.Kind.OnError -> showError(R.string.auth_error, activity)
                        Notification.Kind.OnNext -> resolveNavView(it.value)
                        else -> {
                        }
                    }
                }
    }

    private fun initNavView() {
        val toolbar = activity.findViewById(R.id.toolbar) as Toolbar
        activity.setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.setDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(navigationItemSelectedListener);
    }


    private fun resolveNavView(user: User?) {
        val userPhoto = header.findViewById(R.id.imageView) as ImageView;
        val userName = header.findViewById(R.id.textView) as TextView;
        val menu = navView.getMenu()
        menu.clear()
        if (user == null) {
            userPhoto.imageTintList = ColorStateList.valueOf(userPhoto.context.getColor(R.color.vk_white))
            userPhoto.setImageResource(R.drawable.ic_account_circle_black_24dp)
            userName.setText(R.string.enter)
            activity.getMenuInflater().inflate(R.menu.activity_main_drawer, menu)
            header.setOnClickListener { activity.openVKAuthActivity() }
        } else {
            userPhoto.imageTintList = null
            if (!user.photo.isNullOrEmpty()) {
                Picasso.with(activity)
                        .load(user.photo)
                        .transform(CircleTransform())
                        .into(userPhoto)
            }
            userName.setText(user.name)
            activity.getMenuInflater().inflate(R.menu.activity_main_auth_drawer, menu)
            header.setOnClickListener(null)
        }
        authAction.invoke()
    }

    fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }


    fun logout() {
        userProvider.logOut()
                .execute {
                    when (it.kind) {
                        Notification.Kind.OnError -> showError(R.string.logout_error, activity)
                        Notification.Kind.OnCompleted -> {
                            resolveNavView(null)
                        }
                        else -> {
                        }
                    }
                }
    }

    inner class AuthCallback : VKCallback<VKAccessToken> {
        override fun onResult(res: VKAccessToken) {
            userProvider.login(res.accessToken, res.userId)
                    .execute {
                        when (it.kind) {
                            Notification.Kind.OnError -> showError(R.string.auth_error, activity)
                            Notification.Kind.OnNext -> resolveNavView(it.value)
                            else -> {
                            }
                        }
                    }
        }

        override fun onError(error: VKError) {
            showError(R.string.auth_error, activity);
        }
    }

}