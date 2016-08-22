package com.example.moon.monkeyguard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by moon on 2016/7/22.
 */
public class TempActivity extends AppCompatActivity {
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        TextView textView = (TextView)findViewById(R.id.temp_text);
        textView.setText("llalalallalalal");
    }
}
