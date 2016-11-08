package victoriaslmn.bookcrossing.view.common

import android.support.v7.widget.RecyclerView


abstract class RecycleViewPresenter(val recycleViewPresenter: RecyclerView) : BasePresenter() {
    var searchMode = false

    abstract fun addAction()

    abstract fun search(query: String)
}