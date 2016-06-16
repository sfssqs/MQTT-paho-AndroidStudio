package com.shyla.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.shyla.asmqtt.R;
import com.shyla.asmqtt.RemoteControl;

public class NavigatorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectFragment.OnFragmentInteractionListener,
        CertParserFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener,
        LiveStreamingFragment.OnFragmentInteractionListener {

    private static final String TAG = "NavigatorActivity";

    private Fragment connectFragment;
    private Fragment certParserFragment;
    private Fragment aboutFragment;
    private Fragment liveVideoFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        connectFragment = ConnectFragment.newInstance(null, null);
        certParserFragment = CertParserFragment.newInstance(null, null);
        aboutFragment = AboutFragment.newInstance(null, null);
        liveVideoFragment = LiveStreamingFragment.newInstance(null, null);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, connectFragment);
        transaction.commit();

        RemoteControl.createInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        RemoteControl.getInstance().registerResources(this);
    }

    @Override
    protected void onPause() {
        RemoteControl.getInstance().unregisterResources();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        RemoteControl.destroyInstance();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.v(TAG, "onNavigationItemSelected, item.id : " + id);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_camera) {
            transaction.replace(R.id.content, connectFragment);
            transaction.commit();
        } else if (id == R.id.nav_gallery) {
            transaction.replace(R.id.content, certParserFragment);
            transaction.commit();
        } else if (id == R.id.nav_slideshow) {
            transaction.replace(R.id.content, liveVideoFragment);
            transaction.commit();
        } else if (id == R.id.nav_manage) {
            transaction.replace(R.id.content, aboutFragment);
            transaction.commit();
        }

//        else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.v(TAG, "onFragmentInteraction");
    }
}
