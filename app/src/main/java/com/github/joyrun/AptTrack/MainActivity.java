package com.github.joyrun.AptTrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.joyrun.Test;

@Test("aa")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
