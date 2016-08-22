package com.zte.wcj;

public class BaozaRun implements Runnable
{
	GameView gameview;
	Baoza baoza;
	private boolean bover;
	
	public BaozaRun(GameView gameview, Baoza baoza)
	{
		this.gameview = gameview;
		this.baoza = baoza;
		bover = false;
	}

	@Override
	public void run()
	{
		int ntype = 1;
		int x = baoza.getX();
		int y = baoza.getY();
		
		while(!bover)
		{
			ntype = baoza.getNType();
			
			ntype++;
			
			if(ntype<=5)
			{
				baoza.setNType(ntype);
			}else
			{
				gameview.baozas.remove(baoza);	//移除这个点
				bover = true;	//爆炸结束
				gameview.xinxi[baoza.getX()][baoza.getY()] = KindType.EMPTY;	//重新置为空
			}
			
			try
			{
				Thread.sleep(ContstUtil.TIME_SLEEP* ContstUtil.BAOZA_TIME);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
