package victoriaslmn.bookcrossing.view

import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.MenuItem
import victoriaslmn.bookcrossing.MainActivity
import victoriaslmn.bookcrossing.R
import victoriaslmn.bookcrossing.data.document.BookProvider
import victoriaslmn.bookcrossing.data.user.UserProvider
import victoriaslmn.bookcrossing.view.common.RecycleViewPresenter


class Router(val activity: MainActivity, val userProvider: UserProvider, val bookProvider: BookProvider) {

    val navigationViewPresenter: NavigationViewPresenter
    val recyclerView: RecyclerView
    var currentRecycleViewPresenter: RecycleViewPresenter

    val onQueryTextListener = SearchOnQueryTextListener()

    init {
        navigationViewPresenter = NavigationViewPresenter(activity, userProvider, Navigation())
        navigationViewPresenter.init()

        recyclerView = activity.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)

        currentRecycleViewPresenter = MyBooksPresenter(recyclerView, bookProvider, userProvider)
        currentRecycleViewPresenter.init()

        val fab = activity.findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { currentRecycleViewPresenter.addAction() }
    }


    inner class Navigation : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.getItemId()) {
                R.id.nav_exit -> navigationViewPresenter.logout()
                R.id.nav_folders -> {
                }
                R.id.nav_friends -> {
                }
                R.id.nav_main -> {
                    currentRecycleViewPresenter = MyBooksPresenter(recyclerView, bookProvider, userProvider)
                }
                R.id.nav_recommendations -> {
                }
            }
            currentRecycleViewPresenter.init()
            navigationViewPresenter.closeDrawer()
            return true
        }
    }

    inner class SearchOnQueryTextListener() : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(query: String?): Boolean {
            if (query != null) {
                currentRecycleViewPresenter.search(query);
            }
            return true
        }

        override fun onQueryTextSubmit(submit: String?): Boolean {
            return true
        }
    }

    fun getAuthCallback(): NavigationViewPresenter.AuthCallback {
        return navigationViewPresenter.authCallback
    }

    fun showAuthError() {
        navigationViewPresenter.showError(R.string.auth_error, activity)
    }
}
