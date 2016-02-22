package victoriaslmn.bookcrossing.data.document

import victoriaslmn.bookcrossing.data.common.HttpClient


object DocumentProvider {
    internal val documentApi = HttpClient.retrofit.create(DocumentsApi::class.java);


}
