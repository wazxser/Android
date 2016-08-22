package com.example.moon.monkey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by moon on 2016/7/27.
 */
public class TestActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ChartView chartView = (ChartView)findViewById(R.id.chartview);
    }
}
