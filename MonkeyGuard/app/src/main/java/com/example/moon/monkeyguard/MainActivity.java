package com.example.moon.monkeyguard;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//主类，差不多所有的功能都在这里实现吧
public class MainActivity extends NotificationBase {
    //接收常连接的消息
    public Handler handler;

    private Mqtt mqtt;

    private View view1, view2, view3, view4;//需要滑动的页卡
    private ViewPager viewPager;//viewpager
    private List<View> viewList;//把需要滑动的页卡添加到这个list中
    private Intent intent;
    private List<String> titleList;//viewpager的标题

    //传感器
    String sensorName [] = {"temp_value", "fire_value", "fog_value"};
    int senorValue [] = {10, 100, 150};

    //Notification
    //public NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    int notifyId = 100;

    //传感器的值的呈现
    TextView temp_text;
    TextView fog_text;
    TextView fire_text;
    TextView temp_main;
    TextView fog_main;
    TextView fog_main_ok;
    TextView fire_main_ok;

    //折线图实例
    ChartView tempChart = null;
    FogChartView fogChart = null;
    FireChartView fireChart = null;

    private Handler tempHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 0x1234){
                temp_text.setText(String.valueOf(senorValue[0]));
                if(senorValue[0] < 20){
                    view2.setBackgroundColor(getResources().getColor(R.color.temp_back1));
                }
                else if(senorValue[1] < 40){
                    view2.setBackgroundColor(getResources().getColor(R.color.temp_back2));
                }
                else {
                    view2.setBackgroundColor(getResources().getColor(R.color.temp_back3));
                }
            }
        };
    };

    private Handler fogHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 0x2345){
                fog_text.setText(String.valueOf(senorValue[2]));
            }
        };
    };

    private Handler fireHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 0x3456){
                fire_text.setText(String.valueOf(senorValue[1]));
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        LayoutInflater lf = getLayoutInflater().from(this);

        view1 = lf.inflate(R.layout.activity_main, null);
        view2 = lf.inflate(R.layout.activity_temp, null);
        view3 = lf.inflate(R.layout.activity_fog, null);
        view4 = lf.inflate(R.layout.activity_fire, null);

        initViewPager();
        initMainView();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
              @Override
              public void onPageSelected(int arg0) {
                  // TODO Auto-generated method stub
                  switch (arg0) {
                      case 0:
                          temp_main = (TextView)findViewById(R.id.temp_main);
                          fog_main = (TextView)findViewById(R.id.fog_main);
                          fog_main_ok = (TextView)findViewById(R.id.fog_main_ok);
                          fire_main_ok = (TextView)findViewById(R.id.fire_main_ok);

                          temp_main.setText(String.valueOf(senorValue[0]));
                          fog_main.setText(String.valueOf(senorValue[2]));

                          if(senorValue[1] < 200){
                              fire_main_ok.setText("OK");
                          }
                          else{
                              fire_main_ok.setText("!!");
                          }
                          if(senorValue[2] < 500){
                              fog_main_ok.setText("OK");
                          }
                          else{
                              fog_main_ok.setText("!!");
                          }

                          ImageView mesgImg = (ImageView)findViewById(R.id.mesg_img);
                          mesgImg.setOnClickListener(new View.OnClickListener(){
                              @Override
                              public void onClick(View v) {
                                  Intent intent = new Intent(MainActivity.this, MesgActivity.class);
                                  Bundle bundle = new Bundle();
                                  intent.putExtras(bundle);
                                  startActivityForResult(intent, 1);
                              }
                          });
                          break;
                      case 1:
                          temp_text = (TextView)findViewById(R.id.temp_text);
                          temp_text.setText(String.valueOf(senorValue[0]) + "℃");
                          tempChart = (ChartView)findViewById(R.id.tempChart);

                          new Thread(new Runnable() {
                              @Override
                              public void run() {
                                  while(true){
                                      try {
                                          Thread.sleep(1000);
                                      } catch (InterruptedException e) {
                                          e.printStackTrace();
                                      }

                                      tempHandler.sendEmptyMessage(0x1234);
                                  }
                              }
                          }).start();

                          break;
                      case 2:
                          fog_text = (TextView)findViewById(R.id.fog_text);
                          fog_text.setText(String.valueOf(senorValue[2]));

                          fogChart = (FogChartView)findViewById(R.id.fogChart);

                          new Thread(new Runnable() {
                              @Override
                              public void run() {
                                  while(true){
                                      try {
                                          Thread.sleep(1000);
                                      } catch (InterruptedException e) {
                                          e.printStackTrace();
                                      }

                                      fogHandler.sendEmptyMessage(0x2345);
                                  }
                              }
                          }).start();

                          break;
                      case 3:
                          fire_text = (TextView)findViewById(R.id.fire_text);
                          fire_text.setText(String.valueOf(senorValue[1]));

                          fireChart = (FireChartView)findViewById(R.id.fireChart);

                          new Thread(new Runnable() {
                              @Override
                              public void run() {
                                  while(true){
                                      try {
                                          Thread.sleep(1000);
                                      } catch (InterruptedException e) {
                                          e.printStackTrace();
                                      }

                                      fireHandler.sendEmptyMessage(0x3456);
                                  }
                              }
                          }).start();

                          break;
                      default:
                          break;
                  }
              }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

//        mqtt = new Mqtt();
//        setMqtt();
//
//        handler = new Handler() {
//            public void handleMessage(Message msg) {
//                if (msg.what == 1) {
//                    try{
//                        decodeJson(mqtt.ret);
//
//                        if(tempChart != null){
//                            tempChart.setMessage(senorValue[0]);
//                        }
//                        else{
//                            System.out.println("tempChart is null");
//                        }
//
//                        if(fogChart != null){
//                            fogChart.setMessage(senorValue[2]);
//                        }
//                        else{
//                            System.out.println("fogChart is null");
//                        }
//
//                        if(fireChart != null){
//                            fireChart.setMessage(senorValue[1]);
//                        }
//                        else{
//                            System.out.println("fireChart is null");
//                        }
//
//                        initNotify();
//                        if(senorValue[0] > 50){
//                            showTempNotify();
//                        }
//                        else if(senorValue[1] > 100){
//                            showFireNotify();
//                        }
//                        else if(senorValue[2] > 500){
//                            showFogNotify();
//                        }
//                    }
//                    catch(JSONException e){
//                        System.out.println("Json解析出错");
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        mqtt.setHandler(handler);
    }

    public void updateView(){

    }

    public void initViewPager(){
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);

        viewPager = (android.support.v4.view.ViewPager)findViewById(R.id.viewPager);

        PagerAdapter pagerAdapter = new PagerAdapter(){

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

    public void initMainView(){
        RelativeLayout rTemp = (RelativeLayout)view1.findViewById(R.id.temp_circle);
        RelativeLayout rFog = (RelativeLayout)view1.findViewById(R.id.fog_circle);
        RelativeLayout rFire = (RelativeLayout)view1.findViewById(R.id.fire_circle);
        RelativeLayout rOther = (RelativeLayout)view1.findViewById(R.id.other_circle);

        rTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TempActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        rFog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FogActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        rFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FireActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        rOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_temp);
            }
        });
    }

    public void setMqtt(){
        mqtt.setBroker("tcp://115.29.109.27:61613");
        mqtt.setTopic("sensor");
        mqtt.setUserName("admin");
        mqtt.setPassword("password");
        mqtt.setContent("content");
        mqtt.setQos(1);
        mqtt.setClientId("hahahahhhahah");

        mqtt.init();
        mqtt.listen();
    }

    public void decodeJson(String jsonValue) throws JSONException{
        if(jsonValue != null){
            JSONObject sensorJson = new JSONObject(mqtt.ret);
            Iterator iterator = sensorJson.keys();
            int i = 0;
            while(iterator.hasNext()) {
                double d = Double.parseDouble(sensorJson.getString(sensorName[i]));
                senorValue[i] = (int) d;
                iterator.next();
                i++;
            }
        }
    }

    public void initNotify(){
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量
                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
//				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.logo256);
    }

    /** 显示通知栏 */
    public void showTempNotify(){
        mBuilder.setContentTitle("#温度#")
                .setContentText("家中温度异常，请及时查看")
//				.setNumber(number)//显示数量
                .setTicker("测试通知来啦");//通知首次出现在通知栏，带上升动画效果的
        mNotificationManager.notify(notifyId, mBuilder.build());
//		mNotification.notify(getResources().getString(R.string.app_name), notiId, mBuilder.build());
    }

    public void showFogNotify(){
        mBuilder.setContentTitle("#可燃气体#")
                .setContentText("家中可燃气体指数过高，请及时查看")
//				.setNumber(number)//显示数量
                .setTicker("测试通知来啦");//通知首次出现在通知栏，带上升动画效果的
        mNotificationManager.notify(notifyId, mBuilder.build());
    }

    public void showFireNotify(){
        mBuilder.setContentTitle("#火焰#")
                .setContentText("测到家中有火焰，请及时查看")
                   .setTicker("测试通知来啦");//通知首次出现在通知栏，带上升动画效果的
        mNotificationManager.notify(notifyId, mBuilder.build());
    }
}
