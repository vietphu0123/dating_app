package com.example.datingapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class TopNavigationViewHelper {
    private static final String TAG ="TopNavigationViewHelper";
    public static void SetupTopNavigationView(BottomNavigationViewEx tv)
    {
        Log.d(TAG, "SetupTopNavigationView: setting up navigationview");

    }
    public static void enableNavigation (final Context context,BottomNavigationViewEx view)
    {
        view.setOnNavigationItemSelectedListener((new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_profile:
                        Intent i=new Intent(context,SettingsActivity.class);
                        context.startActivity(i);
                        break;

                    case R.id.ic_matched:
                        Intent j=new Intent(context,MatchesActivity.class);
                        context.startActivity(j);
                        break;
                }
                return false;
            }
        }));
    }
}
