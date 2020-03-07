package am.romanbalayan.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import am.romanbalayan.chatapp.EntryHall.WhoIsActivity;
import am.romanbalayan.chatapp.Friends.FriendsActivity;
import am.romanbalayan.chatapp.Settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Toolbar mToolBar;

    private ViewPager mViewPager;

    private SectionsPagerAdapter sectionsPagerAdapter;

    private TabLayout tabLayout;

    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolBar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager = findViewById(R.id.tab_view_pager_main);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = findViewById(R.id.tab_main);

        tabLayout.setupWithViewPager(mViewPager);

        initSpinner();
    }

    private void initSpinner() {
        spinner = findViewById(R.id.spinner);

        List<String> options = new ArrayList<>();
        options.add("Feed");
        options.add("Friends");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1){
                    Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                    startActivity(intent);
                    adapterView.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user == null){
            checkWhoIs();
        }
    }

    private void checkWhoIs(){
        Intent intent = new Intent(MainActivity.this, WhoIsActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_settings_button){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return true;
    }
}
