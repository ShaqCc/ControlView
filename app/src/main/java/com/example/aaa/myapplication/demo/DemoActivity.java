package com.example.aaa.myapplication.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.aaa.myapplication.R;

/****************************************
 * 功能说明:  
 *
 * Author: Created by bayin on 2017/8/24.
 ****************************************/

public class DemoActivity extends Activity{
    private MyView canvasView;
    private Button btnClearPoints;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_layout);
        getViews();
        btnClearPoints.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                canvasView.clearPoints();
            }
        });
    }

    private void getViews() {
        canvasView = (MyView) findViewById(R.id.canvasView);
        btnClearPoints = (Button) findViewById(R.id.btnClearPoints);
    }
}
