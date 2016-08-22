package com.zte.wcj;

import android.util.Log;
import java.util.Random;

public class JinyuRun implements Runnable
{
	private GameView gameview;
	private Jinyu jinyu;
	
	public JinyuRun(GameView gameView, Jinyu jinyu)
	{
		this.gameview = gameView;
		this.jinyu = jinyu;
	}

	@Override
	public void run()
	{
		int movedir = 0;
		int changedir = 0;
		int needfire = 0;
		int count = 0;
		Dir dir = Dir.RIGHT;
		
		int pinli =4; //方向切换频率（４,６）
		
		while(jinyu.isBlive())	//金鱼还存活着
		{
			if(gameview.migong.bisInSearcd == false)	//搜索时，金鱼不能移动，不在搜索时，才移动
			{
				
				needfire = (new Random()).nextInt(10);
				
				if( needfire == 8)
				{
					jinyu.fire();	//发射子弹
				}
				
				synchronized (jinyu)
				{				
					if(count%pinli == 0)	//每隔两秒才修改下方向
					{
						movedir = (new Random()).nextInt(4);
						
						if(pinli == 4) pinli = 6;
						else           pinli = 4;
					}
					move(movedir);
				}
			
			}
			try
			{
				Thread.sleep(ContstUtil.TIME_SLEEP* ContstUtil.JINYU_SPEED);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}
	}

	private void move(int ran)
	{
		int x = jinyu.getX();
		int y = jinyu.getY();

		//UP,RIGHT,DOWN,LEFT
		
		switch(ran)
		{
		case 0:	//Dir.UP
			if(jinyu.getDir() != Dir.UP)	//方向不同的，先改变方向
			{
				Log.i("wcj", "JinyuRun move predir = "+jinyu.getDir()+" now dir = "+Dir.UP);
				jinyu.setDir(Dir.UP);
				return;
			}
			y--;
			if(y>=0 && gameview.xinxi[x][y] == KindType.EMPTY)	//是空格，则可以到达
			{

				jinyu.setX(x);
				jinyu.setY(y);
				//对信息表中进行修正
				gameview.xinxi[x][y] = KindType.JINYU;	//当前的为金鱼
				gameview.xinxi[x][y+1] = KindType.EMPTY;	//之前的为空格
			}
			break;
		case 1:	//Dir.RIGHT
			if(jinyu.getDir() != Dir.RIGHT)	//方向不同的，先改变方向
			{
				Log.i("wcj", "JinyuRun move predir = "+jinyu.getDir()+" now dir = "+Dir.RIGHT);
				jinyu.setDir(Dir.RIGHT);
				return;
			}
			x++;
			if(x == gameview.end.x && y == gameview.end.y)	//终点不让金鱼进
			{
				Log.i("wcj", "JinyuRun move won't reach end point");
				jinyu.setDir(Dir.LEFT);
				return;
			}
			if(x<ContstUtil.BRICK_X && gameview.xinxi[x][y] == KindType.EMPTY)	//是空格，则可以到达
			{

				jinyu.setX(x);
				jinyu.setY(y);
				//对信息表中进行修正
				gameview.xinxi[x][y] = KindType.JINYU;	//当前的为金鱼
				gameview.xinxi[x-1][y] = KindType.EMPTY;	//之前的为空格
			}
			break;
		case 2:	//Dir.DOWN
			if(jinyu.getDir() != Dir.DOWN)	//方向不同的，先改变方向
			{
				Log.i("wcj", "JinyuRun move predir = "+jinyu.getDir()+" now dir = "+Dir.DOWN);
				jinyu.setDir(Dir.DOWN);
				return;
			}
			y++;
			if(y<ContstUtil.BRICK_Y && gameview.xinxi[x][y] == KindType.EMPTY)	//是空格，则可以到达
			{

				jinyu.setX(x);
				jinyu.setY(y);
				//对信息表中进行修正
				gameview.xinxi[x][y] = KindType.JINYU;	//当前的为金鱼
				gameview.xinxi[x][y-1] = KindType.EMPTY;	//之前的为空格
			}
			break;
		case 3:	//Dir.LEFT
			if(jinyu.getDir() != Dir.LEFT)	//方向不同的，先改变方向
			{
				Log.i("wcj", "JinyuRun move predir = "+jinyu.getDir()+" now dir = "+Dir.LEFT);
				jinyu.setDir(Dir.LEFT);
				return;
			}
			x--;
			if(x == gameview.start.x && y == gameview.start.y)	//起点不让金鱼进
			{
				jinyu.setDir(Dir.RIGHT);
				Log.i("wcj", "JinyuRun move won't reach start point");
				return;
			}
			if(x>=0 && gameview.xinxi[x][y] == KindType.EMPTY)	//是空格，则可以到达
			{

				jinyu.setX(x);
				jinyu.setY(y);
				//对信息表中进行修正
				gameview.xinxi[x][y] = KindType.JINYU;	//当前的为金鱼
				gameview.xinxi[x+1][y] = KindType.EMPTY;	//之前的为空格
			}
			break;			
		default:
			Log.i("wcj", "JinyuRun error ran ");
			break;
		}		
	}

	
}
