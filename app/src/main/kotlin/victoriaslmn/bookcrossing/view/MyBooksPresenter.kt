package victoriaslmn.bookcrossing.view

import android.support.v7.widget.RecyclerView
import rx.Notification
import rx.Observable
import victoriaslmn.bookcrossing.R
import victoriaslmn.bookcrossing.data.document.BookProvider
import victoriaslmn.bookcrossing.data.user.UserProvider
import victoriaslmn.bookcrossing.domain.Book
import victoriaslmn.bookcrossing.view.common.BookAdapter
import victoriaslmn.bookcrossing.view.common.RecycleViewPresenter

class MyBooksPresenter(val recyclerView: RecyclerView, val bookProvider: BookProvider, val userProvider: UserProvider) : RecycleViewPresenter(recyclerView) {
    override fun search(query: String) {
        searchDocuments(query)
    }

    override fun init() {
        Observable.zip(userProvider.getCurrentUser(), userProvider.getAccessToken(),
                { user, assesToken -> Pair(user, assesToken) })//todo paging
                .flatMap { bookProvider.getBooksByUser(it.first, 0, it.second) }
                .execute {//todo common
                    when (it.kind) {
                        Notification.Kind.OnError -> {
                            it.throwable.printStackTrace()
                            showError(R.string.download_error, recyclerView.context)
                        }
                        Notification.Kind.OnNext -> resolveBookList(it.value)
                        else -> {
                        }
                    }
                }
    }

    override fun addAction() {
        //todo 3. download new book from file and find in internet?
    }


    private fun searchDocuments(query: String) {
        if (query.length < 3) {
            return
        }

        userProvider.getAccessToken().
                concatMap {
                    bookProvider.searchBooks(query, 0, it) //todo paging
                }
                .execute {//todo common
                    when (it.kind) {
                        Notification.Kind.OnError ->  {
                            it.throwable.printStackTrace()
                            showError(R.string.download_error, recyclerView.context)
                        }
                        Notification.Kind.OnNext -> resolveBookList(it.value)
                        else -> {
                        }
                    }
                }
    }

    fun resolveBookList(value: List<Book>) {
        recyclerView.adapter = BookAdapter(value, { bookProvider.downloadBook(it) }) //todo 2. save finding books as my
    }
}
