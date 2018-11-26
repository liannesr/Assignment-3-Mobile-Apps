package com.techexchange.mobileapps.assignment3;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View  laberinth = new LaberinthView(this);
        setContentView(laberinth);

    }

}
