package com.zte.wcj;

import android.util.Log;

public class ZidanRun implements Runnable
{
	private GameView gameview;
	private Zidan zidan;
	
	public ZidanRun(GameView gameview, Zidan zidan)
	{
		this.gameview = gameview;
		this.zidan = zidan;
	}

	@Override
	public void run()
	{
		
		while(zidan.isLive())
		{
			int x = zidan.getX();
			int y = zidan.getY();
			Dir dir = zidan.getDir();
			
			if(zidan.getDir() == Dir.UP)
			{
				y = y - ContstUtil.ZIDAN_SPEED;
			}else if(zidan.getDir() == Dir.RIGHT)
			{
				x = x + ContstUtil.ZIDAN_SPEED;
			}else if(zidan.getDir() == Dir.DOWN)
			{
				y = y + ContstUtil.ZIDAN_SPEED;
			}else	//dir.left
			{
				x = x - ContstUtil.ZIDAN_SPEED;
			}
			//先看下子弹是不是越界了
			if(x < ContstUtil.STARTGAME_XPOS + 0*gameview.wall.getWidth()
			|| x > ContstUtil.STARTGAME_XPOS + (ContstUtil.BRICK_X-1)*gameview.wall.getWidth()
			|| y < ContstUtil.STARTGAME_YPOS + 0*gameview.wall.getHeight()
			|| y > ContstUtil.STARTGAME_YPOS + (ContstUtil.BRICK_Y-1)*gameview.wall.getHeight())
			{
				Log.i("wcj", "ZidanRun run remove this zidan ("+x+","+y+")");
				zidan.setLive(false);	//子弹消亡
				
				if(zidan.isGood())//我方子弹
				{
					gameview.goodzidans.remove(zidan);
				}else	//敌方子弹
				{
					gameview.badzidans.remove(zidan);
				}
			}
			
			//根据x,y得到坐标中的x0, y0
			int x0 = (x- ContstUtil.STARTGAME_XPOS)/gameview.wall.getWidth();
			int y0 = (y- ContstUtil.STARTGAME_YPOS)/gameview.wall.getHeight();
			
			//Log.i("wcj", "ZidanRun run gameview.mymap["+x0+"]["+y0+"] = "+gameview.mymap[x0][y0]);
			
			if(x0 >= 0 && x0 <ContstUtil.BRICK_X-1
			&& y0 >= 0 && y0 <ContstUtil.BRICK_Y-1
			&& gameview.xinxi[x0][y0] == KindType.WALL )	//是墙面，也要删除这个点
			{
				//Log.i("wcj", "ZidanRun run remove this zidan for wall");
				zidan.setLive(false);	//子弹消亡
				
				if(zidan.isGood())//我方子弹
				{
					gameview.goodzidans.remove(zidan);
				}else	//敌方子弹
				{
					gameview.badzidans.remove(zidan);
				}

			}
			
			//以上情况都不是,重新设置坐标
			
			zidan.setX(x);
			zidan.setY(y);
			
			try
			{
				Thread.sleep(ContstUtil.TIME_SLEEP * ContstUtil.ZIDAN_TIME );
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
