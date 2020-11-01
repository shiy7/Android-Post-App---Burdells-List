package com.example.finalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.finalapp.ui.chat.ChatFragment;
import com.example.finalapp.ui.home.HomeFragment;
import com.example.finalapp.ui.profile.ProfileFragment;
import com.example.finalapp.ui.shop.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;
    Fragment selectedFrag = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);


        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new HomeFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.navigation_home:
                            selectedFrag = new HomeFragment();
                            break;
                        case R.id.navigation_shop:
                            selectedFrag = new ShopFragment();
                            break;
                        case R.id.navigation_post:
                            selectedFrag = null;
                            startActivity(new Intent(MainActivity.this, PostActivity.class));
                            break;
                        case R.id.navigation_chat:
                            selectedFrag = new ChatFragment();
                            break;
                        case R.id.navigation_account:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            selectedFrag = new ProfileFragment();
                            break;
                    }

                    if (selectedFrag != null){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment, selectedFrag).commit();
                    }

                    return true;
                }
            };

}
