package victoriaslmn.bookcrossing.data.common

import com.squareup.okhttp.OkHttpClient

import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory

object HttpClient {
    val retrofit = Retrofit.Builder()
            .baseUrl("https://api.vk.com/method/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build();
}
