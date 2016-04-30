package victoriaslmn.bookcrossing.view.common

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.book_item.view.*
import victoriaslmn.bookcrossing.R
import victoriaslmn.bookcrossing.domain.Book

class BookAdapter(val books: List<Book>, val downloadBook: (book: Book) -> Unit) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
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
                    downloadBook(book)
                })
            }
        }
    }
}

