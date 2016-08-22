package com.example.moon.monkey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by moon on 2016/7/27.
 */
public class ChartView extends View {
    private int XPoint = 60;   //原点x坐标,距左边的长度
    private int YPoint = 560;  //原点y坐标，距上边的长度
    private int XScale = 100;  //刻度长度
    private int YScale = 40;
    private int XLength = 500;  //x轴最大长度
    private int YLength = 240;  //y轴最大长度

    //x轴刻度数目
    private int MaxDataSize = XLength / XScale;

    //点的y轴坐标
    private List<Integer> data = new ArrayList<Integer>();

    //y轴刻度
    private String[] YLabel = new String[YLength / YScale];

    //
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 0x1234){
                ChartView.this.invalidate();
            }
        };
    };

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        for(int i=0; i<YLabel.length; i++){
            YLabel[i] = String.valueOf(i + 1);
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(data.size() >= MaxDataSize){
                        data.remove(0);
                    }
                    data.add(new Random().nextInt(4) + 1);
                    handler.sendEmptyMessage(0x1234);
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true); //去锯齿
        paint.setColor(Color.BLUE);

        //画Y轴
        canvas.drawLine(XPoint, YPoint, XPoint, YPoint - YLength, paint);

        //画Y轴箭头
        canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint-YLength + 6, paint);
        canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint-YLength + 6 ,paint);

        //添加刻度和文字
        for(int i=0; i * YScale < YLength; i++) {
            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i * YScale, paint);  //刻度

            canvas.drawText(YLabel[i], XPoint - 30, YPoint - i * YScale, paint);//文字
        }

        //画X轴
        canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, paint);
        if(data.size() > 1){
            for(int i=1; i<data.size(); i++){
                canvas.drawLine(XPoint + (i-1) * XScale, YPoint - data.get(i-1) * YScale,
                        XPoint + i * XScale, YPoint - data.get(i) * YScale, paint);
            }
        }
    }
}
