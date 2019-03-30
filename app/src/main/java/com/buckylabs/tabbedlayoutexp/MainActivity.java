package com.buckylabs.tabbedlayoutexp;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity {

    private PageAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Button backup_Btn;
    private ImageButton refresh_Btn;
    private ImageButton share_Btn;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("example_switch", false)) {
            setTheme(R.style.DarkTheme);
            // recreate();
        } else {
            setTheme(R.style.AppTheme);
            // recreate();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        /*Tab tab=tabLayout.getTabAt(0);
        int installedAppsSize=((Fragment_1)mSectionsPagerAdapter.getItem(0)).installedAppsSize;
        tab.setText("Installed Apps "+"("+installedAppsSize+")");*/


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        backup_Btn = findViewById(R.id.backup_Btn);
        refresh_Btn = findViewById(R.id.refresh);
        share_Btn = findViewById(R.id.share);


        Service br = new Service();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter);

        backup_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tabLayout.getSelectedTabPosition() == 0) {
                    Toast.makeText(MainActivity.this, "Installed   ", Toast.LENGTH_SHORT).show();
                    TabLayout.Tab tab=tabLayout.getTabAt(0);

                    ((Fragment_1)mSectionsPagerAdapter.getItem(0)).backupHelperInit();



                }else {
                    Toast.makeText(MainActivity.this, "Restored   ", Toast.LENGTH_SHORT).show();

                        ((Fragment_2)mSectionsPagerAdapter.getItem(1)).InstallApplication();


                }
                    }

        });


        refresh_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tabLayout.getSelectedTabPosition() == 0) {

                    TabLayout.Tab tab = tabLayout.getTabAt(0);

                    ((Fragment_1) mSectionsPagerAdapter.getItem(0)).populateRecyclerview();


                } else {


                    ((Fragment_2) mSectionsPagerAdapter.getItem(1)).populateRecyclerview();


                }
            }


        });


        share_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (tabLayout.getSelectedTabPosition() == 0) {

                    TabLayout.Tab tab = tabLayout.getTabAt(0);

                    ((Fragment_1) mSectionsPagerAdapter.getItem(0)).shareApks();


                } else {


                    ((Fragment_2) mSectionsPagerAdapter.getItem(1)).shareApks();


                }
            }


        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tabLayout.getSelectedTabPosition()==0){

                    backup_Btn.setText("BACKUP");

                }else {

                    backup_Btn.setText("RESTORE");


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setupViewPager (ViewPager mViewPager){
            mSectionsPagerAdapter = new PageAdapter(getSupportFragmentManager());
            mSectionsPagerAdapter.addFragment(new Fragment_1(), "One");
            mSectionsPagerAdapter.addFragment(new Fragment_2(), "Two");
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        /*MenuItem item=menu.findItem(R.id.search);
        SearchView searchView= item.getActionView();
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up backup_Btn, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivityNew.class);
            /*intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);*/

            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



}
