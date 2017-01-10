package victoriaslmn.bookcrossing;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.vk.sdk.VKSdk;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import victoriaslmn.bookcrossing.data.document.BookProvider;
import victoriaslmn.bookcrossing.data.document.DocumentsApi;
import victoriaslmn.bookcrossing.data.document.DocumentsCache;
import victoriaslmn.bookcrossing.data.user.UserApi;
import victoriaslmn.bookcrossing.data.user.UserCache;
import victoriaslmn.bookcrossing.data.user.UserProvider;
import victoriaslmn.bookcrossing.view.Main;
import victoriaslmn.bookcrossing.view.PermissionRequestCallback;
import victoriaslmn.bookcrossing.view.PermissionRequestCode;

public class MainActivity extends AppCompatActivity {

    private Main main;
    private OrmLiteSqlite ormLiteSqlite;
    private PermissionRequestCallback permissionRequestCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///todo refactor
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
            main =
                    new Main(
                            this,
                            new UserProvider(
                                    retrofit.create(UserApi.class),
                                    new UserCache(ormLiteSqlite.getUserDao())),
                            new BookProvider(retrofit.create(DocumentsApi.class),
                                    new DocumentsCache(ormLiteSqlite.getDocumentDao(), getBaseContext())));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ///// TODO: 22.02.16
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(main.getOnQueryTextListener());
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (VKSdk.onActivityResult(requestCode, resultCode, data, main.getAuthCallback())) {
            return;
        }
        main.showAuthError();
    }

    @Override
    protected void onDestroy() {
        ormLiteSqlite.close();
        super.onDestroy();
    }

    public void openVKAuthActivity() {
        VKSdk.login(this, "docs", "friends");
    }//// TODO: 30.04.16 refactor

    public void setPermissionRequestCallback(@NotNull PermissionRequestCallback callback) {
        permissionRequestCallback = callback;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionRequestCallback == null) {
            return;
        }

        PermissionRequestCode permissionRequestCode = PermissionRequestCode.Companion.from(requestCode);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionRequestCallback.allow(permissionRequestCode);
        } else {
            permissionRequestCallback.deny(permissionRequestCode);
        }

    }
}



