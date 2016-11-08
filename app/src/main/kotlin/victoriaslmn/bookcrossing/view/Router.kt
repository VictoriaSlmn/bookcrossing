package victoriaslmn.bookcrossing.view

import android.content.Intent
import android.net.Uri
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
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.view.common.RecycleViewPresenter
import java.io.File


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

        currentRecycleViewPresenter = MyBooksPresenter(recyclerView, bookProvider, userProvider, { openBook(it) })
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
                    currentRecycleViewPresenter = MyBooksPresenter(recyclerView, bookProvider, userProvider, { openBook(it) })
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
            if (query == null || query.length == 0) {
                currentRecycleViewPresenter.init()
            } else {
                currentRecycleViewPresenter.search(query)
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

    fun openBook(book: Book) {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(book.title)
        val extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
        val mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        if (extension.equals("", ignoreCase = true) || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "text/*")
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype)
        }
        // custom message for the intent
        activity.startActivity(Intent.createChooser(intent, "Choose an Application:"))
    }
}
