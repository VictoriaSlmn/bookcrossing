package victoriaslmn.bookcrossing.view.common

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.book_item.view.*
import victoriaslmn.bookcrossing.R
import victoriaslmn.bookcrossing.domain.Book

class BookAdapter(val books: MutableList<Book>,
                  val downloadBook: (book: Book) -> Unit,
                  val openBook: (book: Book) -> Unit) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onBindViewHolder(viewHolder: BookViewHolder, position: Int) {
        viewHolder.bind(books.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    fun addBook(book: Book) {
        books.add(0, book)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return books.count()
    }

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(book: Book) {
            itemView.bookType.text = "${book.format}"
            itemView.bookDescription.text = book.title
            if (book.localURI != null) {
                itemView.bookDownload.visibility = View.GONE
                itemView.setOnClickListener { openBook(book) }
            } else {
                itemView.bookDownload.visibility = View.VISIBLE
                itemView.bookDownload.setOnClickListener({
                    downloadBook(book)
                })
            }
        }
    }

    fun updateBookDownloadedFlag(value: Book, searchMode: Boolean) {
        val position = books.indexOfFirst {
            if (searchMode) {
                it.title == value.title
            } else {
                it.id == value.id
            }
        }
        val book = books.get(position)
        book.localURI = value.localURI

        notifyItemChanged(position)
    }
}

