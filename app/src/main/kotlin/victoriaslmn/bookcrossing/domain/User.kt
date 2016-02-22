package victoriaslmn.bookcrossing.domain

data class User(val id: String?, val name: String, val photo: String?) {
    fun isEmpty(): Boolean {
       return id == null
    }
}
