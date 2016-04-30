package victoriaslmn.bookcrossing.view

import android.support.v7.widget.RecyclerView
import rx.Notification
import victoriaslmn.bookcrossing.R
import victoriaslmn.bookcrossing.data.document.BookProvider
import victoriaslmn.bookcrossing.data.user.UserProvider
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.domain.BookFilter
import victoriaslmn.bookcrossing.view.common.BookAdapter
import victoriaslmn.bookcrossing.view.common.RecycleViewPresenter

class MyBooksPresenter(val recyclerView: RecyclerView, val bookProvider: BookProvider, val userProvider: UserProvider): RecycleViewPresenter(recyclerView) {
    override fun search(query: String) {
        searchDocuments(query)
    }

    override fun init() {
        //todo
    }

    override fun addAction() {
       //todo
    }


    private fun searchDocuments(query: String) {
        userProvider.getAccessToken().
                concatMap {
                    bookProvider.getBooks(BookFilter(query, 0, it!!))///todo
                }
                .execute {
                    when (it.kind) {
                        Notification.Kind.OnError -> showError(R.string.download_error, recyclerView.context)
                        Notification.Kind.OnNext -> resolveBookList(it.value)
                        else -> {
                        }
                    }
                }
    }

    fun resolveBookList(value: List<Book>) {
        recyclerView.adapter = BookAdapter(value, { bookProvider.downloadBook(it) })
    }
}
