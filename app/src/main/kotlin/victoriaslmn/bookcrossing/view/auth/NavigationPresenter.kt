package victoriaslmn.bookcrossing.view.auth

import android.content.res.ColorStateList
import android.os.Environment
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import victoriaslmn.bookcrossing.data.document.BookProvider
import victoriaslmn.bookcrossing.data.user.UserProvider
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.domain.BookFilter
import victoriaslmn.bookcrossing.domain.User
import victoriaslmn.bookcrossing.view.CircleTransform
import kotlinx.android.synthetic.main.book_item.view.*
import java.io.*
import java.net.URL

class NavigationPresenter(val activity: MainActivity, val userProvider: UserProvider, val bookProvider: BookProvider) {
    val navView: NavigationView;
    val header: View;
    val recyclerView: RecyclerView;
    val drawerLayout: DrawerLayout;

    val authCallback = AuthCallback();
    val onQueryTextListener = SearchOnQueryTextListener();

    init {
        val toolbar = activity.findViewById(R.id.toolbar) as Toolbar
        activity.setSupportActionBar(toolbar)
        drawerLayout = activity.findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.setDrawerListener(toggle)
        toggle.syncState()

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
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        recyclerView = activity.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val fab = activity.findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { v -> Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show() }

        initNavView();
    }

    private fun initNavView() {
        userProvider
                .getCurrentUser()
                .execute {
                    when (it.kind) {
                        Notification.Kind.OnError -> showError(R.string.auth_error)
                        Notification.Kind.OnNext -> resolveNavView(it.value)
                        else -> {
                        }
                    }
                }
    }

    private fun logout() {
        userProvider.logOut()
                .execute {
                    when (it.kind) {
                        Notification.Kind.OnError -> showError(R.string.logout_error)
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
        if (user == null) {
            userPhoto.imageTintList = ColorStateList.valueOf(userPhoto.context.getColor(R.color.vk_white))
            userPhoto.setImageResource(R.drawable.ic_account_circle_black_24dp)
            userName.setText(R.string.enter)
            activity.getMenuInflater().inflate(R.menu.activity_main_drawer, menu)
            header.setOnClickListener { activity.openVKAuthActivity() }
        } else {
            userPhoto.imageTintList = null
            Picasso.with(activity)
                    .load(user.photo)
                    .transform(CircleTransform())
                    .into(userPhoto)
            userName.setText(user.name)
            activity.getMenuInflater().inflate(R.menu.activity_main_auth_drawer, menu)
            header.setOnClickListener(null)
        }
    }

    inner class AuthCallback : VKCallback<VKAccessToken> {
        override fun onResult(res: VKAccessToken) {
            userProvider.login(res.accessToken, res.userId)
                    .execute {
                        when (it.kind) {
                            Notification.Kind.OnError -> showError(R.string.auth_error)
                            Notification.Kind.OnNext -> resolveNavView(it.value)
                            else -> {
                            }
                        }
                    }
        }

        override fun onError(error: VKError) {
            showError(R.string.auth_error);
        }
    }

    inner class SearchOnQueryTextListener() : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(query: String?): Boolean {
            searchDocuments(query);
            return true
        }

        override fun onQueryTextSubmit(submit: String?): Boolean {
            return true
        }
    }

    private fun searchDocuments(query: String?) {
        userProvider.getAccessToken().
                concatMap {
                    bookProvider.getBooks(BookFilter(query, 0, it!!))///todo
                }
                .execute {
                    when (it.kind) {
                        Notification.Kind.OnError -> showError(R.string.download_error)
                        Notification.Kind.OnNext -> resolveBookList(it.value)
                        else -> {
                        }
                    }
                }
    }

    private fun resolveBookList(value: List<Book>) {
        recyclerView.adapter = BookAdapter(value);
    }

    fun showError(@StringRes message: Int) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    inner class BookAdapter(val books: List<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
        override fun onBindViewHolder(viewHolder: BookViewHolder, position: Int) {
            viewHolder.bind(books.get(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): BookViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
            return BookViewHolder(view)
        }

        override fun getItemCount(): Int {
            return books.count()
        }

        inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(book: Book) {
                itemView.bookType.text = "${book.format}"
                itemView.bookDescription.text = "${book.title}";
                if (book.localURI != null) {
                    itemView.bookDownload.visibility = View.GONE;
                } else {
                    itemView.bookDownload.visibility = View.VISIBLE;
                    itemView.bookDownload.setOnClickListener({
                        bookProvider.downloadBook(book)
                    })
                }
            }
        }
    }
}

fun <T> Observable<T>.execute(function: (Notification<T>) -> Unit) {
    this.materialize()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(function)
}
