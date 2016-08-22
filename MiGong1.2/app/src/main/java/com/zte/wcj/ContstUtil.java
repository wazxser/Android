package com.zte.wcj;

public class ContstUtil
{
	public static final int MSG_STARTGAMEVIEW = 1;	//进入到游戏界面
	public static final int MSG_WELCOMGAMEEVIEW = 2;
	public static final int MSG_ABOUTGAMEVIEW = 3;
	public static final int MSG_QUITGAMEVIEW = 4;
	public static final int MSG_SETTINGGAMEVIEW = 9;

	public static final int MSG_RULESGAMEVIEW = 5;
	public static final int MSG_UPDATE = 6;	//更新消息
	public static final int MSG_WINTHISGAME = 7;	//羸了这一局
	public static final int MSG_LOSTHISGAME = 8;	//输了这一局
	
	public final static int BRICK_X = 12; // 横坐标的格子数
	public final static int BRICK_Y = 12; // 纵坐标的格子数
	
	public final static int TIME_SLEEP = 100;	//每隔一段时间执行一次
	public static final int TIME_STAY_TISHI = 8;	//提示线显示的时间n*100ms
	
	public static final int STARTGAME_XPOS = 0;	//地图的起始坐标
	public static final int STARTGAME_YPOS = 0;
	
	public static final int ZIDAN_SPEED = 10;	//子弹的速度px
	public static final int ZIDAN_TIME = 1;	//子弹时间间隔执行一次n*100ms
	
	public static final int BAOZA_TIME = 4;	//爆炸效果的时间间隔
	
	public static final int JINYU_SPEED = 10;	//金鱼的速度n*100ms
	
	public static final int JINYU_COUNT = 4;	//初始时几条金鱼
	
	public static final int WUGUI_LIVE = 5;	//初始时，乌龟的生命数
	
	public static final int ZIDAN_WIDTH = 10;	//子弹的宽度与高度px
	
	public static final int GAME_LEVEL = 10;	//游戏共有几级
	
	public static final String SHAREDATA = "com.wcj.migong";	//保存最新分的数据
	public static final String HIGHSCORE = "highscore";	//保存最高分
}
