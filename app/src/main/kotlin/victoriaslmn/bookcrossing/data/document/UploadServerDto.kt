package victoriaslmn.bookcrossing.data.document

import com.google.gson.annotations.SerializedName

class UploadServerDto {
    @SerializedName("upload_url")
    var uploadUrl: String? = null
}