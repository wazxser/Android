package com.zte.wcj;

public class DistanceRule extends Rule
{
	int distance;	//距离路径
	
	public DistanceRule()
	{
		super();
		distance = 0;
	}
	public DistanceRule(int x, int y)
	{
		super(x, y);
		distance = 0;
	}
	public DistanceRule(int x, int y, Dir dir)
	{
		super(x, y, dir);
		distance = 0;
	}
	public DistanceRule(int x, int y, Dir dir, int distance)
	{
		super(x, y, dir);
		this.distance = distance;
	}
	
	public DistanceRule(int x, int y, int distance)
	{
		super(x, y);
		this.distance = distance;
	}
	
	public int getDistance()
	{
		return distance;
	}
	public void setDistance(int distance)
	{
		this.distance = distance;
	}
}
