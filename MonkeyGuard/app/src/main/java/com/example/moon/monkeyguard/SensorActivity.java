package com.example.moon.monkeyguard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by moon on 2016/7/19.
 */
public class SensorActivity extends AppCompatActivity {
    private View view1, view2, view3, view4;//需要滑动的页卡
    private ViewPager viewPager;//viewpager
    private PagerTitleStrip pagerTitleStrip;//viewpager的标题
    private PagerTabStrip pagerTabStrip;//一个viewpager的指示器，效果就是一个横的粗的下划线
    private List<View> viewList;//把需要滑动的页卡添加到这个list中
    private List<String> titleList;//viewpager的标题
    private Button weibo_button;//button对象，一会用来进入第二个Viewpager的示例
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        LayoutInflater lf = getLayoutInflater().from(this);
        view3 = lf.inflate(R.layout.activity_fog, null);
        view2 = lf.inflate(R.layout.activity_temp, null);
        view4 = lf.inflate(R.layout.activity_fire, null);
        view1 = lf.inflate(R.layout.activity_main, null);

        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);

        viewPager = (android.support.v4.view.ViewPager)findViewById(R.id.viewPager);

//        titleList = new ArrayList<String>();// 每个页面的Title数据
//        titleList.add("wp");
//        titleList.add("jy");
//        titleList.add("jh");
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);//直接用适配器来完成标题的显示，所以从上面可以看到，我们没有使用PagerTitleStrip。当然你可以使用。
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }
}

