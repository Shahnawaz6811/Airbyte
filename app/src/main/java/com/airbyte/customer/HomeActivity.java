package com.airbyte.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.airbyte.R;
import com.airbyte.customer.complaints.ComplaintFragment;
import com.airbyte.customer.payment.PaymentFragment;
import com.airbyte.BaseActivity;
import com.airbyte.utils.Session;
import com.airbyte.verification.NumberVerifyActivity;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation.OnTabSelectedListener;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements OnNavigationItemSelectedListener{

    private static final String TAG = "HomeActivity";
    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    List<Fragment> fragments;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.nav_view)
    NavigationView navView;
    String phone;
    String name;
    Session session;
    @BindView(R.id.toolbar)
    Toolbar toolbar;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);


        Log.d(TAG, "onCreate: ");

        fragments = new ArrayList<Fragment>();
         fragments.add(new ComplaintFragment());
         fragments.add(new PaymentFragment());
         fragments.add(new ContactUsFragment());
          replaceFragment(fragments.get(0));

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        session = new Session(this);
        phone = session.getCustomerPhone();
        name = session.getCustomerName();
        setupToolbar();
        setupBottomNav();
        setupDrawer();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_home;
    }

    private void setupDrawer() {

          mDrawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        ((TextView) this.navView.getHeaderView(0).findViewById(R.id.tv_phone)).setText(phone);
        ((TextView) this.navView.getHeaderView(0).findViewById(R.id.tv_uname)).setText(name);
         drawerLayout.setDrawerListener(this.mDrawerToggle);
         navView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void setupToolbar() {
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: ");
                return true;
            }
        });
    }

    private void setupBottomNav() {
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Complaint", R.drawable.complaint);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Payment", R.drawable.online_payment);
         bottomNavigation.addItem(item1);
         bottomNavigation.addItem(item2);
          bottomNavigation.setOnTabSelectedListener(new OnTabSelectedListener() {
              @Override
              public boolean onTabSelected(int position, boolean wasSelected) {
                  if (!wasSelected) {
                      if (position == 0) {
                          toolbar.setTitle("Complaint");
                      }
                      else if (position == 1) {
                          toolbar.setTitle("Payment");
                      }
                      replaceFragment(fragments.get(position));
                      return true;
                  }
                  return false;
              }
          });
    }

    private void replaceFragment(Fragment f) {
        Log.d(TAG, "replaceFragment: ");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, f);
        ft.commit();
    }

    public void onBackPressed() {
        int itemNum =  bottomNavigation.getCurrentItem();

        if (itemNum == 0) {
            finish();
        } else {
             bottomNavigation.setCurrentItem(0);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: 131");
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() != R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        Log.d(TAG, "onOptionsItemSelected: ");
        this.drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.mDrawerToggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.signout:
                session.destroySession();
                Intent intent = new Intent(HomeActivity.this, NumberVerifyActivity.class);
                startActivity(intent);
                HomeActivity.this.finish();
                break;

            case R.id.item_payment:
                replaceFragment(fragments.get(1));
                toolbar.setTitle("Payment");

                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.item_complaint:
                replaceFragment(fragments.get(0));
                toolbar.setTitle("Complaint");

                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.contact_us:
                replaceFragment(fragments.get(2));
                toolbar.setTitle("Contact Us");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }

        return false;
    }

}
