package com.example.moon.monkey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TwtMqtt mqtt = new TwtMqtt();
        mqtt.setBroker("tcp://115.29.109.27:61613");
        mqtt.setTopic("sensor");
        mqtt.setUserName("admin");
        mqtt.setPassword("password");
        mqtt.setContent("cdusvnudwr");
        mqtt.setQos(1);
        mqtt.setClientId("wangyuehaun");

        mqtt.init();
        mqtt.listen();
    }
}
