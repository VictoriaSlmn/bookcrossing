package victoriaslmn.bookcrossing.view.common

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class EmptyAdapter(@LayoutRes val layout: Int) : RecyclerView.Adapter<EmptyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: EmptyAdapter.ViewHolder?, position: Int) {
        //nothing
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }
}
