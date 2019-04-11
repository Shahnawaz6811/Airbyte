package com.airbyte.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.airbyte.R;
import com.airbyte.BaseActivity;
import com.airbyte.utils.Session;
import com.airbyte.verification.NumberVerifyActivity;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeActivity";
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    List<Fragment> tabFragments;
    List<Fragment> drawerFragments;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.nav_view)
    NavigationView navView;
    String phone;
    String name;
    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;
    Session session;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        Log.d(TAG, "onCreate: ");

        setupFragment();

        new ServiceHandler().makeServiceCall();






    }

    private void setupFragment() {
        getSupportActionBar().setTitle("Complaints");
        tabFragments = new ArrayList<Fragment>();
        tabFragments.add(new ComplaintListFragment());
        tabFragments.add(new CustomerListFragment());
        tabFragments.add(new PaymentListFragment());
        replaceFragment(tabFragments.get(0));

        drawerFragments = new ArrayList<Fragment>();
        drawerFragments.add(new ComplaintListFragment());
        drawerFragments.add(new PaymentListFragment());
        drawerFragments.add(new AddUserFragment());
        drawerFragments.add(new AddWorkerFragment());
        drawerFragments.add(new AddGroupFragment());
        drawerFragments.add(new ManageGroupFragment());
        drawerFragments.add(new SendMessageFragment());
        drawerFragments.add(new CreateTemplateFragment());

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        session = new Session(this);
        phone = session.getAdminPhone();
        name = session.getAdminName();
        setupToolbar();
        setupBottomNav();
        setupDrawer();
    }

    private void setupToolbar() {
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
         toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: ");
                return true;
            }
        });
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


    private void setupBottomNav() {
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Complaints", R.drawable.complaint);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Customers", R.drawable.ic_group_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Payments", R.drawable.online_payment);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
//                if (wasSelected) {
                    switch (position){
                        case 0:
                            Log.d(TAG, "onTabSelected: ");
                            getSupportActionBar().setTitle("Complaints");
                            break;

                        case 1:
                            getSupportActionBar().setTitle("Customers");
                            break;

                        case 2:
                            getSupportActionBar().setTitle("Payments");
                            break;
                    }
                    replaceFragment(tabFragments.get(position));
                    return true;

            }
        });
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

    public void replaceFragment(Fragment f) {
        Log.d(TAG, "replaceFragment: ");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, f);
        ft.commit();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_dash_board;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.item_complaint:
                getSupportActionBar().setTitle("Complaints");
                bottomNavigation.setCurrentItem(0);
                replaceFragment(drawerFragments.get(0));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.item_payment:
                getSupportActionBar().setTitle("Payments");
                replaceFragment(drawerFragments.get(1));
                bottomNavigation.setCurrentItem(2);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.item_add_user:
                getSupportActionBar().setTitle("Add Customer");
                  replaceFragment(drawerFragments.get(2));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.item_add_worker:
                getSupportActionBar().setTitle("Add Worker");
                replaceFragment(drawerFragments.get(3));
                 drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.item_add_group    :
                Log.d(TAG, "onNavigationItemSelected: ");
                getSupportActionBar().setTitle("Add Group");
                replaceFragment(drawerFragments.get(4));
                 drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.item_manage_group:
                getSupportActionBar().setTitle("Manage Group");
                replaceFragment(drawerFragments.get(5));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.item_send_message:
                getSupportActionBar().setTitle("Notification");
                replaceFragment(drawerFragments.get(6));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.item_template:
                getSupportActionBar().setTitle("Template");
                replaceFragment(drawerFragments.get(7));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.signout:
                session.destroySession();
                Intent intent = new Intent(this, NumberVerifyActivity.class);
                startActivity(intent);
                finish();
                 break;

        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigation != null && bottomNavigation.getCurrentItem() != 0) {
            bottomNavigation.setCurrentItem(0);
            return;
        }
        super.onBackPressed();
    }
}
