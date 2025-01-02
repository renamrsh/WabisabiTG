package com.example.wabisabi.Activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.wabisabi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageButton buttonDrawerToggle;
    ImageButton backMenuIcon;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onSearchBarClicked(int searchBarId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        SearchView searchView = findViewById(searchBarId);

        //LinearLayout important_cont_on_search_hide = findViewById(R.id.important_cont_on_search_hide);
        //LinearLayout important_recycle = findViewById(R.id.important_recycle);
            searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
                /*
                if(searchBarId==R.id.search_bar_home){
                    if (hasFocus) {
                        important_cont_on_search_hide.setVisibility(View.INVISIBLE);
                        important_recycle.setVisibility(View.VISIBLE);
                    } else {
                        important_recycle.setVisibility(View.INVISIBLE);
                        important_cont_on_search_hide.setVisibility(View.VISIBLE);
                    }
                }*/
            });


        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getHeight();
            int keypadHeight = screenHeight - r.bottom;
            if (keypadHeight > screenHeight * 0.15) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }


    protected void onCreateDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        buttonDrawerToggle = findViewById(R.id.buttonDrawerToggle);
        navigationView = findViewById(R.id.navigationView);
        ConstraintLayout logOut = findViewById(R.id.logOut);

        logOut.setOnClickListener(v -> logOut());

        View headerView = navigationView.getHeaderView(0);
        backMenuIcon = headerView.findViewById(R.id.backMenuIcon);

        buttonDrawerToggle.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        backMenuIcon.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.sdrawer_profile) {
                startActivity(new Intent(BaseActivity.this, Profile.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }else if (itemId == R.id.sdrawer_bookmarks) {
                startActivity(new Intent(BaseActivity.this, Bookmarks.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }/*else if (itemId == R.id.sdrawer_settings) {
                startActivity(new Intent(BaseActivity.this, Settings.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }*/
            drawerLayout.close();
            return false;
        });
    }
    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        new Handler().postDelayed(() -> {
            startActivity(new Intent(BaseActivity.this, Login.class));
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }, 500);
    }
}
