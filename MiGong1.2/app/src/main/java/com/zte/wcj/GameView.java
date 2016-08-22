package com.zte.wcj;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
	private SoundPool sp;//声明一个SoundPool
	private int music;//定义一个整型用load（）；来设置suondID

	SurfaceHolder mSurfaceHolder = null;
	Paint paint = null;
	
	int screenW = 0;
	int screenH = 0;

	private static final String TAG = "GameView ";
	private static final String MYTAG = "wcj";
	
	MiGong migong = null;
	boolean bGameOver = false;	//游戏是否结束
	
	int[][] mymap = null;	//地图信息
	KindType[][] xinxi = null;	//整张地图的信息情况
	Bitmap[] xinxiBitmap = null;	//信息图
	Bitmap[] wuguiBitmap = null;	//乌龟图
	Bitmap[] jinyuBitmap = null;	//金鱼图
	
	Bitmap wall = null;	//墙
	Bitmap empty = null; //空图
	Bitmap startmap = null;	//起点与终点图
	Bitmap endmap = null;
	
	Wugui wugui = null;	//自己的乌龟
	
	ArrayList<Jinyu> jinyus = null;	//金鱼图
	
	ArrayList<Zidan> goodzidans = null;	//我方子弹
	ArrayList<Zidan> badzidans = null;	//对方子弹
	
	ArrayList<Baoza> baozas = null;	//爆炸效果
	
	int level = 1;	//游戏级别
	int nKillJinyu = 0;	//杀金鱼数
	
	public boolean bKillWugui = false;	//乌龟是不是被杀了,暂时没有用到这个变量
	
	public final Point start = new Point(0, 1);	//开始坐标(0,1)
	public final Point end = new Point(ContstUtil.BRICK_X-1, ContstUtil.BRICK_Y-1-1);	//终点坐标(11,10)
	
	public GameView(Context context, AttributeSet att)
	{
		super(context, att);
		
		Log.i(MYTAG, TAG+"GameView2 context = "+context);
		
		mSurfaceHolder = this.getHolder();	//得到holder
		mSurfaceHolder.addCallback(this);	//添加回调函数
		setKeepScreenOn(true);	//设置背景常亮
		
		migong = (MiGong)context;
		
		setFocusable(true);
		paint = new Paint();	
		paint.setAntiAlias(true);	//设置画笔无锯齿(如果不设置,可以看到效果好差)
		paint.setColor(Color.BLACK);
		
		loadBitmap();
		initGame();
	}
	
	//初始化游戏
	private void initGame()
	{
		// TODO Auto-generated method stub
		level = 1;//游戏级别
		mymap = MyMap.getMap(level);	//得到地图数据
		
		mymap[start.x][start.y] = 0;	//保证start 与 end两个点为空格
		mymap[end.x][end.y] = 0;
		
		xinxi = new KindType[ContstUtil.BRICK_X][ContstUtil.BRICK_Y];	//初始化信息表
		
		wugui = new Wugui(this, start.x, start.y);	//初始位置在0,1上
		
		goodzidans = new ArrayList<Zidan>();
		badzidans = new ArrayList<Zidan>();
		
		baozas = new ArrayList<Baoza>();
		jinyus = new ArrayList<Jinyu>();
		
		//对信息赋初值
		for (int x = 0; x < ContstUtil.BRICK_X; x++)
		{
			for (int y = 0; y < ContstUtil.BRICK_Y; y++)
			{
				if(mymap[x][y] == 0)	//空格
				{
					xinxi[x][y] = KindType.EMPTY;
				}else	//墙面
				{
					xinxi[x][y] = KindType.WALL;
				}
			}
		}
		xinxi[start.x][start.y] = KindType.WUGUI;	//起始时，第一个点为乌龟
		
		//初始时生成四条金鱼
		for(int n = 0; n < ContstUtil.JINYU_COUNT; n++)
		{
			createOneJinyu();
		}

	}
	//添加金鱼
	private void createOneJinyu()
	{
		
		int x = 1;
		int y = 1;
		
		int ran = 0;
		
		Log.i(MYTAG, TAG+"createjinyu ");
		
		//第一种方法生成金鱼
		
		while(true)
		{
			Random rdm = new Random(System.currentTimeMillis());
            ran = Math.abs(rdm.nextInt())%((ContstUtil.BRICK_X-1)*(ContstUtil.BRICK_Y-1));

			//先生成一个随机数

			x = ran/ContstUtil.BRICK_Y+1;
			y = ran/ContstUtil.BRICK_X+1;
			Log.i(MYTAG, TAG+"createjinyu ran = "+ran +", x = "+x+", y = "+y);
			if(x>=1 && x < ContstUtil.BRICK_X-1		//保证是合法的坐标
			&& y>=1 && y < ContstUtil.BRICK_Y-1)
			{
				if(xinxi[x][y] == KindType.EMPTY)	//是空格才进行生成金鱼
				{
					Jinyu onejinyu = new Jinyu(this, x, y, (int)Math.random()*4);
					new Thread(new JinyuRun(this, onejinyu)).start();
					jinyus.add(onejinyu);
					Log.i(MYTAG, TAG+"createjinyu add a jinyu point("+x+","+y+")");
					xinxi[x][y] = KindType.JINYU;
					
					break;
				}
			}
			continue;	//继续产生金鱼
		}
		
		Log.i(MYTAG, TAG+"createjinyu success");

	}

	//加载图片
	private void loadBitmap()
	{
		// TODO Auto-generated method stub
		wall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		empty = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
		startmap = BitmapFactory.decodeResource(getResources(), R.drawable.start);
		endmap = BitmapFactory.decodeResource(getResources(), R.drawable.end);
		
		//EMPTY, WALL, WUGUI, JINYU, /* START, END, ZIDAN */
		xinxiBitmap = new Bitmap[KindType.values().length];
		xinxiBitmap[0] = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
		xinxiBitmap[1] = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		//xinxibitmap[2] [3] 没有用到
		
		//UP,RIGHT,DOWN,LEFT
		wuguiBitmap = new Bitmap[Dir.values().length];
		wuguiBitmap[0] = BitmapFactory.decodeResource(getResources(), R.drawable.wuguiup);
		wuguiBitmap[1] = BitmapFactory.decodeResource(getResources(), R.drawable.wuguiright);
		wuguiBitmap[2] = BitmapFactory.decodeResource(getResources(), R.drawable.wuguidown);
		wuguiBitmap[3] = BitmapFactory.decodeResource(getResources(), R.drawable.wuguileft);
		
		//UP,RIGHT,DOWN,LEFT
		jinyuBitmap = new Bitmap[Dir.values().length];
		jinyuBitmap[0] = BitmapFactory.decodeResource(getResources(), R.drawable.jinyuup);
		jinyuBitmap[1] = BitmapFactory.decodeResource(getResources(), R.drawable.jinyuright);
		jinyuBitmap[2] = BitmapFactory.decodeResource(getResources(), R.drawable.jinyudown);
		jinyuBitmap[3] = BitmapFactory.decodeResource(getResources(), R.drawable.jinyuleft);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub	
		Log.i(MYTAG, TAG+"surfaceChanged");
		screenW = getWidth();
		screenH = getHeight();
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0)
	{
		// TODO Auto-generated method stub
		Log.i(MYTAG, TAG+"surfaceCreated");
		new Thread(this).start();		

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		// TODO Auto-generated method stub
		Log.i(MYTAG, TAG+"surfaceDestroyed");
		bGameOver = true;
	}

	@Override
	public void run()
	{
		while(!bGameOver)
		{
			synchronized(this)	//锁定后判断
			{
				kill();//判断是不是爆炸了
			}
			MyDraw();
			migong.mHandler.sendEmptyMessage(ContstUtil.MSG_UPDATE);	//发送更新的消息
			try
			{
				Thread.sleep(ContstUtil.TIME_SLEEP);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void kill()
	{
		for(int i = 0; i < goodzidans.size(); i++)
		{
			Zidan goodzidan = goodzidans.get(i);
			for(int j = 0; j < jinyus.size(); j++)
			{
				Jinyu jinyu = jinyus.get(j);
				if(goodzidan.killJinyu(jinyu) == true)	//我方子弹打金鱼
				{
					break;
				}
			}
		}
		
		for(int i = 0; i < goodzidans.size(); i++)
		{
			Zidan goodzidan = goodzidans.get(i);
			goodzidan.killWugui(wugui); //我方子弹打乌龟
		}
		
		for(int i = 0; i < goodzidans.size(); i++)	//我方子弹打对方子弹
		{
			for( int j = 0; j < badzidans.size(); j++)
			{
				goodzidans.get(i).killZidan(badzidans.get(j));
			}
		}
		
		for(int i = 0; i < badzidans.size(); i++)	//对方子弹打我方子弹
		{
			for( int j = 0; j < goodzidans.size(); j++)
			{
				badzidans.get(i).killZidan(goodzidans.get(j));
			}
		}
		
		for(int i = 0; i < badzidans.size(); i++)
		{
			badzidans.get(i).killWugui(wugui); //敌方子弹打乌龟
		}
		
		for(int i = 0; i < badzidans.size(); i++)
		{
			for(int j = 0; j<jinyus.size(); j++)
			{
				if(badzidans.get(i).killJinyu(jinyus.get(j)) == true) //敌方子弹打金鱼
				{
					break;
				}
			}
		}	
	}

	public void MyDraw()
	{
		
		Canvas canvas = mSurfaceHolder.lockCanvas();	//得到一个canvas实例
		
		if( canvas == null)
		{
			Log.i(MYTAG, TAG+"draw canvas == null");
			return;
		}
		canvas.drawRect(0, 0, screenW, screenH, paint);	//清屏操作
		
		canvas.save();
		
		//第一种方法
		drawGameview(canvas);

		drawZidan(canvas);//画双方子弹
		
		drawBaoza(canvas);	//画爆炸效果
		
		
		if(migong.bNeedDrawTishi)
		{
			drawtishi(canvas);
		}

		
		canvas.restore();
		mSurfaceHolder.unlockCanvasAndPost(canvas);
	}

	private void drawGameview(Canvas canvas)
	{
		int ntype = 0;
		//EMPTY, WALL, WUGUI, JINYU, /* START, END, ZIDAN */
		for (int x = 0; x < ContstUtil.BRICK_X; x++)
		{
			for (int y = 0; y < ContstUtil.BRICK_Y; y++)
			{
				 ntype = xinxi[x][y].ordinal();
				 
				canvas.drawBitmap(xinxiBitmap[0], 	//空格
						ContstUtil.STARTGAME_XPOS + x*empty.getWidth(), 
						ContstUtil.STARTGAME_YPOS + y*empty.getHeight(), 
						paint);
				 
				 if(ntype == 1) //墙
				 {
					 canvas.drawBitmap(xinxiBitmap[1], 	//空格
								ContstUtil.STARTGAME_XPOS + x*empty.getWidth(), 
								ContstUtil.STARTGAME_YPOS + y*empty.getHeight(), 
								paint);
				}
			}
		}
		//单独画起点与终点

		canvas.drawBitmap(endmap, 
				ContstUtil.STARTGAME_XPOS + end.x*endmap.getWidth(), 
				ContstUtil.STARTGAME_YPOS + end.y*endmap.getHeight(), 
				paint);
		
		for (int x = 0; x < ContstUtil.BRICK_X; x++)
		{
			for (int y = 0; y < ContstUtil.BRICK_Y; y++)
			{
				 ntype = xinxi[x][y].ordinal();
				 
				if(ntype == 2)	//乌龟
				 {
					 Dir dir = wugui.getDir();
					 
					 canvas.drawBitmap(wuguiBitmap[dir.ordinal()], 
								ContstUtil.STARTGAME_XPOS + x*empty.getWidth(), 
								ContstUtil.STARTGAME_YPOS + y*empty.getHeight(), 
								paint);
					 
				 }else if(ntype == 3)	//金鱼
				 {
					 Dir dir = Dir.RIGHT;
					 boolean bexist = false;
					 
					 for(Jinyu jinyu : jinyus)
					{
						if(jinyu.getX() == x || jinyu.getY() == y)
						{
							dir = jinyu.getDir();
							bexist = true;
							break;
						}
					}
					 if(bexist)	//金鱼存在
					 {
						 canvas.drawBitmap(jinyuBitmap[dir.ordinal()], 
								ContstUtil.STARTGAME_XPOS + x*empty.getWidth(), 
								ContstUtil.STARTGAME_YPOS + y*empty.getHeight(), 
								paint);
					 }else	//估计是一个错误的坐标　
					 {
						 Log.e("wcj","gameview drawGameview error jinyu point("+x+","+y+")");
					 }
				 }
			}
		}
	}

	private void drawJinyu(Canvas canvas)
	{
		for(Jinyu jinyu : jinyus)
		{
			jinyu.draw(canvas);
		}	
	}

	private void drawBaoza(Canvas canvas)
	{
		for(Baoza baoza : baozas)
		{
			baoza.draw(canvas);
		}		
	}

	private void drawZidan(Canvas canvas)
	{
		for(Zidan zidan : goodzidans)
		{
			zidan.draw(canvas);
		}
		for(Zidan zidan : badzidans)
		{
			zidan.draw(canvas);
		}
	}

	private void drawtishi(Canvas canvas)
	{
		paint.setColor(Color.RED);	//画红线
		//npoint = 2, 3, 4
		Log.i("wcj", "gameview drawtishi");
		for(int n = 1; n < migong.result.size(); n++)
		{
			Rule next = migong.result.get(n);
			Rule pre = migong.result.get(n-1);
			
			Log.i("wcj", "gameview draw line point["+pre.getX()+"]["+pre.getY()+"].dir = "+pre.getDir()+" -- > point["+next.getX()+"]["+next.getY()+"].dir = "+next.getDir());
			
			canvas.drawLine(ContstUtil.STARTGAME_XPOS+pre.getX()*empty.getWidth()+empty.getWidth()/2, 
					ContstUtil.STARTGAME_YPOS+pre.getY()*empty.getHeight()+empty.getHeight()/2, 
					ContstUtil.STARTGAME_XPOS+next.getX()*empty.getWidth()+empty.getWidth()/2,
					ContstUtil.STARTGAME_YPOS+next.getY()*empty.getHeight()+empty.getHeight()/2, paint);
		}
		paint.setColor(Color.BLACK);	//重新画黑线
	}

	private void drawMyMap(Canvas canvas)
	{
		// TODO Auto-generated method stub
		
		for (int x = 0; x < ContstUtil.BRICK_X; x++)
		{
			for (int y = 0; y < ContstUtil.BRICK_Y; y++)
			{
				if(mymap[x][y] == 0)	//空格
				{				
					canvas.drawBitmap(empty, 
					ContstUtil.STARTGAME_XPOS + x*empty.getWidth(), 
					ContstUtil.STARTGAME_YPOS + y*empty.getHeight(), 
					paint);
				}
				else	//墙
				{
					canvas.drawBitmap(wall, 
							ContstUtil.STARTGAME_XPOS + x*wall.getWidth(), 
							ContstUtil.STARTGAME_YPOS + y*wall.getHeight(), 
							paint);
				}
			}
		}
		//单独画起点与终点
		canvas.drawBitmap(startmap, 
				ContstUtil.STARTGAME_XPOS + start.x*startmap.getWidth(), 
				ContstUtil.STARTGAME_YPOS + start.y*startmap.getHeight(), 
				paint);
		canvas.drawBitmap(endmap, 
				ContstUtil.STARTGAME_XPOS + end.x*endmap.getWidth(), 
				ContstUtil.STARTGAME_YPOS + end.y*endmap.getHeight(), 
				paint);
	}
	
	//判断某个点是不是能到达
	public boolean isCanReach(int x, int y)
	{
		//第一种方法
		
		if(xinxi[x][y] == KindType.EMPTY)	//信息表中的空格，则可以移动
		{
			return true;
		}
		return false;

	}

	public void createOneWugui()
	{		
		if(wugui == null)
		{
			Log.i("wcj", "gameview createOneWugui create a new  wugui");
			wugui = new Wugui(this, start.x, start.y);
		}
		wugui.setX(start.x);
		wugui.setY(start.y);
		
		xinxi[start.x][start.y] = KindType.WUGUI;
		Log.i("wcj", "gameview createOneWugui wugui point("+start.x+","+start.y+")");
	}
	
	//重新得到当前的信息	
	public void regetxinxi()
	{
		for(int x = 0; x < ContstUtil.BRICK_X; x++)
		{
			for(int y = 0; y <ContstUtil.BRICK_Y; y++)
			{
				xinxi[x][y] = KindType.EMPTY;
				if(mymap[x][y] == 1)
				{
					xinxi[x][y] = KindType.WALL;
				}
			}
		}
		
		xinxi[wugui.getX()][wugui.getY()] = KindType.WUGUI;
		
		for(Jinyu jinyu: jinyus)
		{
			xinxi[jinyu.getX()][jinyu.getY()] = KindType.JINYU;
		}
		Log.i("wcj", "gameview regetxinxi reget xinxi success");
	}

	public void resumeGame()
	{
		if(level>=10) level = 10;
		
		Log.i("wcj", "gameview resumeGame level = "+level);
		
		//清空链表信息
		jinyus.clear();
		goodzidans.clear();
		badzidans.clear();
		baozas.clear();
		
		mymap = MyMap.getMap(level);	//重新得到地图

		createOneWugui();	//得到新的乌龟
		
		Random rdm = new Random(System.currentTimeMillis());
        int ran = Math.abs(rdm.nextInt())%3;
        //得到３+level个金鱼
        
        for(int i = 0; i < level+ran; i++)
        {
        	createOneJinyu();
        }
        regetxinxi(); //更新信息
	}
	
	public void clearGameviewData()
	{
		Log.i("wcj", "gameview clearGameviewData start");
		
		//消除自己
		if(wugui != null)
		{
			wugui = null;
		}
		//消除我方子弹
		for(Zidan goodzidan : goodzidans)
		{
			goodzidan.setLive(false);	//停止线程
		}
		goodzidans.clear();
		
		//消除敌方金鱼
		for(Jinyu jinyu : jinyus)
		{
			jinyu.setBlive(false);	//停止线程
		}
		jinyus.clear();
		
		//消除敌方子弹
		for(Zidan badzidan : badzidans)
		{
			badzidan.setLive(false);	//停止线程
		}
		badzidans.clear();
		
		bGameOver = true;	//游戏结束
		//游戏结束
		Log.i("wcj", "gameview clearGameviewData success");
	}

	public void lostGameDo()
	{
		bGameOver = true;	//游戏结束
		
		migong.alertDialog.setTitle("游戏结束");
		migong.alertDialog.setMessage("游戏将要结束，点击“确认”返回到主界面，点击“重玩”则重新开始游戏");
		
		migong.alertDialog.setNegativeButton("重玩",new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				clearGameviewData();	//清除用户数据
				
				level = 1;	//强制设置为第一关
				bGameOver = false;	//游戏没有结束
				resumeGame();
				new Thread(GameView.this).start();	
			}});
		
		migong.alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				migong.mHandler.sendEmptyMessage(ContstUtil.MSG_WELCOMGAMEEVIEW);
			}
			
		});
		migong.alertDialog.show();
	}
}
