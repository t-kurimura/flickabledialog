package com.tkurimura.flickabledialog.sample;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.sample.R;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    navigationView.setCheckedItem(R.id.nav_mail);

    setDefaultFragment();
  }

  @Override public boolean onNavigationItemSelected(MenuItem item) {

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    switch (item.getItemId()) {
      case R.id.nav_mail:
        fragmentTransaction.replace(R.id.tab_content_container, new CreateMailFragment());
        fragmentTransaction.commit();
        setTitle(item.getTitle());
        break;
      case R.id.nav_review:
        fragmentTransaction.replace(R.id.tab_content_container, new ReviewAppealFragment());
        fragmentTransaction.commit();
        setTitle(item.getTitle());
        break;
      case R.id.nav_premium:
        fragmentTransaction.replace(R.id.tab_content_container, new PremiumAppealFragment());
        fragmentTransaction.commit();
        setTitle(item.getTitle());
        break;
      case R.id.nav_profile:
        fragmentTransaction.replace(R.id.tab_content_container, new ProfileTutorialFragment());
        fragmentTransaction.commit();
        setTitle(item.getTitle());
        break;
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void setDefaultFragment(){

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.tab_content_container, new CreateMailFragment());
    fragmentTransaction.commit();
  }
}