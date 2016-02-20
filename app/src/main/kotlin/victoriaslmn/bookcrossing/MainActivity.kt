package victoriaslmn.bookcrossing

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var userPhoto: ImageView? = null;
    private var userName: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener(
                {
                    view ->
                    Snackbar
                            .make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                })
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        userPhoto = nav_view.getHeaderView(0).findViewById(R.id.imageView) as ImageView;
        userName = nav_view.getHeaderView(0).findViewById(R.id.textView) as TextView;
        userPhoto?.setOnClickListener({ view -> auth() })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.action_search) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.nav_camera -> Toast.makeText(this, "camera", Toast.LENGTH_SHORT).show()
            R.id.nav_gallery -> Toast.makeText(this, "gallery", Toast.LENGTH_SHORT).show()
            R.id.nav_slideshow -> Toast.makeText(this, "slideshow", Toast.LENGTH_SHORT).show()
            R.id.nav_manage -> Toast.makeText(this, "manage", Toast.LENGTH_SHORT).show()
            R.id.nav_share -> Toast.makeText(this, "share", Toast.LENGTH_SHORT).show()
            R.id.nav_send -> Toast.makeText(this, "send", Toast.LENGTH_SHORT).show()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun auth() {
        VKSdk.login(this, "docs", "friends")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && VKSdk.onActivityResult(requestCode, resultCode, data, AuthCallback())) {
            return
        }
        Toast.makeText(this@MainActivity,"vk auth error", Toast.LENGTH_LONG).show()
    }

    private inner class AuthCallback : VKCallback<VKAccessToken> {
        override fun onResult(token: VKAccessToken) {
            val request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_50"))
            request.executeWithListener(getUserCallback())
        }

        override fun onError(error: VKError) {
            Toast.makeText(this@MainActivity, error.errorMessage ?: "auth error", Toast.LENGTH_LONG).show()
        }
    }

    private inner class getUserCallback : VKRequest.VKRequestListener() {
        override fun onError(error: VKError?) {
            super.onError(error)
            Toast.makeText(this@MainActivity, error?.errorMessage ?: "get user error", Toast.LENGTH_LONG).show()
        }

        override fun onComplete(response: VKResponse?) {
            super.onComplete(response)
            val photo = response?.json?.getString("photo_50")
            val name = response?.json?.getString("first_name")
            val lastName = response?.json?.getString("last_name")

            Picasso.with(this@MainActivity).load(photo).into(userPhoto);
            userName?.text = name + " " + lastName;
        }
    }
}



