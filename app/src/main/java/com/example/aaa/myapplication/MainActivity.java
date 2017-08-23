package com.example.aaa.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MyViewGroup myViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myViewGroup = (MyViewGroup) findViewById(R.id.viewgroup);

        findViewById(R.id.bt_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewGroup.testMove();
            }
        });
    }
}
