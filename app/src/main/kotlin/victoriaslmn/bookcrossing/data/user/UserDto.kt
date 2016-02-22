package victoriaslmn.bookcrossing.data.user

import com.google.gson.annotations.SerializedName
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import victoriaslmn.bookcrossing.data.common.Fields

@DatabaseTable(tableName = "Users")
class UserDto {
    object Field{
        const val CURRENT = "current";
    }

    @DatabaseField(columnName = "id", id = true, throwIfNull = true)
    @SerializedName("uid")
    var id: Long = 0

    @DatabaseField(columnName = "first_name")
    @SerializedName("first_name")
    var firstName: String? = null

    @DatabaseField(columnName = "last_name")
    @SerializedName("last_name")
    var lastName: String? = null

    @DatabaseField(columnName = Fields.PHOTO_200)
    @SerializedName(Fields.PHOTO_200)
    var photoBig: String? = null

    @DatabaseField(columnName = Fields.PHOTO_100)
    @SerializedName(Fields.PHOTO_100)
    var photoSmall: String? = null

    @DatabaseField(columnName = Field.CURRENT)
    var current: Boolean = false

    @DatabaseField(columnName = "access_token")
    var accessToken: String? = null
}