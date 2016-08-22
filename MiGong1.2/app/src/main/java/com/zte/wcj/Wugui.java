package com.zte.wcj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class Wugui implements Animal
{
	private GameView gameView;
	private int x;
	private int y;
	private boolean blive;	//是否有生命
	
	private Bitmap[] wugui;
	Dir dir;	//方向
	
	public int npicWidth;	//图片的宽高度
	public int npicHeight;
	
	int nlive;	//金鱼的生命数,初始有５条命
	
	public Wugui(GameView gameView, int i, int j)
	{
		// TODO Auto-generated constructor stub
		this.gameView = gameView;
		this.x = i;
		this.y = j;
		
		blive = true;
		dir = Dir.RIGHT;
		//nlive = 1;	//测试用的
		nlive = ContstUtil.WUGUI_LIVE;
		
		loadBitmap();
	}
	
	private void loadBitmap()
	{
		// TODO Auto-generated method stub
		//UP,RIGHT,DOWN,LEFT
		wugui = new Bitmap[4];
		wugui[0] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.wuguiup);
		wugui[1] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.wuguiright);
		wugui[2] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.wuguidown);
		wugui[3] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.wuguileft);
		
		npicWidth = wugui[0].getWidth();
		npicHeight = wugui[0].getHeight();
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
	
	public boolean isBlive()
	{
		return blive;
	}

	public void setBlive(boolean blive)
	{
		this.blive = blive;
	}

	public Dir getDir()
	{
		return dir;
	}

	public void setDir(Dir dir)
	{
		this.dir = dir;
	}

	public int getNlive()
	{
		return nlive;
	}

	public void setNlive(int nlive)
	{
		this.nlive = nlive;
	}

	@Override
	public void draw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		canvas.drawBitmap(wugui[dir.ordinal()], 
				ContstUtil.STARTGAME_XPOS + x*npicWidth, 
				ContstUtil.STARTGAME_YPOS + y*npicHeight, 
				gameView.paint);
	}

	public void fire()
	{
		Log.i("wcj", "wugui fire point("+x+","+y+").dir = "+dir);
		//UP,RIGHT,DOWN,LEFT
		switch(dir.ordinal())
		{
		case 0:	//Dir.UP
			if(y-1 >= 0)	//空格才发子弹
			{				
				Zidan zidan = new Zidan(gameView, 
						ContstUtil.STARTGAME_XPOS + (x)*gameView.wall.getWidth()+gameView.wall.getWidth()/2-ContstUtil.ZIDAN_WIDTH/2, 
						ContstUtil.STARTGAME_YPOS + (y)*gameView.wall.getHeight()-ContstUtil.ZIDAN_WIDTH, 
						dir, true);
				
				new Thread(new ZidanRun(gameView, zidan)).start();		
				gameView.goodzidans.add(zidan);
				Log.i("wcj", "wugui fire add goodzidans up");
			}
			break;
		case 1:	//Dir.RIGHT
			if((x+1)< ContstUtil.BRICK_X)	//空格才发子弹
			{				
				Zidan zidan = new Zidan(gameView, 
						ContstUtil.STARTGAME_XPOS + (x)*gameView.wall.getWidth()+gameView.wall.getWidth(), 
						ContstUtil.STARTGAME_YPOS + (y)*gameView.wall.getHeight()+gameView.wall.getHeight()/2-ContstUtil.ZIDAN_WIDTH/2, 
						dir, true);
				
				new Thread(new ZidanRun(gameView, zidan)).start();		
				gameView.goodzidans.add(zidan);
				Log.i("wcj", "wugui fire add goodzidans right");
			}
			break;
		case 2:	//Dir.DOWN
			if((y+1)<ContstUtil.BRICK_Y)	//空格才发子弹
			{				
				Zidan zidan = new Zidan(gameView, 
						ContstUtil.STARTGAME_XPOS + (x)*gameView.wall.getWidth()+gameView.wall.getWidth()/2-ContstUtil.ZIDAN_WIDTH/2, 
						ContstUtil.STARTGAME_YPOS + (y)*gameView.wall.getHeight()+gameView.wall.getHeight(), 
						dir, true);
				
				new Thread(new ZidanRun(gameView, zidan)).start();		
				gameView.goodzidans.add(zidan);
				Log.i("wcj", "wugui fire add goodzidans down");
			}
			break;
		case 3:	//Dir.LEFT
			if((x-1)>=0)	//空格才发子弹
			{				
				Zidan zidan = new Zidan(gameView, 
						ContstUtil.STARTGAME_XPOS + (x)*gameView.wall.getWidth()-ContstUtil.ZIDAN_WIDTH,	//子弹的宽度为５像素 
						ContstUtil.STARTGAME_YPOS + (y)*gameView.wall.getHeight()+gameView.wall.getHeight()/2-ContstUtil.ZIDAN_WIDTH, 
						dir, true);
				
				new Thread(new ZidanRun(gameView, zidan)).start();		
				gameView.goodzidans.add(zidan);
				Log.i("wcj", "wugui fire add goodzidans left");
			}
			break;			
		default:
		}
	}

}
