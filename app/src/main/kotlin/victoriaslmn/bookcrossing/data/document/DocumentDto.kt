package victoriaslmn.bookcrossing.data.document

import com.google.gson.annotations.SerializedName
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "Documents")
class DocumentDto {
    object Type {
        const val TEXT = 1
        const val ARCHIVE = 2
        const val GIF = 3
        const val IMAGE = 4
        const val AUDIO = 5
        const val VIDEO = 6
        const val BOOKS = 7
        const val NONAME = 8
    }

    @DatabaseField(columnName = "id", id = true)
    @SerializedName("id")
    var id: Long? = null

    @DatabaseField(columnName = "owner_id")
    @SerializedName("owner_id")
    var owerId: Long? = null

    @DatabaseField(columnName = "title")
    @SerializedName("title")
    var title: String? = null

    @DatabaseField(columnName = "size")
    @SerializedName("size")
    var size: Long? = null

    @DatabaseField(columnName = "ext")
    @SerializedName("ext")
    var ext: String? = null

    @DatabaseField(columnName = "url")
    @SerializedName("url")
    var url: String? = null

    @DatabaseField(columnName = "date")
    @SerializedName("date")
    var date: Long? = null

    @DatabaseField(columnName = "type")
    @SerializedName("type")
    var type: Int? = null

    @DatabaseField(columnName = "access_key")
    @SerializedName("access_key")
    var accessKey: String? = null

    @DatabaseField(columnName = "load")
    var load: Boolean = false

    @DatabaseField(columnName = "folder")
    var folder: String? = null

    @DatabaseField(columnName = "dir")
    var dir: String? = null
}
