package com.dlsu.thesis.getbetter.modules;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Toast;

import com.dlsu.thesis.getbetter.R;
import com.dlsu.thesis.getbetter.adminpages.AddImpressionFragment;
import com.dlsu.thesis.getbetter.adminpages.AddSymptomFragment;
import com.dlsu.thesis.getbetter.adminpages.AdminHomeFragment;
import com.dlsu.thesis.getbetter.adminpages.ViewImpressionsFragment;


public class AdminActivity extends AppCompatActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private CharSequence mTitle;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private AddImpressionFragment addImpressionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mTitle = getTitle();
        actionBar.setTitle(mTitle);


        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                if(item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }

                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(getApplicationContext(), "Home selected", Toast.LENGTH_SHORT).show();
                        AdminHomeFragment homeFragment = new AdminHomeFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.admin_frame_content, homeFragment);
                        fragmentTransaction.commit();
                        return true;

                    case R.id.impressions:
                        Toast.makeText(getApplicationContext(), "Impressions", Toast.LENGTH_SHORT).show();
                        ViewImpressionsFragment viewImpressionsFragment = new ViewImpressionsFragment();
                        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction3.replace(R.id.admin_frame_content, viewImpressionsFragment);
                        fragmentTransaction3.commit();
                        return true;

                    case R.id.symptoms:
                        return true;

                    case R.id.add_impression:
                        addImpressionFragment = new AddImpressionFragment();
                        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.admin_frame_content, addImpressionFragment);
                        fragmentTransaction1.commit();
                        return true;

                    case R.id.add_symptom:
                        AddSymptomFragment addSymptomFragment = new AddSymptomFragment();
                        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.admin_frame_content, addSymptomFragment);
                        fragmentTransaction2.commit();
                        return true;

                    case R.id.logout:
                        return true;
                    default:return true;
                }
            }
        });

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

    }

    public void onCheckBoxClicked(View view) {

        addImpressionFragment.onCheckBoxClicked(view);
    }



}
