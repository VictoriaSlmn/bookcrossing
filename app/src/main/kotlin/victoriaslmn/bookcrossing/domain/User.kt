package victoriaslmn.bookcrossing.domain

data class User(val id: Long?, val name: String, val photo: String?) {
    fun isEmpty(): Boolean {
       return id == null
    }
}
