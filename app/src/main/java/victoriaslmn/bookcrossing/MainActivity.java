package victoriaslmn.bookcrossing;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.vk.sdk.VKSdk;

import java.sql.SQLException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import victoriaslmn.bookcrossing.data.user.UserApi;
import victoriaslmn.bookcrossing.data.user.UserCache;
import victoriaslmn.bookcrossing.data.user.UserProvider;
import victoriaslmn.bookcrossing.view.auth.NavigationPresenter;

public class MainActivity extends AppCompatActivity {

    private NavigationPresenter navigationPresenter;
    private OrmLiteSqlite ormLiteSqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///todo
        ormLiteSqlite = new OrmLiteSqlite(this);
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient();
            client.interceptors().add(interceptor);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.vk.com/method/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            navigationPresenter =
                    new NavigationPresenter(
                            this,
                            new UserProvider(
                                    retrofit.create(UserApi.class),
                                    new UserCache(ormLiteSqlite.getUserDao())));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ///// TODO: 22.02.16
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(navigationPresenter.getOnQueryTextListener());
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (VKSdk.onActivityResult(requestCode, resultCode, data, navigationPresenter.getAuthCallback())) {
            return;
        }
        navigationPresenter.showError(R.string.auth_error);
    }

    @Override
    protected void onDestroy() {
        ormLiteSqlite.close();
        super.onDestroy();
    }

    public void openVKAuthActivity() {
        VKSdk.login(this, "docs", "friends");
    }
}



