package com.zte.wcj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moon on 2016/6/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    //表名
    private static String TableName = "level";
    //数据库名
    private static String DBName = "test.db";
    //数据库版本号
    private static int DBVersion = 1;
    private Context context;
    //数据库实例
    private SQLiteDatabase database;
    //此类自己的实例
    public static DBHelper dbHelper;
    //创建数据库的语句
    private String createDBSql =
            "create table level(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "areaId TEXT NOT NULL default a UNIQUE," +
                    "level TEXT NOT NULL default '1');";

    public DBHelper(Context context){
        super(context, DBName, null, DBVersion);
        this.context = context;
    }

    //DBHepler单例模式，节省资源，防止访问冲突
    public static synchronized DBHelper getInstance(Context context){
        if(dbHelper == null){
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createDBSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //插入数据，使用ContentValues方式传入
    public void insertData(String values){
        ContentValues contentValues = new ContentValues();
        contentValues.put("level",values);
        contentValues.put("areaId","a");
        database = getWritableDatabase();
        database.insert(TableName, null, contentValues);
    }

    //通过id删除数据
    public void deleteDataById(String areaId){
        String[] args = {areaId};
        //这里需要可写的数据库
        database = getWritableDatabase();
        database.delete(TableName, "areaId=?", args);
    }

    //查询所有数据
    public String queryLevel(){
        String str = "";
        //这里需要可读的数据库
        database = getReadableDatabase();
        Cursor cursor = database.query(TableName, null, null, null, null, null, null, null);
        while(cursor.moveToNext()){
           str = cursor.getString(cursor.getColumnIndex("level"));
        }
        return str;
    }
}

