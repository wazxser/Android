package com.zte.wcj;

import android.util.Log;

public class Rule
{
	int x;
	int y;
	Dir dir; // 方向为上一个点到达这个点的方向盘

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

	public void setXY(int x, int y)
	{
		this.x = x;
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

	public Rule(int x, int y, Dir dir)
	{
		this.x = x;
		this.y = y;
		this.dir = dir;
	}

	public Rule(int x, int y)
	{
		this.x = x;
		this.y = y;
		this.dir = Dir.UP;
	}

	public Rule()
	{
		this.x = 0;
		this.y = 0;
		this.dir = Dir.UP;
	}

	@Override
	public boolean equals(Object obj)
	{
		Rule role = (Rule) obj;

		if ((Rule) obj == this)
		{
			Log.i("wcj","rule equals obj = this return true");
			return true;
		}

		if (role.getX() == x && role.getY() == y)
		{
			
			Log.i("wcj","equals first("+x+", "+y+") second("+role.getX()+", "+role.getY()+") return true");
			return true;
		}
		return false;
	}
}
