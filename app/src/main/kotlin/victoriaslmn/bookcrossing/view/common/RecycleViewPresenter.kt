package victoriaslmn.bookcrossing.view.common

import android.support.v7.widget.RecyclerView


abstract class RecycleViewPresenter(val recycleViewPresenter: RecyclerView): BasePresenter() {
    abstract fun addAction()

    abstract fun search(query: String)
}