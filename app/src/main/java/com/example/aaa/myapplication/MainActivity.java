package com.example.aaa.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MyViewGroup myViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myViewGroup = (MyViewGroup) findViewById(R.id.viewgroup);

        findViewById(R.id.bt_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewGroup.reset();
            }
        });
    }
}
