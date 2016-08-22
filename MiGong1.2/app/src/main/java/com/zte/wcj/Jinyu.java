package com.zte.wcj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class Jinyu implements Animal
{
	private GameView gameView;
	private int x;
	private int y;
	private boolean blive;	//是否有生命
	
	private Bitmap[] jinyus;
	Dir dir;	//方向
	
	public int npicWidth;
	public int npicHeight;
	
	int nlive;	//金鱼的生命数
	
	public Jinyu(GameView gameView, int x, int y)
	{
		// TODO Auto-generated constructor stub
		this.gameView = gameView;
		this.x = x;
		this.y = y;
		
		blive = true;
		dir = Dir.RIGHT;
		
		loadBitmap();
	}
	
	public Jinyu(GameView gameView2, int x2, int y2, int i)
	{
		this(gameView2, x2, y2);
		
		switch(i)
		{
		case 0:
			dir = Dir.UP;
			break;
		case 1:
			dir = Dir.RIGHT;
			break;
		case 2:
			dir = Dir.DOWN;
			break;
		case 3:
			dir = Dir.LEFT;
			break;			
		default:
			dir = Dir.RIGHT;
		}
	}

	private void loadBitmap()
	{
		// TODO Auto-generated method stub
		//UP,RIGHT,DOWN,LEFT
		jinyus = new Bitmap[4];
		jinyus[0] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.jinyuup);
		jinyus[1] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.jinyuright);
		jinyus[2] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.jinyudown);
		jinyus[3] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.jinyuleft);
		
		npicWidth = jinyus[0].getWidth();
		npicHeight = jinyus[0].getHeight();
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

	@Override
	public void draw(Canvas canvas)
	{
		canvas.drawBitmap(jinyus[dir.ordinal()], 
				ContstUtil.STARTGAME_XPOS + x*npicWidth, 
				ContstUtil.STARTGAME_YPOS + y*npicHeight, 
				gameView.paint);
	}
	public void changeDir()	//发射子弹后，改变金鱼的方向
	{
		//UP,RIGHT,DOWN,LEFT
		switch(dir.ordinal())
		{
		case 0:
			dir= Dir.DOWN;
			break;
		case 1:
			dir= Dir.LEFT;
			break;
		case 2:
			dir= Dir.UP;
			break;
		case 3:
			dir= Dir.RIGHT;
			break;
		default:
			break;
		}
		
	}
	public void fire()
	{
		//UP,RIGHT,DOWN,LEFT
		switch(dir.ordinal())
		{
		case 0:	//Dir.UP
			if(y-1 >= 0)	//空格才发子弹
			{				
				Zidan zidan = new Zidan(gameView, 
						ContstUtil.STARTGAME_XPOS + (x)*gameView.wall.getWidth()+gameView.wall.getWidth()/2-ContstUtil.ZIDAN_WIDTH/2, 
						ContstUtil.STARTGAME_YPOS + (y)*gameView.wall.getHeight()-ContstUtil.ZIDAN_WIDTH, 
						dir, false);
				
				new Thread(new ZidanRun(gameView, zidan)).start();		
				gameView.badzidans.add(zidan);
				//changeDir();	//改变金鱼的方向
			}
			break;
		case 1:	//Dir.RIGHT
			if((x+1)< ContstUtil.BRICK_X)	//空格才发子弹
			{				
				Zidan zidan = new Zidan(gameView, 
						ContstUtil.STARTGAME_XPOS + (x)*gameView.wall.getWidth()+gameView.wall.getWidth(), 
						ContstUtil.STARTGAME_YPOS + (y)*gameView.wall.getHeight()+gameView.wall.getHeight()/2-ContstUtil.ZIDAN_WIDTH/2, 
						dir, false);
				
				new Thread(new ZidanRun(gameView, zidan)).start();		
				gameView.badzidans.add(zidan);
				//changeDir();	//改变金鱼的方向
			}
			break;
		case 2:	//Dir.DOWN
			if((y+1)<ContstUtil.BRICK_Y)	//空格才发子弹
			{				
				Zidan zidan = new Zidan(gameView, 
						ContstUtil.STARTGAME_XPOS + (x)*gameView.wall.getWidth()+gameView.wall.getWidth()/2-ContstUtil.ZIDAN_WIDTH/2, 
						ContstUtil.STARTGAME_YPOS + (y)*gameView.wall.getHeight()+gameView.wall.getHeight(), 
						dir, false);
				
				new Thread(new ZidanRun(gameView, zidan)).start();		
				gameView.badzidans.add(zidan);
				//changeDir();	//改变金鱼的方向
			}
			break;
		case 3:	//Dir.LEFT
			if((x-1)>=0)	//空格才发子弹
			{				
				Zidan zidan = new Zidan(gameView, 
						ContstUtil.STARTGAME_XPOS + (x)*gameView.wall.getWidth()-ContstUtil.ZIDAN_WIDTH, 
						ContstUtil.STARTGAME_YPOS + (y)*gameView.wall.getHeight()+gameView.wall.getHeight()/2-ContstUtil.ZIDAN_WIDTH/2, 
						dir, false);
				
				new Thread(new ZidanRun(gameView, zidan)).start();		
				gameView.badzidans.add(zidan);
				//changeDir();	//改变金鱼的方向
			}
			break;			
		default:
		}
	}
}
