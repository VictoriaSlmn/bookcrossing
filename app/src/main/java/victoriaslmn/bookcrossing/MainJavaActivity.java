package victoriaslmn.bookcrossing;


import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class MainJavaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView userPhoto;
    private TextView userName;
    private NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar
                        .make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(toggle);
        toggle.syncState();
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        userPhoto = (ImageView) nav_view.getHeaderView(0).findViewById(R.id.imageView);
        userName = (TextView) nav_view.getHeaderView(0).findViewById(R.id.textView);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void auth() {
        VKSdk.login(this, "docs", "friends");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (VKSdk.onActivityResult(requestCode, resultCode, data, new AuthCallback())) {
            return;
        }
        Toast.makeText(this, "vk auth error", Toast.LENGTH_LONG).show();

    }

    private void addBackStack(BaseFragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content, fragment);
        tx.addToBackStack(fragment.getFragmentName());
        tx.commit();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    private class AuthCallback implements VKCallback<VKAccessToken> {
        @Override
        public void onResult(VKAccessToken res) {
            VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200"));
            request.executeWithListener(new UserCallback());
        }

        @Override
        public void onError(VKError error) {
            Toast.makeText(getApplicationContext(), error.errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private class UserCallback extends VKRequest.VKRequestListener {

        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);
            try {
                JSONObject object = response.json.getJSONArray("response").getJSONObject(0);
                String photo = object.getString("photo_200");
                String name = object.getString("first_name");
                String lastName = object.getString("last_name");

                Picasso.with(getApplicationContext())
                        .load(photo)
                        .transform(new CircleTransform())
                        .into(userPhoto);
                userName.setText(String.format("%s %s", name, lastName));
                Menu menu = nav_view.getMenu();
                menu.clear();
                getMenuInflater().inflate(R.menu.activity_main_auth_drawer, menu);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onError(VKError error) {
            super.onError(error);
            Toast.makeText(getApplicationContext(), error.errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}



