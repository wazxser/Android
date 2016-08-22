package com.zte.wcj;

import java.util.ArrayList;

import android.util.Log;

//A*算法
public class AstartRule extends DistanceRule
{
	//f = G (distance) + H
	int H;	//
	
	public AstartRule()
	{
		super();
		H = 0;
	}
	public AstartRule(int x, int y)
	{
		super(x, y);
		H = 0;
	}
	public AstartRule(int x, int y, Dir dir)
	{
		super(x, y, dir);
		H = 0;
	}
	
	public AstartRule(int x, int y, Dir dir, int G, int H)
	{
		super(x, y, dir);
		this.H = H;
		this.distance = G;
	}
	
	public AstartRule(int x, int y, int G, int H)
	{
		super(x, y);
		this.H = H;
		this.distance = G;
	}
	
	public int getH()
	{
		return H;
	}
	public void setH(int h)
	{
		H = h;
	}
	
	public int getG()
	{
		return distance;
	}
	public void setG(int g)
	{
		distance = g;
	}

	public int getF()	//得到总的长度
	{
		return (distance+H);
	}
	
	//某点是不是在astart列表中
	public boolean PointIsInArraylist(ArrayList<AstartRule> astars)
	{
		for(AstartRule astar: astars)
		{
			if(this.equals(astar))
			{
				//Log.i("wcj","astartRule PointIsInArraylist this("+x+","+y+").equals(astar("+astar.getX()+","+astar.getY()+")) return true");
				return true;
			}
		}
		//Log.i("wcj","astartRule PointIsInArraylist return false");
		return false;
	}
}
