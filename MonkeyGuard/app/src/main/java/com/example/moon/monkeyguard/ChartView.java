package com.example.moon.monkeyguard;

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
    private int XPoint = 100;
    private int YPoint = 600;
    private int XScale = 120;  //刻度长度
    private int YScale = 60;
    private int XLength = 600;
    private int YLength = 480;

    private int MaxDataSize = XLength / XScale;

    private List<Integer> data = new ArrayList<Integer>();

    private String[] YLabel = new String[YLength / YScale];

    private int message = 30;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 0x1234){
                ChartView.this.invalidate();
            }
        };
    };

    public void setMessage(int message){
        this.message = message;
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int j = -10;
        for(int i = 0; i < YLabel.length; i++){
            YLabel[i] = String.valueOf(j);
            j = j + 10;
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
                    data.add(message);
                    handler.sendEmptyMessage(0x1234);
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true); //去锯齿
        paint.setColor(Color.WHITE);

//      画Y轴
      canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);

//      Y轴箭头
      canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint-YLength + 6, paint);
      canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint-YLength + 6 ,paint);

        //添加刻度和文字
        for(int i=0; i * YScale < YLength; i++) {
            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i * YScale, paint);  //刻度

            canvas.drawText(YLabel[i], XPoint - 30, YPoint - i * YScale, paint);
        }

        //画X轴
        canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, paint);

        if(data.size() > 1){
            for(int i=1; i<data.size(); i++){
                float m = (float)data.get(i - 1);
                float startY = YPoint - ( m + 10 ) / 10 * YScale;
                float n = (float)data.get(i);
                float stopY = YPoint - ( n + 10 ) / 10 * YScale;
                canvas.drawLine(XPoint + (i-1) * XScale, startY, XPoint + i * XScale, stopY, paint);
            }
        }
    }
}
