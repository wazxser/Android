package com.zte.wcj;

import android.util.Log;
import android.view.View.OnClickListener;

public class DrawTishiRunnable implements Runnable
{
	MiGong miGong = null;
	int nSecond = 0;
	boolean bover;
	int sleepTime = 100;	//需要绘制的时间

	public DrawTishiRunnable(MiGong miGong)
	{
		// TODO Auto-generated constructor stub
		this.miGong = miGong;
		bover = false;
		nSecond = 0;
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		Log.i("wcj", "DrawTishiRunnable run bover = "+bover);
		while(!bover)
		{	
			try
			{
				Thread.sleep(sleepTime);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(nSecond <= ContstUtil.TIME_STAY_TISHI)	//500ms
			{				
				nSecond ++;
			}
			else	//nSecond > 5
			{
				Log.i("wcj", "DrawTishiRunnable run nSeconde = "+nSecond);
				
				bover = true; //停止绘制
				
				miGong.bNeedDrawTishi = false;	//不需要再提示
			}
		}
	}
}
