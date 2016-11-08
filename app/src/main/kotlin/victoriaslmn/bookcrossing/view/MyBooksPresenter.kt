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

class MyBooksPresenter(val recyclerView: RecyclerView,
                       val bookProvider: BookProvider,
                       val userProvider: UserProvider,
                       val openBook: (book: Book) -> Unit) : RecycleViewPresenter(recyclerView) {

    override fun search(query: String) {
        searchDocuments(query)
    }

    override fun init() {
        getUserAndAccesskey()
                .flatMap { bookProvider.getBooksByUser(it.first, 0, it.second) } //todo paging
                .execute { dataLoadedAction(it, { value -> resolveBookList(value) }) }
    }

    private fun getUserAndAccesskey() = Observable.zip(userProvider.getCurrentUser(), userProvider.getAccessToken(),
            { user, assesToken -> Pair(user, assesToken) })

    override fun addAction() {
        //todo 3. download new book from file and find in internet?
    }


    private fun searchDocuments(query: String) {
        if (query.length < 3) {
            return
        }

        userProvider.getAccessToken().
                concatMap { bookProvider.searchBooks(query, 0, it) } //todo paging
                .execute { dataLoadedAction(it, { value -> resolveBookList(value) }) }
    }

    fun <T> dataLoadedAction(it: Notification<T>, action: (T) -> Unit) {
        when (it.kind) {
            Notification.Kind.OnError -> {
                it.throwable.printStackTrace()
                showError(R.string.download_error, recyclerView.context)
            }
            Notification.Kind.OnNext -> action(it.value)
            else -> {
            }
        }
    }

    fun resolveBookList(value: List<Book>) {
        recyclerView.adapter = BookAdapter(value, downloadDocumentAction(), openBook)
    }

    private fun downloadDocumentAction(): (Book) -> Unit = {

        //todo start loading

        getUserAndAccesskey()
                .flatMap { userAndAccesskey ->
                    bookProvider.downloadBook(it,
                            userAndAccesskey.first!!, //todo auth exception
                            userAndAccesskey.second!!)
                }.execute {
            dataLoadedAction(it, {
                value ->
                (recyclerView.adapter as BookAdapter).updateBookDownloadedFlag(value, searchMode)
            })
        }
    }
}
