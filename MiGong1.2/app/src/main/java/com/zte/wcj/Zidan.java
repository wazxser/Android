package com.zte.wcj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class Zidan implements Animal
{
	int x;	//x,y为地图中的真实像素坐标
	int y;
	Dir dir;
	
	private boolean good;
	private boolean live = true;

	private GameView gameview;
	
	private Bitmap goodzidan = null;
	private Bitmap badzidan = null;
	
	public int npicWidth;
	public int npicHeight;
	
	public Zidan(GameView gameview)
	{
		this.gameview = gameview;		
		
		loadBitmap();
	}

	public Zidan(int x, int y, Dir dir)
	{
		this.x = x;	
		this.y = y;
		this.dir = dir;
		good = false;	//默认为对方子弹
		live = true;	//子弹有生命
		
		loadBitmap();
	}

	public Zidan(GameView gameview, int x, int y, Dir dir)
	{
		this.gameview = gameview;
		this.x = x;
		this.y = y;
		this.dir = dir;
		good = false;	//默认为对方子弹
		live = true;	//子弹有生命
		
		loadBitmap();
	}
	public Zidan(GameView gameview, int x, int y, Dir dir, boolean good)
	{
		this.gameview = gameview;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.good = good;
		live = true;	//子弹有生命
		
		loadBitmap();
	}
	private void loadBitmap()
	{
		goodzidan = BitmapFactory.decodeResource(gameview.getResources(), R.drawable.goodzidan);
		badzidan  = BitmapFactory.decodeResource(gameview.getResources(), R.drawable.badzidan);
		
		npicWidth = goodzidan.getWidth();
		npicHeight = goodzidan.getHeight();
	}

	@Override
	public void draw(Canvas canvas)
	{
		if(good)	//我方子弹
		{
			canvas.drawBitmap(goodzidan, x, y, gameview.paint);

		}else	//对方子弹
		{
			canvas.drawBitmap(badzidan, x, y, gameview.paint);
			

		}
	}
	//我方子弹打敌方,若成功，则发出爆炸效果
	public boolean killJinyu(Jinyu jinyu)
	{
		
		int x1 =  ContstUtil.STARTGAME_XPOS + jinyu.getX()*jinyu.npicWidth;
		int y1 = ContstUtil.STARTGAME_YPOS + jinyu.getY()*jinyu.npicHeight;

		
		//x,y两个坐标小于两个物体之间的连线，则为相撞
		if( Math.abs(x-x1)< Math.abs((npicWidth+jinyu.npicWidth)/2)
		&&  Math.abs(y-y1)< Math.abs((npicHeight+jinyu.npicHeight)/2) )
		{
			if(good) //我方子弹打金鱼
			{
				
				//相撞了
				gameview.goodzidans.remove(this);	//移除我方子弹			
				gameview.jinyus.remove(jinyu);	//移除敌方
				
				jinyu.setBlive(false);//关闭线程
				live = false;	//关闭线程
				
				gameview.nKillJinyu ++; 	//杀敌
				
				//画爆炸效果
				//产生爆炸效果
				Baoza baoza = new Baoza(gameview, jinyu.getX(), jinyu.getY());
				
				new Thread(new BaozaRun(gameview, baoza)).start();
				
				gameview.baozas.add(baoza);
				
				gameview.xinxi[jinyu.getX()][jinyu.getY()] = KindType.BAOZA;	//此点修正为爆炸
				
			}else //对方子弹打对方
			{
				gameview.badzidans.remove(this);	//移除对方子弹	
				live = false;	//关闭线程
			}
			return true;
		}
		return false;
	}
	
	//敌方打我方,若成功，则发出爆炸效果
	public boolean killWugui(Wugui wugui)
	{
		int x1 =  ContstUtil.STARTGAME_XPOS + wugui.getX()*wugui.npicWidth;
		int y1 = ContstUtil.STARTGAME_YPOS + wugui.getY()*wugui.npicHeight;

		
		//x,y两个坐标小于两个物体之间的连线，则为相撞
		if( Math.abs(x-x1)< Math.abs((npicWidth+wugui.npicWidth)/2)
		&&  Math.abs(y-y1)< Math.abs((npicHeight+wugui.npicHeight)/2) )
		{
			if(good == false) //对方子弹打我方
			{
				//相撞了
				live = false;	//关闭线程
				gameview.badzidans.remove(this);	
				gameview.wugui.setNlive(gameview.wugui.getNlive()-1);	//生命数减１
				
				
				//画爆炸效果
				//产生爆炸效果
				Baoza baoza = new Baoza(gameview, wugui.getX(), wugui.getY());			
				new Thread(new BaozaRun(gameview, baoza)).start();			
				gameview.baozas.add(baoza);
				
				gameview.bKillWugui = true;	//被杀死了
				gameview.createOneWugui();	//创建一个新的乌龟
				gameview.regetxinxi();	//重新得到信息列表
				
				gameview.xinxi[wugui.getX()][wugui.getY()] = KindType.BAOZA;	//此点修正为爆炸
				
				if(gameview.wugui.getNlive() < 1)	//游戏结束
				{
					gameview.bGameOver = true;	
					gameview.migong.mHandler.sendEmptyMessage(ContstUtil.MSG_LOSTHISGAME);
				}
			}else //我方打我方
			{
				gameview.goodzidans.remove(this);	//移除对方子弹
				live = false;	//关闭线程
			}
			return true;
		}
		return false;
	}
	
	public void killZidan(Zidan zidan)
	{
		if(good && !zidan.isGood()
		|| !good && zidan.isGood())	//双方不一样才相互的打
		{
			int x1 = zidan.getX();
			int y1 = zidan.getY();
			
			if( Math.abs(x-x1)< npicWidth
			&&  Math.abs(y-y1)< npicHeight)
			{
				if(good == true)
				{
					gameview.goodzidans.remove(this);	//移除子弹
					gameview.badzidans.remove(zidan);
				}else
				{
					gameview.goodzidans.remove(zidan);	//移除子弹
					gameview.badzidans.remove(this);
				}
				
				live = false;	//关闭线程
				zidan.setLive(false);
			}
		}
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

	public Dir getDir()
	{
		return dir;
	}

	public void setDir(Dir dir)
	{
		this.dir = dir;
	}

	public boolean isGood()
	{
		return good;
	}

	public void setGood(boolean good)
	{
		this.good = good;
	}

	public boolean isLive()
	{
		return live;
	}

	public void setLive(boolean live)
	{
		this.live = live;
	}


		
	
}
