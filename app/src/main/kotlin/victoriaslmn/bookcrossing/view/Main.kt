package victoriaslmn.bookcrossing.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.MenuItem
import victoriaslmn.bookcrossing.MainActivity
import victoriaslmn.bookcrossing.R
import victoriaslmn.bookcrossing.data.document.BookProvider
import victoriaslmn.bookcrossing.data.user.UserProvider
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.view.common.FileUtils
import victoriaslmn.bookcrossing.view.common.RecycleViewPresenter
import java.io.File


class Main(val activity: MainActivity, val userProvider: UserProvider, val bookProvider: BookProvider) : Router {

    val navigationViewPresenter: NavigationViewPresenter
    val recyclerView: RecyclerView
    var currentRecycleViewPresenter: RecycleViewPresenter

    val onQueryTextListener = SearchOnQueryTextListener()

    init {
        recyclerView = activity.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)

        currentRecycleViewPresenter = MyBooksPresenter(recyclerView, bookProvider, userProvider, this)
        currentRecycleViewPresenter.init()
        currentRecycleViewPresenter.searchMode = false

        val fab = activity.findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { currentRecycleViewPresenter.addAction() }

        navigationViewPresenter = NavigationViewPresenter(activity, userProvider, Navigation(), { currentRecycleViewPresenter.init() })
        navigationViewPresenter.init()
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
                    currentRecycleViewPresenter = MyBooksPresenter(recyclerView, bookProvider, userProvider, this@Main)
                }
                R.id.nav_recommendations -> {
                }
            }
            currentRecycleViewPresenter.init()
            currentRecycleViewPresenter.searchMode = false
            navigationViewPresenter.closeDrawer()
            return true
        }
    }

    inner class SearchOnQueryTextListener() : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(query: String?): Boolean {
            if (query == null || query.length == 0) {
                currentRecycleViewPresenter.init()
                currentRecycleViewPresenter.searchMode = false
            } else {
                currentRecycleViewPresenter.searchMode = true
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

    override fun openBook(book: Book) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.fromFile(File(book.localURI))
        val type = when (book.format) {
            Book.Format.PDF -> "application/pdf"
            Book.Format.DOC -> "application/msword"
            Book.Format.DOCX -> "application/msword"
            Book.Format.TXT -> "text/plain"
            Book.Format.RTF -> "application/msword"
            Book.Format.EPUB -> "application/epub+zip"
            Book.Format.FB2 -> "*/*"
            Book.Format.NONAME -> "*/*"
        }
        intent.setDataAndType(uri, type)
        activity.startActivity(Intent.createChooser(intent, "Choose an Application:"))
    }

    override fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                return false
            }
        } else {
            return true
        }
    }

    override fun requestStoragePermission(requestCode: PermissionRequestCode, callback: PermissionRequestCallback) {
        ActivityCompat.requestPermissions(activity, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode.code)
        activity.setPermissionRequestCallback(callback)
    }

    override fun showFileChooser(fileChoose: (File?) -> Unit) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            activity.setActivityResultCallback(FileChooseCallback(fileChoose))
            activity.startActivityForResult(
                    Intent.createChooser(intent, activity.getString(R.string.file_chooser_title)),
                    ActivityResultRequestCode.FILE_SELECT.code)
        } catch (ex: android.content.ActivityNotFoundException) {
            navigationViewPresenter.showError(R.string.file_chooser_error, activity)
        }
    }

    inner class FileChooseCallback(val fileChoose: (File?) -> Unit) : ActivityResultCallback {
        override fun onActivityResult(requestCode: ActivityResultRequestCode, data: Intent) {
            if (requestCode === ActivityResultRequestCode.FILE_SELECT) {
                val path = FileUtils.getPath(activity, data.data)
                val file = if (path == null) null else File(path)
                fileChoose(file)
            } else {
                fileChoose(null)
            }
        }
    }
}

interface Router {
    fun openBook(book: Book)
    fun isStoragePermissionGranted(): Boolean
    fun requestStoragePermission(requestCode: PermissionRequestCode, callback: PermissionRequestCallback)
    fun showFileChooser(fileChoose: (File?) -> Unit)
}

interface PermissionRequestCallback {
    fun allow(code: PermissionRequestCode)
    fun deny(code: PermissionRequestCode)
}

interface ActivityResultCallback {
    fun onActivityResult(requestCode: ActivityResultRequestCode, data: Intent)
}

enum class ActivityResultRequestCode(val code: Int) {
    FILE_SELECT(1);

    companion object {
        fun from(findCode: Int): ActivityResultRequestCode = ActivityResultRequestCode.values().first { it.code == findCode }
    }
}

enum class PermissionRequestCode(val code: Int) {
    STORAGE(1);

    companion object {
        fun from(findCode: Int): PermissionRequestCode = PermissionRequestCode.values().first { it.code == findCode }
    }
}