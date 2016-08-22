package com.zte.wcj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Baoza implements Animal
{
	int x;	//x,y不是绝对的坐标，为数组中的坐标相一致
	int y;
	
	int nType;	//1---5
	
	private GameView gameview;
	private Bitmap[] baozas = null;
	
	public Baoza(GameView gameview, int x, int y)
	{
		this.gameview = gameview;
		this.x = x;
		this.y = y;
		
		nType = 1;	//初始值为第一张图片
		
		loadBitmap();
	}
	
	private void loadBitmap()
	{
		baozas = new Bitmap[5];
		
		baozas[0] = BitmapFactory.decodeResource(gameview.getResources(), R.drawable.baoza1);
		baozas[1] = BitmapFactory.decodeResource(gameview.getResources(), R.drawable.baoza2);
		baozas[2] = BitmapFactory.decodeResource(gameview.getResources(), R.drawable.baoza3);
		baozas[3] = BitmapFactory.decodeResource(gameview.getResources(), R.drawable.baoza4);
		baozas[4] = BitmapFactory.decodeResource(gameview.getResources(), R.drawable.baoza5);
	}

	@Override
	public void draw(Canvas canvas)
	{
		canvas.drawBitmap(baozas[nType-1],
				ContstUtil.STARTGAME_XPOS + x*baozas[0].getWidth(), 
				ContstUtil.STARTGAME_YPOS + y*baozas[0].getHeight(),
				gameview.paint);
	}

	public int getNType()
	{
		return nType;
	}

	public void setNType(int type)
	{
		nType = type;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}
}
