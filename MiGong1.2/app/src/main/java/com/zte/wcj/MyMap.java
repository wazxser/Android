package com.zte.wcj;

import android.util.Log;

public class MyMap
{
	int levle;	//
	static int[][][] bricks = new int[][][]
	{	
			{	//第一张图
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,0,0,0,0,0,1,0,0,0,1, },
			{ 1,0,1,0,1,0,1,0,0,1,0,1, },
			{ 1,1,0,1,0,0,0,0,1,0,0,1, },
			{ 1,0,0,0,0,1,0,0,0,0,0,1, },
			{ 1,0,1,0,1,0,0,1,0,1,0,1, },
			{ 1,0,0,1,0,0,1,0,0,1,0,1, },
			{ 1,1,0,1,0,1,0,1,0,0,1,1, },
			{ 1,0,0,0,0,0,0,0,0,1,0,1, },
			{ 1,0,1,1,0,1,0,0,1,0,0,1, },
			{ 1,0,0,1,0,1,1,0,0,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			},
			//第二张图
			{
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,1,0,0,1,0,0,0,0,1,1, },
			{ 1,0,0,0,1,0,1,1,0,0,1,1, },
			{ 1,0,1,0,0,0,0,0,0,1,0,1, },
			{ 1,0,0,1,0,1,0,1,0,0,0,1, },
			{ 1,1,0,1,1,0,0,0,1,0,0,1, },
			{ 1,0,0,0,0,0,1,0,0,1,0,1, },
			{ 1,0,1,1,0,1,0,1,0,0,0,1, },
			{ 1,0,0,0,1,1,0,0,0,1,1,1, },
			{ 1,1,0,0,0,1,0,1,0,0,0,1, },
			{ 1,0,0,0,1,0,0,0,1,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			},
			//第三张图
			{
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,0,0,1,0,1,0,0,0,0,1, },
			{ 1,0,0,1,0,0,1,1,1,0,0,1, },
			{ 1,0,1,0,0,1,0,0,0,0,1,1, },
			{ 1,0,0,0,0,0,0,1,0,1,0,1, },
			{ 1,0,1,0,1,0,1,1,0,0,0,1, },
			{ 1,0,0,0,0,0,0,0,0,1,1,1, },
			{ 1,1,0,1,0,1,0,0,1,0,0,1, },
			{ 1,0,1,1,0,0,0,0,0,0,1,1, },
			{ 1,0,0,0,0,1,1,0,0,0,0,1, },
			{ 1,1,0,1,1,0,0,0,0,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			},
			//第四张图
			{
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,1,0,0,0,1,1,0,1,0,1, },
			{ 1,0,0,0,1,1,0,1,0,1,0,1, },
			{ 1,0,0,1,0,0,0,0,0,0,0,1, },
			{ 1,1,0,0,0,1,1,1,0,0,0,1, },
			{ 1,0,0,1,0,1,0,0,0,0,0,1, },
			{ 1,1,0,1,0,0,0,0,1,1,0,1, },
			{ 1,0,0,0,0,1,1,0,0,0,1,1, },
			{ 1,1,1,0,0,0,0,1,0,0,1,1, },
			{ 1,0,0,0,1,0,1,0,1,0,0,1, },
			{ 1,0,1,0,1,0,0,0,0,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			},
			//第五张图
			{
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,1,0,0,0,0,1,0,0,0,1, },
			{ 1,0,0,0,1,0,0,0,0,1,1,1, },
			{ 1,1,0,0,1,0,1,1,0,0,0,1, },
			{ 1,1,0,0,1,0,0,1,0,0,1,1, },
			{ 1,0,0,1,0,0,0,0,1,0,0,1, },
			{ 1,1,0,0,0,0,0,1,1,0,1,1, },
			{ 1,0,1,0,1,1,1,0,0,0,1,1, },
			{ 1,0,0,0,0,0,0,1,1,0,0,1, },
			{ 1,0,0,1,0,0,1,0,1,0,0,1, },
			{ 1,0,0,0,1,1,0,0,0,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },	
			},
			//第六张地图
			{
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,0,1,0,1,0,1,0,0,0,1, },
			{ 1,0,0,0,1,0,0,0,0,0,1,1, },
			{ 1,1,0,1,0,0,1,0,1,0,0,1, },
			{ 1,0,0,0,0,0,0,1,0,1,1,1, },
			{ 1,0,0,1,1,0,0,1,0,0,0,1, },
			{ 1,0,1,0,0,0,0,0,0,0,0,1, },
			{ 1,0,1,0,0,0,0,1,1,0,0,1, },
			{ 1,1,0,0,1,0,1,0,0,1,0,1, },
			{ 1,1,0,0,1,0,0,0,1,0,0,1, },
			{ 1,0,0,1,1,0,1,0,1,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			},
			//第七张地图
			{
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,0,1,0,0,0,1,0,1,0,1, },
			{ 1,0,0,1,1,0,1,1,0,1,0,1, },
			{ 1,0,1,0,1,0,0,1,0,1,0,1, },
			{ 1,0,0,0,0,0,0,0,0,0,0,1, },
			{ 1,0,0,1,0,1,1,1,0,0,1,1, },
			{ 1,0,1,0,0,0,0,0,0,0,1,1, },
			{ 1,0,0,0,0,0,1,0,0,0,0,1, },
			{ 1,0,0,1,0,0,0,0,0,1,1,1, },
			{ 1,0,1,0,0,0,0,0,0,0,0,1, },
			{ 1,0,0,0,0,1,1,0,1,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			},
			//第八张地图
			{
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,0,1,0,1,1,0,0,1,0,1, },
			{ 1,0,0,1,0,0,0,0,1,0,0,1, },
			{ 1,1,0,0,0,1,0,0,0,0,0,1, },
			{ 1,0,0,1,0,1,1,0,1,1,0,1, },
			{ 1,1,0,1,1,0,0,1,0,0,1,1, },
			{ 1,0,0,1,0,1,0,1,0,0,0,1, },
			{ 1,1,0,0,0,0,0,0,0,1,1,1, },
			{ 1,1,0,1,0,0,1,0,0,0,0,1, },
			{ 1,0,0,0,0,0,1,0,1,0,0,1, },
			{ 1,0,1,0,0,1,0,0,0,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			},
			//第九张地图
			{
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,0,1,0,1,0,1,0,1,0,1, },
			{ 1,0,0,0,0,0,0,0,0,0,0,1, },
			{ 1,1,0,1,0,0,1,0,1,0,0,1, },
			{ 1,0,0,0,0,0,0,1,1,0,1,1, },
			{ 1,0,0,1,1,0,1,0,0,0,1,1, },
			{ 1,0,1,0,0,1,1,1,0,0,0,1, },
			{ 1,0,0,0,1,0,0,0,0,0,1,1, },
			{ 1,1,1,0,0,1,0,1,1,0,0,1, },
			{ 1,0,0,0,0,1,0,0,0,0,0,1, },
			{ 1,0,0,1,0,0,0,1,1,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			},
			//第十张地图
			{
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			{ 0,0,0,0,1,0,0,1,0,0,1,1, },
			{ 1,0,0,1,1,0,1,0,0,1,0,1, },
			{ 1,1,0,0,0,0,0,0,1,0,0,1, },
			{ 1,0,0,1,0,1,1,0,0,0,0,1, },
			{ 1,0,1,0,1,0,0,1,1,0,0,1, },
			{ 1,0,1,0,1,0,0,0,0,0,0,1, },
			{ 1,0,1,0,0,0,0,1,1,0,1,1, },
			{ 1,0,0,0,1,0,1,0,0,1,0,1, },
			{ 1,1,0,1,0,0,0,1,0,0,0,1, },
			{ 1,0,0,0,0,1,0,0,0,0,0,0, },
			{ 1,1,1,1,1,1,1,1,1,1,1,1, },
			}
	};
	
	public MyMap()
	{
		levle = 1;
	}
	
	public MyMap(int level)
	{
		this.levle = level;
	}	
	
	public int getLevle()
	{
		return levle;
	}

	public void setLevle(int levle)
	{
		this.levle = levle;
	}

	public static int[][] getMap(int level)
	{
		if(level > ContstUtil.GAME_LEVEL || level <0)	//最多十级
		{
			Log.i("wcj","getMap null");
			return null;
		}
		
		int[][] brick = new int[ContstUtil.BRICK_X][ContstUtil.BRICK_Y];
		
		for (int x = 0; x < ContstUtil.BRICK_X; x++)
		{
			for (int y = 0; y < ContstUtil.BRICK_Y; y++)
			{
				brick[y][x] = bricks[level-1][x][y];
			}
		}
		Log.i("wcj","getMap get bricks success");
		return brick;
	}
}
