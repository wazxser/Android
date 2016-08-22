package com.zte.wcj;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MiGong extends Activity{
    private static final String TAG = "migong ";
	private final static String MYTAG = "wcj";

	private DBHelper dbHelper = DBHelper.getInstance(this);

	GameView gameview = null;	//游戏界面引用
	TextView rulesview = null;	//游戏规则界面
	TextView aboutview = null;	//关于游戏界面
	
	ImageButton startgame = null;	//菜单按钮
	ImageButton aboutgame = null;
	ImageButton rulesgame = null;
	ImageButton quitgame = null;

	ImageButton setting = null;

	ImageButton upbutton = null;
	ImageButton rightbutton = null;
	ImageButton downbutton = null;
	ImageButton leftbutton = null;
	ImageButton attactbutton = null;	//攻击按钮
	ImageButton fanhuibutton = null;	//返回按钮
	
	TextView info = null;	//显示信息的界面

	//ImageButton tishibutton = null;	//提示按钮
	ImageButton zuiduanbutton = null;	//最短路径
	boolean bNeedDrawTishi = false;	//是不是需要画提示路线
	
	Builder alertDialog = null;	//游戏结束对话框
	
	ArrayList<Rule> lines = null;	//保存搜索的路线
	ArrayList<Rule> result = null;	//保存提示的路线
	ArrayList<DistanceRule> leastlines = null;	//保存最短路径
	
	ArrayList<AstartRule> astartlines = null;	//保存astart算法产生的路径值
	ArrayList<AstartRule> astarOpen = null;	//所有已生成而未扩展的结点
	ArrayList<AstartRule> astarClosed = null;	//记录已扩展过的结点
	ArrayList<AstartRule> astarSon = null;	//节点扩展的子节点，暂时没有用到
	
	boolean[][] searchs = null;	//是不是访问了
	boolean bisInSearcd = false;	//是不是在搜索过程中，搜索过程中，不允许敌方移动
	
	Rule end = new Rule(ContstUtil.BRICK_X-1, ContstUtil.BRICK_Y-2);	//终点坐标（11,10）

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Intent intent = new Intent(MiGong.this, yypService.class);
		startService(intent);
		Log.i(MYTAG, TAG+"onCreate savedInstanceState = "+savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐去标题栏(程序的名字)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐去电池等图标和一切修饰部分(状态栏部分)
		//上面的代码写在setContentView之前
		//setContentView(new GameView(this));
		//setContentView(R.layout.main);
		ToWelcomeView();
    }

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MiGong.this,yypService.class);
		stopService(intent);
		super.onStop();
	}

	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{	if(msg.what != ContstUtil.MSG_UPDATE)
			{
				Log.i("wcj", "migong Handler msg.what = "+msg.what);
			}
			switch(msg.what)
			{
			case ContstUtil.MSG_STARTGAMEVIEW:
				ToGameView();
				break;
				
			case ContstUtil.MSG_WELCOMGAMEEVIEW:
				ToWelcomeView();
				break;
				
			case ContstUtil.MSG_ABOUTGAMEVIEW:
				ToAboutGame();
				break;
				
			case ContstUtil.MSG_QUITGAMEVIEW:
				finish();
				break;

			case ContstUtil.MSG_SETTINGGAMEVIEW:
				setLevel();
				break;
				
			case ContstUtil.MSG_RULESGAMEVIEW:
				ToRulesGame();
				break;
			case ContstUtil.MSG_UPDATE:	//更新信息
				if(gameview!=null)
				{
				info.setText("游戏级别："+gameview.level+"　"
						+"坐标("+gameview.wugui.getX()+","+gameview.wugui.getY()+")\n"
						+"生命值："+gameview.wugui.getNlive()+" "
						+"子弹数："+gameview.goodzidans.size()+"\n"
						+"杀敌数："+gameview.nKillJinyu+"\n"
						+"敌方数："+gameview.jinyus.size()+" "
						+"子弹数："+gameview.badzidans.size()+"\n");
				}
				break;
			case ContstUtil.MSG_WINTHISGAME:	//赢了这一局
				Toast.makeText(MiGong.this, "恭喜你，胜利通过第"+gameview.level+"关！", Toast.LENGTH_LONG).show();
				gameview.level ++; //游戏级别加１
				gameview.wugui.setNlive(gameview.wugui.getNlive()+1);	//乌龟的生命数加１
				gameview.resumeGame();	//重新开始新的一局游戏
				break;
			case ContstUtil.MSG_LOSTHISGAME:	//输了这一局
				//Toast.makeText(MiGong.this, "大虾再接再励！", Toast.LENGTH_LONG).show();
				gameview.lostGameDo();
				break;
			default:
				break;
			}
		}
	};
	
	OnClickListener startlistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			Log.i("wcj", "migong startlistener");
			
			mHandler.sendEmptyMessage(ContstUtil.MSG_STARTGAMEVIEW);//发送消息
		}
		
	};
	OnClickListener ruleslistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			Log.i("wcj", "migong ruleslistener");
			
			mHandler.sendEmptyMessage(ContstUtil.MSG_RULESGAMEVIEW);//发送消息
		}
		
	};
	OnClickListener aboutlistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			Log.i("wcj", "migong aboutlistener");
			
			mHandler.sendEmptyMessage(ContstUtil.MSG_ABOUTGAMEVIEW);//发送消息
		}
		
	};
	OnClickListener quitlistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			Log.i("wcj", "migong quitlistener");
			
			mHandler.sendEmptyMessage(ContstUtil.MSG_QUITGAMEVIEW);//发送消息
		}		
	};

	OnClickListener settinglistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			Log.i("wcj", "migong settinglistener");

			mHandler.sendEmptyMessage(ContstUtil.MSG_SETTINGGAMEVIEW);//发送消息
		}
	};
		
	OnClickListener uplistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			
			int x = gameview.wugui.getX();
			int y = gameview.wugui.getY();
			Log.i("wcj", "migong uplistener x = "+x+", y = "+y);
			y--;
			printMyMap();
			printWuguiJinyu();
			if(gameview.wugui.getDir() != Dir.UP)	//方向不相同，则返回
			{
				gameview.wugui.setDir(Dir.UP);
				return;
			}
			//Log.i("wcj", "migong uplistener gameview.xinxi["+x+"]["+y+"] = "+gameview.xinxi[x][y]);
			if(y>=0 && gameview.xinxi[x][y] == KindType.EMPTY)	//是空格，则可以到达
			{
				Log.i("wcj", "migong uplistener2 x = "+x+", y = "+y);
				gameview.wugui.setX(x);
				gameview.wugui.setY(y);
				
				//对信息表中进行修正
				gameview.xinxi[x][y] = KindType.WUGUI;	//当前的为乌龟
				if(y+1<ContstUtil.BRICK_Y) gameview.xinxi[x][y+1] = KindType.EMPTY;	//之前的为空格
			}				
		}
		
	};

	OnClickListener rightlistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			int x = gameview.wugui.getX();
			int y = gameview.wugui.getY();
			Log.i("wcj", "migong rightlistener x = "+x+", y = "+y);
			x++;
			printMyMap();
			printWuguiJinyu();
			if(gameview.wugui.getDir() != Dir.RIGHT)	//方向不相同，则返回
			{
				gameview.wugui.setDir(Dir.RIGHT);
				return;
			}
			//Log.i("wcj", "migong rightlistener gameview.xinxi["+x+"]["+y+"] = "+gameview.xinxi[x][y]);
			if(x<ContstUtil.BRICK_X && gameview.xinxi[x][y] == KindType.EMPTY)
			{
				Log.i("wcj", "migong rightlistener2 x = "+x+", y = "+y);
				gameview.wugui.setX(x);
				gameview.wugui.setY(y);
				
				//对信息表中进行修正
				gameview.xinxi[x][y] = KindType.WUGUI;	//当前的为乌龟
				if(x-1>=0) gameview.xinxi[x-1][y] = KindType.EMPTY;	//之前的为空格
				
				if(x == gameview.end.x && y == gameview.end.y //是最后一个点
				&& gameview.jinyus.size() <= 0 ) //没有了金鱼
				{
					mHandler.sendEmptyMessage(ContstUtil.MSG_WINTHISGAME);	//发送赢了这局的消息
					Log.i("wcj", "migong rightlistener win this level");
				}
			}
		}		
	};
	
	OnClickListener downlistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			int x = gameview.wugui.getX();
			int y = gameview.wugui.getY();
			Log.i("wcj", "migong downlistener x = "+x+", y = "+y);
			y++;
			printMyMap();
			printWuguiJinyu();
			if(gameview.wugui.getDir() != Dir.DOWN)	//方向不相同，则返回
			{
				gameview.wugui.setDir(Dir.DOWN);
				return;
			}
			//Log.i("wcj", "migong downlistener gameview.xinxi["+x+"]["+y+"] = "+gameview.xinxi[x][y]);
			if(y<ContstUtil.BRICK_Y && gameview.xinxi[x][y] == KindType.EMPTY)
			{
				Log.i("wcj", "migong downlistener2 x = "+x+", y = "+y);
				gameview.wugui.setX(x);
				gameview.wugui.setY(y);
				
				//对信息表中进行修正
				gameview.xinxi[x][y] = KindType.WUGUI;	//当前的为乌龟
				if(y-1>=0) gameview.xinxi[x][y-1] = KindType.EMPTY;	//之前的为空格
			}
		}		
	};
	
	OnClickListener leftlistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			int x = gameview.wugui.getX();
			int y = gameview.wugui.getY();
			Log.i("wcj", "migong leftlistener x = "+x+", y = "+y);
			x--;
			printMyMap();
			printWuguiJinyu();
			if(gameview.wugui.getDir() != Dir.LEFT)	//方向不相同，则返回
			{
				gameview.wugui.setDir(Dir.LEFT);
				return;
			}
			//Log.i("wcj", "migong leftlistener gameview.xinxi["+x+"]["+y+"] = "+gameview.xinxi[x][y]);
			if(x>=0 && gameview.xinxi[x][y] == KindType.EMPTY)
			{
				Log.i("wcj", "migong leftlistener2 x = "+x+", y = "+y);
				gameview.wugui.setX(x);
				gameview.wugui.setY(y);
				
				//对信息表中进行修正
				gameview.xinxi[x][y] = KindType.WUGUI;	//当前的为乌龟
				if(x+1<ContstUtil.BRICK_X) gameview.xinxi[x+1][y] = KindType.EMPTY;	//之前的为空格
			}
		}		
	};
	
	OnClickListener attactlistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			int x = gameview.wugui.getX();
			int y = gameview.wugui.getY();
			Dir dir = gameview.wugui.getDir();
			Log.i("wcj", "migong attactlistener x = "+x+", y = "+y);
			
			gameview.wugui.fire();	//乌龟发射子弹

		}		
	};
	
	OnClickListener zuiduanlistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			int x = gameview.wugui.getX();
			int y = gameview.wugui.getY();
			
			printMyMap();	//打印当前的顶点出来
			printXinxi();	//打印当前信息出来
			Log.i("wcj", "migong zuiduanlistener x = "+x+", y = "+y);
			
			bisInSearcd = true;	//不允许其它的移动
			
			//以下分两种情况来讨论
			if(gameview.jinyus.size()>0)	//如果有金鱼，则先搜索到最近的金鱼
			{
				//第一种方法
				//清空链表
				leastlines.clear();
				initSearchs();	//初始化searchs数组
				printSearchs();	//打印searchs数组
				DistanceRule start = new DistanceRule(x, y, 0);	//起点信息
				printWuguiJinyu();
				jinyuSearchs();//对serarchs进行修改
				initJinyuArrayList(start);	//初始化搜索金鱼路径
				searchLeastJinyu(start);
				
				printLines();
				showLeastJinyu();	//显示金鱼的路径
				
			}else	//没有金鱼，则搜索终点
			{
				//第二种方法
				//dijskra A*算法
				
				astartlines.clear();	//先清空列表
				astarOpen.clear();	//初始化open列表
				astarClosed.clear();	//初始化close列表
				
				AstartRule start = new AstartRule(x, y, 0, 0);
				
				initClosed();	//初始化closed列表
				initAstartLines(start);	//初始化lines表
				AstartSearch();	//A* 搜索
				
				getResult(start);	//把结果保存在result中
				
				printastarClosed();
				printastarOpen();
				
				printResult();
				
				bNeedDrawTishi = true;	//需要显示提示
				new Thread(new DrawTishiRunnable(MiGong.this)).start();	//启动线程来提示
				
			}
			bisInSearcd = false;//搜索完后，金鱼才可以移动

		}
	};
	
	OnClickListener fanhuilistener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			Log.i("wcj", "migong fanhuilistener towelcome view");
			if(gameview != null)	//游戏界面的返回处理
			{

				dbHelper.deleteDataById("a");
				dbHelper.insertData(gameview.level+"");

				Log.i("wcj", "migong fanhuilistener gameview towelcome view");
				gameview.clearGameviewData();	//清空游戏界面中的数据
				mHandler.sendEmptyMessage(ContstUtil.MSG_WELCOMGAMEEVIEW);
				gameview = null;
			}else if(aboutview != null)
			{
				Log.i("wcj", "migong fanhuilistener aboutview towelcome view");
				mHandler.sendEmptyMessage(ContstUtil.MSG_WELCOMGAMEEVIEW);
				aboutview = null;
			}else if(rulesgame != null)
			{
				Log.i("wcj", "migong fanhuilistener rulesview towelcome view");
				mHandler.sendEmptyMessage(ContstUtil.MSG_WELCOMGAMEEVIEW);
				rulesgame = null;
			}
						
		}	
	};


	protected void showLastEnd()
	{
		//因为是后添加的，所以金鱼应该从尾向前来搜索
		
		result.clear(); //先清空存放结果的链表
		
		//先找到金鱼
		int n = 0;
		
		int x = 0;
		int y = 0;
		Dir dir = Dir.UP;
		
		DistanceRule now =null; //当前的结点
		DistanceRule pre = null; //上一个结点，不保存方向
		
		Log.i("wcj", "migong showLastEnd n = "+ (leastlines.size()-1));
		for(n = leastlines.size()-1; n >=0; n--)
		{
			x = leastlines.get(n).getX();
			y = leastlines.get(n).getY();
			dir = leastlines.get(n).getDir();
			now = leastlines.get(n);
			
			if(x == gameview.end.x && y == gameview.end.y)	//找到了最后的点
			{				
				result.add(now);
				pre = getPreJinyu(now);	//继续得到上一个的坐标
				Log.i("wcj", "migong showLastEnd add now("+now.getX()+","+now.getY()+").dir = "+now.getDir());
				Log.i("wcj", "migong showLastEnd pre("+pre.getX()+","+pre.getY()+")");
				break;
			}
		}
		Log.i("wcj", "migong showLastEnd n2 = "+ n);
		if(result.size() < 0 )
		{
			Log.i("wcj", "migong showLastEnd not have end");
			return ;
		}
		
		//从n往前搜索
		for(int i = n -1; i >=1; i--)
		{
			now = leastlines.get(i);
			
			if(now.equals(pre))	//当前的与上一个相同，示为找到
			{				
				result.add(now);
				pre = getPreJinyu(now);
				
				Log.i("wcj", "migong showLastEnd now("+now.getX()+","+now.getY()+").dir = "+now.getDir());
				Log.i("wcj", "migong showLastEnd pre("+pre.getX()+","+pre.getY()+")");
			}
		}
		result.add(pre);	//加入起点
		Log.i("wcj", "migong showLastEnd show log ");
		for(int i = 0; i < result.size(); i++)
		{
			Rule rolue = result.get(i);
			//Log.i("wcj", "migong showLastEnd point("+rolue.getX()+","+rolue.getY()+").dir = "+rolue.getDir());
		}
		
		bNeedDrawTishi = true;	//需要显示提示
		new Thread(new DrawTishiRunnable(MiGong.this)).start();	//启动线程来提示
		
	}
	
	protected void printResult()
	{
		for(Rule r: result)
		{
			Log.i("wcj","migong printResult point("+r.getX()+","+r.getY()+").dir = "+r.getDir());
		}
	}

	//根据start 与 end得到　result，结果保存　在astarClosed中
	protected void getResult(AstartRule start)
	{
		Rule myend = new Rule(gameview.end.x, gameview.end.y);
		
		result.clear();	//先清空
		
		int n = astarClosed.size()-1;
		
		AstartRule next = astarClosed.get(n);
		AstartRule pre = null;
		
		
		//最后一个为终点的坐标　
		result.add(next);
		Log.i("wcj","migong getResult result add point("+next.getX()+","+next.getY()+").dir = "
				+next.getDir()+" g = "+next.getG()+" h = "+next.getH());
		
		pre = getPreResult(next);
		
		Log.i("wcj","migong getResult pre point("+pre.getX()+","+pre.getY()+").dir = "
				+pre.getDir()+" g = "+pre.getG()+" h = "+pre.getH());
		
		while(!start.equals(pre)&&n>=0)
		{
			Log.i("wcj","migong getResult n = "+n);
			if(pre.equals(astarClosed.get(n)))
			{
				next = astarClosed.get(n);
				result.add(next);
				
				Log.i("wcj","migong getResult result add point("+next.getX()+","+next.getY()+").dir = "
						+next.getDir()+" g = "+next.getG()+" h = "+next.getH());
				
				pre = getPreResult(next);
				Log.i("wcj","migong getResult pre point("+pre.getX()+","+pre.getY()+").dir = "
						+pre.getDir()+" g = "+pre.getG()+" h = "+pre.getH());
			}
			n--;
		}
		//在此pre保存的是初始起点坐标
		result.add(pre);
	}

	private AstartRule getPreResult(AstartRule next)
	{
		AstartRule temp = null;
		int x = next.getX();
		int y = next.getY();
		
		for(AstartRule r : astarClosed)
		{
			if(r.equals(next))
			{
				if (r.getDir() == Dir.UP)
				{
					temp = new AstartRule(x, y + 1);	//得到上一个格子的坐标

				} else if (r.getDir() == Dir.RIGHT)
				{
					temp = new AstartRule(x-1, y);	//得到上一个格子的坐标

				} else if (r.getDir() == Dir.DOWN)
				{
					temp = new AstartRule(x, y - 1);	//得到上一个格子的坐标

				} else	// if(myrule.getDir() == Dir.LEFT)		
				{
					temp = new AstartRule(x+1, y);	//得到上一个格子的坐标

				}
			}
		}
		return temp;
	}

	//Ａ*搜索
	protected void AstartSearch()
	{
		Log.i("wcj", "migong AstartSearch start");
		
		AstartRule r = null;
		Rule myend = new Rule(gameview.end.x, gameview.end.y);
		
		while(astarOpen.size()>0)	//还有扩展结点
		{
			r = leastRule(astarOpen);	//取得是小的Ｆ的点
			if(r.equals(myend))	//是最后一个结点，找到了
			{
				astarClosed.add(r);				
				Log.i("wcj", "migong AstartSearch astarClosed add Point("+r.getX()+","+r.getY()+").dir = "
						+r.getDir()+" g = "+r.getG()+", h = "+r.getH());
				
				Log.i("wcj", "migong AstartSearch search end success ");
				break;
			}
			
			//根据r,得到son结点
			astarSon = getAstarSon(r);
			
			for(AstartRule myrule : astarSon)
			{
				//对close表中进行栓选
				if(myrule.PointIsInArraylist(astarClosed))
				{
					continue;
				}
				
				if(myrule.PointIsInArraylist(astarOpen))	//如果子表在open表中
				{
					selectAstartOpen(myrule);	//有进行先择
					continue;
				}
				astarOpen.add(myrule);	//添加到open表中
				
				Log.i("wcj", "migong AstartSearch astarOpen add Point("+myrule.getX()+","+myrule.getY()+").dir = "
						+myrule.getDir()+" g = "+myrule.getG()+", h = "+myrule.getH());
			}
			astarClosed.add(r);
			
			Log.i("wcj", "migong AstartSearch astarClosed add Point("+r.getX()+","+r.getY()+").dir = "
					+r.getDir()+" g = "+r.getG()+", h = "+r.getH());
			
			astarOpen.remove(r);
			Log.i("wcj", "migong AstartSearch astarOpen remove Point("+r.getX()+","+r.getY()+").dir = "
					+r.getDir()+" g = "+r.getG()+", h = "+r.getH());
			
		}
	}
	

	private void selectAstartClose(AstartRule rule)
	{
		AstartRule r1 =null;
		AstartRule r2 = rule;
		
		for(int i = 0; i < astarClosed.size(); i++)
		{
			r1 = astarClosed.get(i);	//取得第一个格子
			
			if(r1.equals(r2))	//有相同的点
			{
				Log.i("wcj", "migong selectAstartClose r1("+r1.getX()+","+r1.getY()+").dir = "+r1.getDir()
						+" g = "+r1.getG()+" h = "+r1.getH()+" f = "+ r1.getF());
				Log.i("wcj", "migong selectAstartClose r2("+r2.getX()+","+r2.getY()+").dir = "+r2.getDir()
						+" g = "+r2.getG()+" h = "+r2.getH()+" f = "+ r2.getF());
				if(r1.getG()>r2.getG())	//r2的路径更小
				{
					astarClosed.remove(r1);
					astarClosed.add(r2);
					Log.i("wcj", "migong selectAstartClose astarClosed remove r1");
					Log.i("wcj", "migong selectAstartClose astartOpen add r2("+r2.getX()+","+r2.getY()+").dir = "+r2.getDir()
							+" g = "+r2.getG()+" h = "+r2.getH()+" f = "+ r2.getF());
				}
			}
		}
	}

	private void selectAstartOpen(AstartRule rule)
	{
		AstartRule r1 =null;
		AstartRule r2 = rule;	//r2为新添加的点,还未添加到open表中来
		
		for(int i = 0; i < astarOpen.size(); i++)
		{
			r1 = astarOpen.get(i);	//取得第一个格子
		
			if(r1.equals(r2))
			{
				Log.i("wcj", "migong selectAstartOpen r1("+r1.getX()+","+r1.getY()+").dir = "+r1.getDir()
						+" g = "+r1.getG()+" h = "+r1.getH()+" f = "+ r1.getF());
				Log.i("wcj", "migong selectAstartOpen r2("+r2.getX()+","+r2.getY()+").dir = "+r2.getDir()
						+" g = "+r2.getG()+" h = "+r2.getH()+" f = "+ r2.getF());
				if(r1.getG() > r2.getG())	//我们传过来的路径更小，则更新
				{
					astarOpen.remove(r1);	//删除r1
					Log.i("wcj", "migong selectAstartOpen astarOpen remove r1");
					Log.i("wcj", "migong selectAstartOpen astarOpen add r2");
					//添加r2
					astarOpen.add(r2);
					break;
				}
			}
			
		}
	}

	private ArrayList<AstartRule> getAstarSon(AstartRule r)
	{
		astarSon.clear();	//先清空列表
		
		Log.i("wcj", "migong getAstarSon parent Point("+r.getX()+","+r.getY()+").dir = "+r.getDir()+" g = "+r.getG()+", h = "+r.getH());
		
		int x = 0;
		int y = 0;
		
		int g = 0;
		int h = 0;
		
		AstartRule temp = new AstartRule(x, y);

		//开始搜索
		
		//up right down left
		
		//up
		{	//重新取得坐标
			x = r.getX();
			y = r.getY()-1;
			
			temp.setXY(x, y);
		}
		if(y>=0)
		{
			if( gameview.xinxi[x][y] == KindType.EMPTY && !temp.PointIsInArraylist(astarClosed)) //这个格子为空
			{
				g = r.getG()+1;
				h = Math.abs(x - gameview.end.x) +Math.abs(y - gameview.end.y);	//h的值
				Log.i("wcj", "migong getAstarSon astarSon add up Point("+x+","+y+").dir = "+Dir.UP+" g = "+g+", h = "+h);
				astarSon.add(new AstartRule(x, y, Dir.UP, g, h));
			}
		}
		
		//right
		{	//重新取得坐标
			x = r.getX() +1;
			y = r.getY();
			
			temp.setXY(x, y);
		}
		if(x<ContstUtil.BRICK_X)
		{
			if( gameview.xinxi[x][y] == KindType.EMPTY && !temp.PointIsInArraylist(astarClosed)) //这个格子为空
			{
				g = r.getG()+1;
				h = Math.abs(x - gameview.end.x) +Math.abs(y - gameview.end.y);	//h的值
				Log.i("wcj", "migong getAstarSon astarSon add right Point("+x+","+y+").dir = "+Dir.RIGHT+" g = "+g+", h = "+h);
				astarSon.add(new AstartRule(x, y, Dir.RIGHT, g, h));
			}
		}
		
		//down
		{	//重新取得坐标
			x = r.getX();
			y = r.getY() + 1;
			
			temp.setXY(x, y);
		}
		if(y<ContstUtil.BRICK_Y)
		{
			if( gameview.xinxi[x][y] == KindType.EMPTY && !temp.PointIsInArraylist(astarClosed)) //这个格子为空
			{
				g = r.getG()+1;
				h = Math.abs(x - gameview.end.x) +Math.abs(y - gameview.end.y);	//h的值
				Log.i("wcj", "migong getAstarSon astarSon add right Point("+x+","+y+").dir = "+Dir.DOWN+" g = "+g+", h = "+h);
				astarSon.add(new AstartRule(x, y, Dir.DOWN, g, h));
			}
		}
		
		//left
		{	//重新取得坐标
			x = r.getX() -1;
			y = r.getY();
			
			temp.setXY(x, y);
		}
		if(x >= 0)
		{
			if( gameview.xinxi[x][y] == KindType.EMPTY && !temp.PointIsInArraylist(astarClosed)) //这个格子为空
			{
				g = r.getG()+1;
				h = Math.abs(x - gameview.end.x) +Math.abs(y - gameview.end.y);	//h的值
				Log.i("wcj", "migong getAstarSon astarSon add right Point("+x+","+y+").dir = "+Dir.LEFT+" g = "+g+", h = "+h);
				astarSon.add(new AstartRule(x, y, Dir.LEFT, g, h));
			}
		}
		
		//Log.i("wcj","migong getAstarSon success");
		
		return astarSon;
	}

	//列表中最小F点
	public AstartRule leastRule(ArrayList<AstartRule> astars)
	{
		int size = astars.size();
		int i = 0;	//保存最小的值的索引
		
		if(size < 1)
		{
			Log.i("wcj","migong leastRule size() < 1 return null");
			return null;
		}else if(size == 1)
		{
			Log.i("wcj","migong leastRule size() == 1");
			i = 0;
			return astars.get(i);
		}else{	// size >=2
			//Log.i("wcj","migong leastRule size() >= 2");
			i = 0;
			for(int n = 1; n < size; n++)
			{
				if(astars.get(i).getF() > astars.get(n).getF())	//找到最小的size
				{
					i = n;
				}
			}
			Log.i("wcj","migong leastRule get point("+astars.get(i).getX()+","+astars.get(i).getY()+").dir = "
					+astars.get(i).getDir()+" g ="+astars.get(i).getG()+" h = "+astars.get(i).getH());
			return astars.get(i);
		}
	}
	protected void initAstartLines(AstartRule start)
	{
		astarClosed.add(start);	//把起始结点添加到closed表中来
		Log.i("wcj", "migong initAstartLines astarClosed add Point("+start.x+","+start.y+")");
		
		int x = 0;
		int y = 0;
		
		int g = 0;
		int h = 0;

		//开始搜索
		
		//up right down left
		
		//up
		{	//重新取得坐标
			x = start.x;
			y = start.y-1;
		}
		if(y>=0)
		{
			if( gameview.xinxi[x][y] == KindType.EMPTY) //这个格子为空
			{
				g = 1;
				h = Math.abs(x - gameview.end.x) +Math.abs(y - gameview.end.y);	//h的值
				Log.i("wcj", "migong initAstartLines astarOpen add up Point("+x+","+y+").dir = "+Dir.UP+" g = "+g+", h = "+h);
				astarOpen.add(new AstartRule(x, y, Dir.UP, g, h));
			}
		}
		
		//right
		{	//重新取得坐标
			x = start.x +1;
			y = start.y;
		}
		if(x<ContstUtil.BRICK_X)
		{
			if( gameview.xinxi[x][y] == KindType.EMPTY) //这个格子为空
			{
				g = 1;
				h = Math.abs(x - gameview.end.x) +Math.abs(y - gameview.end.y);	//h的值
				Log.i("wcj", "migong initAstartLines astarOpen add right Point("+x+","+y+").dir = "+Dir.RIGHT+" g = "+g+", h = "+h);
				astarOpen.add(new AstartRule(x, y, Dir.RIGHT, g, h));
			}
		}
		
		//down
		{	//重新取得坐标
			x = start.x;
			y = start.y + 1;
		}
		if(y<ContstUtil.BRICK_Y)
		{
			if( gameview.xinxi[x][y] == KindType.EMPTY) //这个格子为空
			{
				g = 1;
				h = Math.abs(x - gameview.end.x) +Math.abs(y - gameview.end.y);	//h的值
				Log.i("wcj", "migong initAstartLines astarOpen add right Point("+x+","+y+").dir = "+Dir.DOWN+" g = "+g+", h = "+h);
				astarOpen.add(new AstartRule(x, y, Dir.DOWN, g, h));
			}
		}
		
		//left
		{	//重新取得坐标
			x = start.x -1;
			y = start.y;
		}
		if(x >= 0)
		{
			if( gameview.xinxi[x][y] == KindType.EMPTY) //这个格子为空
			{
				g = 1;
				h = Math.abs(x - gameview.end.x) +Math.abs(y - gameview.end.y);	//h的值
				Log.i("wcj", "migong initAstartLines astarOpen add right Point("+x+","+y+").dir = "+Dir.LEFT+" g = "+g+", h = "+h);
				astarOpen.add(new AstartRule(x, y, Dir.LEFT, g, h));
			}
		}
		
		Log.i("wcj","migong initAstartLines success");
	}

	protected void initClosed()
	{
		for (int x = 0; x < ContstUtil.BRICK_X; x++)
		{
			for (int y = 0; y < ContstUtil.BRICK_Y; y++)
			{
				if(gameview.xinxi[x][y] != KindType.EMPTY)	//为空的才没有访问过
				{
					astarClosed.add(new AstartRule(x,y,0,0));
				}
			}
		}
		Log.i("wcj","migong initClosed success");
	}

	protected void printSearchs()
	{
		System.out.print("migong printsearchs: ");
		System.out.print("yx");
		for(int x = 0; x<ContstUtil.BRICK_X; x++)
		{
			System.out.print(" "+x);
		}
		System.out.println();
		
		for (int x = 0; x < ContstUtil.BRICK_X; x++)
		{
			System.out.print("  "+x);
			for (int y = 0; y < ContstUtil.BRICK_Y; y++)
			{
				if(searchs[x][y] == true)	//为空的才没有访问过
				{
					System.out.print("   1");
				}else
				{
					System.out.print("   0");
				}
			}
			System.out.println();
		}
		Log.i("wcj","migong printSearchs success");
	}

	protected void printLines()
	{
		for(Rule r: leastlines)
		{
			Log.i("wcj","migong printLines point("+r.getX()+","+r.getY()+").dir = "+r.getDir());
		}
		
	}
	
	protected void printastarOpen()
	{
		for(AstartRule r: astarOpen)
		{
			Log.i("wcj","migong printastarOpen point("+r.getX()+","+r.getY()+").dir = "
					+r.getDir()+" g = "+r.getG()+" h = "+r.getH());
		}
		
	}
	
	protected void printastarClosed()
	{
		for(AstartRule r: astarClosed)
		{
			Log.i("wcj","migong printastarClosed point("+r.getX()+","+r.getY()+").dir = "
					+r.getDir()+" g = "+r.getG()+" h = "+r.getH());
		}
		
	}

	protected void jinyuSearchs()
	{
		int x = 0;
		int y = 0;
		for(Jinyu jinyu: gameview.jinyus)
		{
			x = jinyu.getX();
			y = jinyu.getY();
			searchs[x][y] = false;	//金鱼的位置没有访问过
		}
		
	}

	protected void showLeastJinyu()
	{
		//因为是后添加的，所以金鱼应该从尾向前来搜索
		
		result.clear(); //先清空链表
		
		//先找到金鱼
		int n = 0;
		
		int x = 0;
		int y = 0;
		Dir dir = Dir.UP;
		
		DistanceRule now =null; //当前的结点
		DistanceRule pre = null; //上一个结点，不保存方向
		
		Log.i("wcj", "migong showLeastJinyu n = "+ (leastlines.size()-1));
		for(n = leastlines.size()-1; n >=0; n--)
		{
			x = leastlines.get(n).getX();
			y = leastlines.get(n).getY();
			dir = leastlines.get(n).getDir();
			
			if(gameview.xinxi[x][y] == KindType.JINYU)	//找到了金鱼的位置
			{
				now = leastlines.get(n);
				result.add(now);
				pre = getPreJinyu(now);	//继续得到上一个的坐标
				Log.i("wcj", "migong showLeastJinyu now("+now.getX()+","+now.getY()+").dir = "+now.getDir());
				Log.i("wcj", "migong showLeastJinyu pre("+pre.getX()+","+pre.getY()+")");
				break;
			}
		}
		Log.i("wcj", "migong showLeastJinyu n2 = "+ n);
		if(result.size() < 0 )
		{
			Log.i("wcj", "migong showLeastJinyu not have jinyu");
			return ;
		}
		
		//从n往前搜索
		for(int i = n -1; i >=1; i--)
		{
			now = leastlines.get(i);
			
			if(now.equals(pre))	//当前的与上一个相同，示为找到
			{				
				result.add(now);
				pre = getPreJinyu(now);
				
				Log.i("wcj", "migong showLeastJinyu now("+now.getX()+","+now.getY()+").dir = "+now.getDir());
				Log.i("wcj", "migong showLeastJinyu pre("+pre.getX()+","+pre.getY()+")");
			}
		}
		result.add(pre);	//加入起点
		Log.i("wcj", "migong showLeastJinyu show log ");
		for(int i = result.size()-1; i >= 0; i--)
		{
			Rule rolue = result.get(i);
			Log.i("wcj", "migong showLeastJinyu point("+rolue.getX()+","+rolue.getY()+").dir = "+rolue.getDir());
		}
		
		bNeedDrawTishi = true;	//需要显示提示
		new Thread(new DrawTishiRunnable(MiGong.this)).start();	//启动线程来提示
	}

	private DistanceRule getPreJinyu(DistanceRule now)
	{
		int x = now.getX();
		int y = now.getY();
		//Dir dir = now.getDir();	//得到上一个格子的方向
		DistanceRule temp = null;

		Log.i("wcj", "migong getPreJinyu now("+now.getX()+","+now.getY()+").dir = "+now.getDir());
		
		if (now.getDir() == Dir.UP)
		{
			temp = new DistanceRule(x, y + 1);	//得到上一个格子的坐标
			
		} else if (now.getDir() == Dir.RIGHT)
		{
			temp = new DistanceRule(x-1, y);	//得到上一个格子的坐标
			
		} else if (now.getDir() == Dir.DOWN)
		{
			temp = new DistanceRule(x, y - 1);	//得到上一个格子的坐标
			
		} else	// if(now.getDir() == Dir.LEFT)		
		{
			temp = new DistanceRule(x+1, y);	//得到上一个格子的坐标
		}
		Log.i("wcj", "migong getPreJinyu pre point("+temp.getX()+","+temp.getY()+")");
		return temp;
	}

	protected void printWuguiJinyu()
	{
		Log.i("wcj","migong printWuguiJinyu wugui point("+gameview.wugui.getX()+", "+gameview.wugui.getY()+")");
		for(Jinyu jinyu : gameview.jinyus)
		{
			Log.i("wcj","migong printWuguiJinyu jinyu point("+jinyu.getX()+", "+jinyu.getY()+")");
		}
	}

	protected void initJinyuArrayList(DistanceRule start)
	{
		int x = start.getX(); // 得到起点的信息
		int y = start.getY();
		
		searchs[x][y] = true; //此点置为已访问
		leastlines.add(start); // 把起点坐标加入到链表中来

		Log.i("wcj","migong initJinyuArrayList add start point("+x+","+y+")");

		if (y-1 >=0 && false == searchs[x][y-1]) // 向上没有访问过
		{			
			if(gameview.xinxi[x][y-1] == KindType.EMPTY
			|| gameview.xinxi[x][y-1] == KindType.JINYU)	//上边为空格或是金鱼
			{
				Log.i("wcj","migong initJinyuArrayList add point("+x+","+(y-1)+")");
				leastlines.add(new DistanceRule(x, y-1, Dir.UP, 1)); // 把向上的点加入到堆栈中来
			}
		}
		if (x + 1 < ContstUtil.BRICK_X && false == searchs[x + 1][y]) // 向右没有访问过
		{
			if(gameview.xinxi[x+1][y] == KindType.EMPTY
			|| gameview.xinxi[x+1][y] == KindType.JINYU)	//右边为空格或是金鱼
			{
				Log.i("wcj","migong initJinyuArrayList add point("+(x+1)+","+(y)+")");
				leastlines.add(new DistanceRule(x + 1, y, Dir.RIGHT, 1)); // 把向右的点加入到堆栈中来
			}
		}
		if (y + 1 < ContstUtil.BRICK_Y && false == searchs[x][y+1]) // 向下没有访问过
		{
			if(gameview.xinxi[x][y+1] == KindType.EMPTY
			|| gameview.xinxi[x][y+1] == KindType.JINYU)	//下边为空格或是金鱼
			{
				Log.i("wcj","migong initJinyuArrayList add point("+x+","+(y+1)+")");
				leastlines.add(new DistanceRule(x, y+1, Dir.DOWN, 1)); // 把向下的点加入到堆栈中来
			}
		}
		if (x - 1 >=0 && false == searchs[x - 1][y]) // 向左没有访问过
		{
			if(gameview.xinxi[x-1][y] == KindType.EMPTY
			|| gameview.xinxi[x-1][y] == KindType.JINYU)	//左边为空格或是金鱼			
			{
				Log.i("wcj","migong initJinyuArrayList add point("+(x-1)+","+(y)+")");
				leastlines.add(new DistanceRule(x - 1, y, Dir.LEFT, 1)); // 把向左的点加入到堆栈中来
			}
		}
		Log.i("wcj","migong initJinyuArrayList success");		
	}

	protected void initEndArrayList(DistanceRule start)
	{
		int x = start.getX(); // 得到起点的信息
		int y = start.getY();
		
		searchs[x][y] = true; //此点置为已访问
		leastlines.add(start); // 把起点坐标加入到链表中来

		Log.i("wcj","migong initEndArrayList add start point("+x+","+y+")");

		if (y-1 >=0 && false == searchs[x][y-1]) // 向上没有访问过
		{			
			if(gameview.xinxi[x][y-1] == KindType.EMPTY)	//上边为空格
			{
				Log.i("wcj","migong initEndArrayList add point("+x+","+(y-1)+")");
				leastlines.add(new DistanceRule(x, y-1, Dir.UP, 1)); // 把向上的点加入到堆栈中来
			}
		}
		if (x + 1 < ContstUtil.BRICK_X && false == searchs[x + 1][y]) // 向右没有访问过
		{
			if(gameview.xinxi[x+1][y] == KindType.EMPTY)	//右边为空格
			{
				Log.i("wcj","migong initEndArrayList add point("+(x+1)+","+(y)+")");
				leastlines.add(new DistanceRule(x + 1, y, Dir.RIGHT, 1)); // 把向右的点加入到堆栈中来
			}
		}
		if (y + 1 < ContstUtil.BRICK_Y && false == searchs[x][y+1]) // 向下没有访问过
		{
			if(gameview.xinxi[x][y+1] == KindType.EMPTY)	//下边为空格
			{
				Log.i("wcj","migong initEndArrayList add point("+x+","+(y+1)+")");
				leastlines.add(new DistanceRule(x, y+1, Dir.DOWN, 1)); // 把向下的点加入到堆栈中来
			}
		}
		if (x - 1 >=0 && false == searchs[x - 1][y]) // 向左没有访问过
		{
			if(gameview.xinxi[x-1][y] == KindType.EMPTY)	//左边为空格		
			{
				Log.i("wcj","migong initEndArrayList add point("+(x-1)+","+(y)+")");
				leastlines.add(new DistanceRule(x - 1, y, Dir.LEFT, 1)); // 把向左的点加入到堆栈中来
			}
		}
		Log.i("wcj","migong initEndArrayList success");			
	}

	protected void searchLeastEnd(DistanceRule start)
	{
		Log.i("wcj", "migong searchLeastEnd searchs["+gameview.end.x+"]["+gameview.end.y+"] = "
				+ searchs[gameview.end.x][gameview.end.y]);
		Log.i("wcj", "migong searchLeastEnd gameview.xinxi["+gameview.end.x+"]["+gameview.end.y+"] = "
				+ gameview.xinxi[gameview.end.x][gameview.end.y]);
		
		int n = (ContstUtil.BRICK_X-1)*(ContstUtil.BRICK_Y-1);
		int i = 0;
		Log.i("wcj", "migong searchLeastEnd search start n = "+n+", i = "+i);
		while(searchs[gameview.end.x][gameview.end.y] == false && i<=n)	//终点没有访问
		{
			Log.i("wcj", "migong searchLeastEnd start do search while i = "+i);
			//先访问
			lastEndStartSearch();
			
			//筛选出最短的路径来
			selectSearch();
			i++;
		}
		Log.i("wcj", "migong searchLeastEnd search done");
	}

	private void lastEndStartSearch()
	{
		int x = 0;
		int y = 0;
		Dir dir = Dir.RIGHT;
		int n = leastlines.size();
		
		Log.i("wcj", "migong lastStartSearch leastlines.size() = "+leastlines.size());
		
		for(int i = 0; i< n; i++)
		{
			DistanceRule r = leastlines.get(i);
			x = r.getX();
			y = r.getY();
			dir = r.getDir();
			
			Log.i("wcj", "migong lastStartSearch searchs["+x+"]["+y+"] = "+searchs[x][y]);
			
			if(searchs[x][y] == false)	//有没有访问的，则进行访问
			{
				lastEndDoSearch(r);
				searchs[x][y] = true;	//置为已访问过
			}
		}		
	}

	private void lastEndDoSearch(DistanceRule r)
	{
		int x = r.getX();
		int y = r.getY();
		//Dir  dir = r.getDir();
		int distance = r.getDistance();
		
		Log.i("wcj", "migong lastEndDoSearch point("+x+", "+y+")");
		
		//向上搜索
		if(y-1>=0 && false == searchs[x][y-1])	//向上没有访问
		{
			if(gameview.xinxi[x][y-1] == KindType.EMPTY )	//是空格
			{
				leastlines.add(new DistanceRule(x, y-1, Dir.UP, distance+1));
				Log.i("wcj", "migong lastEndDoSearch add point ("+x+","+(y-1)+").dir = "+Dir.UP);
			}			
		}
		
		//向右搜索
		if(x+1<ContstUtil.BRICK_X && false == searchs[x+1][y])	//向上没有访问
		{
			if(gameview.xinxi[x+1][y] == KindType.EMPTY )	//是空格
			{
				leastlines.add(new DistanceRule(x+1, y, Dir.RIGHT, distance+1));
				Log.i("wcj", "migong lastEndDoSearch add point ("+(x+1)+","+(y)+").dir = "+Dir.RIGHT);
			}			
		}
		
		//向下搜索
		if(y+1<ContstUtil.BRICK_Y && false == searchs[x][y+1])	//向上没有访问
		{
			if(gameview.xinxi[x][y+1] == KindType.EMPTY )	//是空格
			{
				leastlines.add(new DistanceRule(x, y+1, Dir.DOWN, distance+1));
				Log.i("wcj", "migong lastEndDoSearch add point ("+x+","+(y+1)+").dir = "+Dir.DOWN);
			}			
		}
		
		//向左搜索
		if(x-1>=0 && false == searchs[x-1][y])	//向上没有访问
		{
			if(gameview.xinxi[x-1][y] == KindType.EMPTY )	//是空格
			{
				leastlines.add(new DistanceRule(x-1, y, Dir.LEFT, distance+1));
				Log.i("wcj", "migong lastEndDoSearch add point ("+(x-1)+","+(y)+").dir = "+Dir.LEFT);
			}			
		}	
	}

	protected void searchLeastJinyu(DistanceRule start)
	{
		int n = (ContstUtil.BRICK_X-1)*(ContstUtil.BRICK_Y-1);
		int i = 0;
		
		while(leastlinesHasJinyu()== false && i<=n)	//金鱼没有访问到
		{
			//先访问
			lastJinyuStartSearch();
			
			//筛选出最短的路径来
			selectSearch();
			i++;
		}
	}
	
	private void lastJinyuStartSearch()
	{
		int x = 0;
		int y = 0;
		Dir dir = Dir.RIGHT;
		int n = leastlines.size();
		
		Log.i("wcj", "migong lastStartSearch leastlines.size() = "+leastlines.size());
		
		for(int i = 0; i< n; i++)
		{
			DistanceRule r = leastlines.get(i);
			x = r.getX();
			y = r.getY();
			dir = r.getDir();
			
			Log.i("wcj", "migong lastStartSearch searchs["+x+"]["+y+"] = "+searchs[x][y]);
			
			if(searchs[x][y] == false)	//有没有访问的，则进行访问
			{
				lastJinyuDoSearch(r);
				searchs[x][y] = true;	//置为已访问过
			}
		}		
	}

	private void lastJinyuDoSearch(DistanceRule r)
	{
		int x = r.getX();
		int y = r.getY();
		//Dir  dir = r.getDir();
		int distance = r.getDistance();
		
		Log.i("wcj", "migong lastJinyuDoSearch point("+x+", "+y+")");
		
		//向上搜索
		if(y-1>=0 && false == searchs[x][y-1])	//向上没有访问
		{
			if(gameview.xinxi[x][y-1] == KindType.EMPTY 
			|| gameview.xinxi[x][y-1] == KindType.JINYU)	//是空格或是金鱼
			{
				leastlines.add(new DistanceRule(x, y-1, Dir.UP, distance+1));
				Log.i("wcj", "migong lastJinyuDoSearch add point ("+x+","+(y-1)+").dir = "+Dir.UP);
			}			
		}
		
		//向右搜索
		if(x+1<ContstUtil.BRICK_X && false == searchs[x+1][y])	//向上没有访问
		{
			if(gameview.xinxi[x+1][y] == KindType.EMPTY 
			|| gameview.xinxi[x+1][y] == KindType.JINYU)	//是空格或是金鱼
			{
				leastlines.add(new DistanceRule(x+1, y, Dir.RIGHT, distance+1));
				Log.i("wcj", "migong lastJinyuDoSearch add point ("+(x+1)+","+(y)+").dir = "+Dir.RIGHT);
			}			
		}
		
		//向下搜索
		if(y+1<ContstUtil.BRICK_Y && false == searchs[x][y+1])	//向上没有访问
		{
			if(gameview.xinxi[x][y+1] == KindType.EMPTY 
			|| gameview.xinxi[x][y+1] == KindType.JINYU)	//是空格或是金鱼
			{
				leastlines.add(new DistanceRule(x, y+1, Dir.DOWN, distance+1));
				Log.i("wcj", "migong lastJinyuDoSearch add point ("+x+","+(y+1)+").dir = "+Dir.DOWN);
			}			
		}
		
		//向左搜索
		if(x-1>=0 && false == searchs[x-1][y])	//向上没有访问
		{
			if(gameview.xinxi[x-1][y] == KindType.EMPTY 
			|| gameview.xinxi[x-1][y] == KindType.JINYU)	//是空格或是金鱼
			{
				leastlines.add(new DistanceRule(x-1, y, Dir.LEFT, distance+1));
				Log.i("wcj", "migong lastJinyuDoSearch add point ("+(x-1)+","+(y)+").dir = "+Dir.LEFT);
			}			
		}			
	}

	//链表中是不是有金鱼，有返回true，没有返回false
	private boolean leastlinesHasJinyu()
	{
		for(DistanceRule line: leastlines)
		{
			for(Jinyu jinyu: gameview.jinyus)
			{
				Rule r = new Rule(jinyu.getX(), jinyu.getY());
				if(line.equals(r))
				{
					Log.i("wcj", "migong leastlinesHasJinyu jinyu point("+r.getX()+", "+r.getY()+") return true ");
					return true;
				}
			}
		}
		Log.i("wcj", "migong leastlinesHasJinyu return false");
		return false;
	}

	protected void printMyMap()
	{
		System.out.println("gameview.mymap:");
		System.out.print("yx");
		for(int x = 0; x<ContstUtil.BRICK_X; x++)
		{
			if(x>=10)
			{
				System.out.print(" "+x);
			}else
			{
				System.out.print("  "+x);
			}
		}
		System.out.println();
		for(int y = 0 ; y< ContstUtil.BRICK_Y; y ++)
		{
			if(y>=10)
			{
				System.out.print(" "+y+" ");
			}else
			{
				System.out.print("  "+y+" ");
			}
			
			for(int x = 0; x<ContstUtil.BRICK_X; x++)
			{
				System.out.print(gameview.mymap[x][y]+", ");
			}
			System.out.println();
		}
	}
	
	protected void printXinxi()
	{
		System.out.println("gameview.xinxi:(EMPTY(0), WALL(1), WUGUI(2), JINYU(3), BAOZA(4), START(5), END(6), ZIDAN(7) )");
		System.out.print("yx");
		for(int x = 0; x<ContstUtil.BRICK_X; x++)
		{
			if(x>=10)
			{
				System.out.print(" "+x);
			}else
			{
				System.out.print("  "+x);
			}
		}
		System.out.println();
		
		for(int y = 0 ; y< ContstUtil.BRICK_Y; y ++)
		{
			if(y>=10)
			{
				System.out.print(" "+y+" ");
			}else
			{
				System.out.print("  "+y+" ");
			}
			
			for(int x = 0; x<ContstUtil.BRICK_X; x++)
			{
				System.out.print(gameview.xinxi[x][y].ordinal()+", ");
			}
			System.out.println();
		}
	}

	protected void showLastRule(DistanceRule start)
	{
		Log.i("wcj", "migong showLastRule");
		//先找到最后一个元素
		DistanceRule last = null;
		
		for(int i = leastlines.size()-1; i>=0; i--)
		{
			if(leastlines.get(i).equals(end))	//找到了
			{
				last = leastlines.get(i);
			}
		}
		
		if(last == null)	//没有找到，返回
		{
			Log.i("wcj", "migong showLastRule last == null");
			return;
		}
		
		//先清空链表
		result.clear();
		
		result.add(last);	//把最后一个结点加入到链表中来
		
		Rule now = last;	//初始值为end	
		Rule pre = null;

		pre = getPreDistanceRolue(now);
		Log.i("wcj", "migong showLastRule pre point("+pre.getX()+","+pre.getY()+").dir = "+pre.getDir());
		
		//从倒数第二个向前来配对
		for(int n = leastlines.size()-1; n >=0; n--)
		{
			now = leastlines.get(n);	//
			if(now.equals(start))
			{
				result.add(start);
				Log.i("wcj", "migong showLastRule now point and break");
				break;
			}
			if(now.equals(pre))	//当前格子为上一个格子，添加
			{
				result.add(pre);
				pre = getPreDistanceRolue(now);	//当前格子修正
				Log.i("wcj", "migong showLastRule pre point("+pre.getX()+","+pre.getY()+").dir = "+pre.getDir());
			}				
		}
		
		Log.i("wcj", "migong showLastRule find ");
		for(int n = result.size()-1; n >= 0; n--)
		{
			Rule rolue = result.get(n);
			Log.i("wcj", "migong showLastRule point("+rolue.getX()+","+rolue.getY()+").dir = "+rolue.getDir());
		}
		
		bNeedDrawTishi = true;	//需要显示提示
		new Thread(new DrawTishiRunnable(MiGong.this)).start();	//启动线程来提示
	}
	private Rule getPreDistanceRolue(Rule myrolue)
	{
		int x = myrolue.getX();
		int y = myrolue.getY();
		Dir dir = myrolue.getDir();	//得到上一个格子的方向
		Rule temp = null;

		Log.i("wcj", "migong getPreRolue mypoint("+myrolue.getX()+","+myrolue.getY()+").dir = "+myrolue.getDir());
		
		if (myrolue.getDir() == Dir.UP)
		{
			temp = new Rule(x, y + 1);	//得到上一个格子的坐标
			
			for(Rule rolue: leastlines)
			{
				if(rolue.equals(temp))
				{
					Log.i("wcj", "migong getPreRolue point("+rolue.getX()+","+rolue.getY()+").dir = "+rolue.getDir());
					return rolue;
				}
			}
		} else if (myrolue.getDir() == Dir.RIGHT)
		{
			temp = new Rule(x-1, y);	//得到上一个格子的坐标
			
			for(Rule rolue: leastlines)
			{
				if(rolue.equals(temp))
				{
					Log.i("wcj", "migong getPreRolue point("+rolue.getX()+","+rolue.getY()+").dir = "+rolue.getDir());
					return rolue;
				}
			}
		} else if (myrolue.getDir() == Dir.DOWN)
		{
			temp = new Rule(x, y - 1);	//得到上一个格子的坐标
			
			for(Rule rolue: leastlines)
			{
				if(rolue.equals(temp))
				{
					Log.i("wcj", "migong getPreRolue point("+rolue.getX()+","+rolue.getY()+").dir = "+rolue.getDir());
					return rolue;
				}
			}
		} else	// if(myrolue.getDir() == Dir.LEFT)		
		{
			temp = new Rule(x+1, y);	//得到上一个格子的坐标
			
			for(Rule rolue: leastlines)
			{
				if(rolue.equals(temp))
				{
					Log.i("wcj", "migong getPreRolue point("+rolue.getX()+","+rolue.getY()+").dir = "+rolue.getDir());
					return rolue;
				}
			}
		}
		Log.i("wcj", "migong getPreRolue may error point ");
	return null;
	}

	private void selectSearch()
	{
		
		DistanceRule r1 = null;
		DistanceRule r2 = null;
		
		Log.i("wcj", "migong selectSearch");
		
		for(int i = 0; i < leastlines.size(); i++)
		{
			r1 = leastlines.get(i);	//取得第一个格子
			for(int j = i+1; j < leastlines.size(); j++)
			{
				r2 = leastlines.get(j);	//取得第二个格子
				if(r1.equals(r2))
				{
					Log.i("wcj", "migong selectSearch r1("+r1.getX()+","+r1.getY()+").dir = "+r1.getDir()+" distance = "+r1.getDistance());
					Log.i("wcj", "migong selectSearch r2("+r2.getX()+","+r2.getY()+").dir = "+r2.getDir()+" distance = "+r2.getDistance());
					if(r1.getDistance()<r2.getDistance())	//r1的路径更小
					{
						leastlines.remove(r2);
						Log.i("wcj", "migong selectSearch remove r2");
					}
					else	//r2的路径更小
					{
						leastlines.remove(r1);
						Log.i("wcj", "migong selectSearch remove r1");
					}
				}
			}
		}
		
	}

	private void initSearchs()
	{
		//第一种方法
		for (int x = 0; x < ContstUtil.BRICK_X; x++)
		{
			for (int y = 0; y < ContstUtil.BRICK_Y; y++)
			{
				if(gameview.xinxi[x][y] == KindType.EMPTY)	//为空的才没有访问过
				{
					searchs[x][y] = false;
				}else
				{
					searchs[x][y] = true; // 其它的情况都访问过
				}
			}
		}
		//searchs[gameview.end.x][gameview.end.y] = false;	//终点强置为没有访问???
		Log.i("wcj","migong initSearchs success");

	}

	private void ToRulesGame()
	{
		Log.i("wcj", "migong ToRulesGame");
		setContentView(R.layout.rulesgame);
		
		fanhuibutton = (ImageButton)findViewById(R.id.fanhui);
		rulesview = (TextView)findViewById(R.id.rulesgame);
		fanhuibutton.setOnClickListener(fanhuilistener);
	}

	private void ToAboutGame()
	{
		Log.i("wcj", "migong ToAboutGame");
		setContentView(R.layout.aboutgame);
		
		fanhuibutton = (ImageButton)findViewById(R.id.fanhui);
		aboutview = (TextView)findViewById(R.id.aboutview);
		String str = dbHelper.queryLevel();
		int i = Integer.parseInt(str);
		i = i - 1;
		str = String.valueOf(i);
		aboutview.setText(str);
		fanhuibutton.setOnClickListener(fanhuilistener);
	}

	private void ToGameView()
	{
		Log.i("wcj", "migong ToGameView");
		setContentView(R.layout.gameview);	
		initGame();
		if(dbHelper.queryLevel().length() != 0){
			gameview.level = Integer.parseInt(dbHelper.queryLevel());
		}else{
			dbHelper.insertData("1");
		}
	}
	private void initGame()
	{
		Log.i("wcj", "migong initGame");
		
		lines = new ArrayList<Rule>();
		result = new ArrayList<Rule>();
		leastlines = new ArrayList<DistanceRule>();
		astartlines = new ArrayList<AstartRule>();
		
		searchs = new boolean[ContstUtil.BRICK_X][ContstUtil.BRICK_Y];
		astarOpen = new ArrayList<AstartRule>();
		astarClosed = new ArrayList<AstartRule>();
		astarSon = new ArrayList<AstartRule>();
		
		gameview = (GameView)findViewById(R.id.mygameview);
		
		upbutton = (ImageButton)findViewById(R.id.up);
		rightbutton = (ImageButton)findViewById(R.id.right);
		downbutton = (ImageButton)findViewById(R.id.down);
		leftbutton = (ImageButton)findViewById(R.id.left);
		attactbutton = (ImageButton)findViewById(R.id.attact);
		//tishibutton = (ImageButton)findViewById(R.id.tishi);
		zuiduanbutton = (ImageButton)findViewById(R.id.zuiduan);
		fanhuibutton = (ImageButton)findViewById(R.id.fanhui);
		
		info = (TextView)findViewById(R.id.info);
		
		fanhuibutton.setOnClickListener(fanhuilistener);
		zuiduanbutton.setOnClickListener(zuiduanlistener);
		//tishibutton.setOnClickListener(tishilistener);
		upbutton.setOnClickListener(uplistener);
		rightbutton.setOnClickListener(rightlistener);
		downbutton.setOnClickListener(downlistener);
		leftbutton.setOnClickListener(leftlistener);
		attactbutton.setOnClickListener(attactlistener);
		
		//游戏结束对话框
		alertDialog = new AlertDialog.Builder(MiGong.this);
	}

	private void ToWelcomeView()
	{
		setContentView(R.layout.main);
		Log.i("wcj", "migong ToWelcomeView");
		
		startgame = (ImageButton)findViewById(R.id.startgame);
		rulesgame = (ImageButton)findViewById(R.id.rulesgame);
		aboutgame = (ImageButton)findViewById(R.id.aboutgame);
		quitgame = (ImageButton)findViewById(R.id.quitgame);
		setting = (ImageButton)findViewById(R.id.setting);

		Log.i("wcj", "migong ToWelcomeView startgame = "+startgame);
		
		startgame.setOnClickListener(startlistener);
		rulesgame.setOnClickListener(ruleslistener);
		aboutgame.setOnClickListener(aboutlistener);
		quitgame.setOnClickListener(quitlistener);
		setting.setOnClickListener(settinglistener);
	}

	public void setLevel(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(MiGong.this);
		dialog.setTitle("请输入");
		final EditText edit = new EditText(MiGong.this);
		dialog.setView(edit);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				dbHelper.deleteDataById("a");
				String str = edit.getText().toString();
				dbHelper.insertData(str);
			}
		});
		dialog.setNegativeButton("取消",  new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){

			}
		});
		dialog.show();
	}
}