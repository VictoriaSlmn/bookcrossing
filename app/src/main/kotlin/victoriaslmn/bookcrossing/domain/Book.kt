package victoriaslmn.bookcrossing.domain

data class Book(val id: Long,
                val title: String,
                val format: Format,
                val ownerId: Long,
                val localURI: String?,
                val remoteURI: String?) {
    enum class Format{
        NONAME, TXT, RTF, PDF, FB2, EPUB, DOC, DOCX
    }
}