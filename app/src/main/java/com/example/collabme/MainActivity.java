package com.example.collabme;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_socialmedia);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // this is the main activity
    }
}