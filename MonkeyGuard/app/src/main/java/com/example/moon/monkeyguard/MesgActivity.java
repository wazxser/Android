package com.example.moon.monkeyguard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by moon on 2016/7/30.
 */
public class MesgActivity extends AppCompatActivity {
    public ListView mesgList;
    private String[] cities = {"北京 朝阳", "江苏 宿迁", "江苏 南京", "江苏 徐州", "辽宁 朝阳"};
    private List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_mesg);

        mesgList = (ListView)findViewById(R.id.mesg_listview);
        mesgList.setAdapter(new SimpleAdapter(getApplication(), listitems,
                R.layout.activity_mesg_listview_item, new String[]{"name"},
                new int[]{R.id.listview_item_textview}));

        mesgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MesgActivity.this, TempActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
    }
}
