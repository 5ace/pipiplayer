package cn.pipi.mobile.pipiplayer.db;

import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库
 * 
 * @author qiny
 * 
 */
public class PipiDBHelp extends SQLiteOpenHelper {

	// 数据库名字
	public static String pipiPlayerDBName = TableName.pipiPlayerDBName;

	// 数据库版本号
	public static final int dbVersion = 1;

	public static PipiDBHelp pipiDBHelp = null;

	// 数据库表名 收藏
	// 收藏表 中字段 影片 ID 影片url地址 影片名字 影片图片地址 影片播放进度 是否收藏状态
	public static final String STORE_TABLENAME = TableName.Movie_TABLENAME;
	public static final String HISTROY_TABLENAME = TableName.HISTROY_TABLENAME;
	public static final String SAVE_TABLENAME = TableName.SAVE_TABLENAME;
	public  final String CREATETABLE = "( "+TableName.Movie_ID+" integer primary key autoincrement,"+TableName.MovieID+" text,"
			+TableName.MovieName+" Text,"+TableName.MovieImgUrl+" Text ,"
			+TableName.MovieUrl+" text, "+TableName.MoviePlaySourKey+" text, "+TableName.MovieLocalUrl+" text, "+TableName.MoviePlayProgress+" Integer,"
			+TableName.MovieStoreState+"  Integer,"+TableName.MovieLoadState+"  Integer ,"+TableName.MovieSize+"  Integer,"
			+TableName.MoviePlayList+"  text,"+TableName.MoviePlayPosition+"  Integer"+ " );";
	public  final String CREATETABLE2 = "( "+TableName.Movie_ID+" integer primary key autoincrement,"+TableName.KEY+" text "
			+ " );";
	public  final String CREATETABLE3 = "( "+TableName.APK_ID+" integer primary key autoincrement,"+TableName.APK_START+" integer ,"
			+TableName.APK_END+" integer ,"+TableName.APK_COMPLETE+" integer ,"+TableName.APK_SIZE+" integer ,"+TableName.APK_URL+" text ,"
			+TableName.APK_VERSION+" text ,"+TableName.APK_PATH+" text "+ " );";
	
	private final String CREATE_STORE_TABLE = "create table "
			+ STORE_TABLENAME
			+CREATETABLE;
	private final String CREATE_HISTROY_TABLE = "create table "
			+ TableName.HISTROY_TABLENAME
			+ CREATETABLE;
	private final String CREATE_SAVE_TABLE = "create table "
			+ TableName.SAVE_TABLENAME
			+CREATETABLE;
	//搜索关键词
	private final String CREATE_KEYS_TABLE = "create table "
			+ TableName.KEYS_TABLENAME
			+CREATETABLE2;
	//断点下载APK
	private final String CREATE_APK_TABLE = "create table "
			+ TableName.APK_TABLENAME
			+CREATETABLE3;
	
	public PipiDBHelp(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public PipiDBHelp() {
		super(VLCApplication.getAppContext(), pipiPlayerDBName, null, dbVersion);
	}

	/**
	 * 单例
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static PipiDBHelp getInstance() {

		if (pipiDBHelp == null) {
			pipiDBHelp = new PipiDBHelp();
		}

		return pipiDBHelp;

	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		// TODO Auto-generated method stub
		// 创建影片收藏表
		sqLiteDatabase.execSQL(CREATE_STORE_TABLE);
		sqLiteDatabase.execSQL(CREATE_HISTROY_TABLE);
		sqLiteDatabase.execSQL(CREATE_SAVE_TABLE);
		sqLiteDatabase.execSQL(CREATE_KEYS_TABLE);
		
		sqLiteDatabase.execSQL(CREATE_APK_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(newVersion>oldVersion){
			//选择你想删除更新哪些表，可以保留部分表数据
			 db.execSQL("DROP TABLE IF EXISTS  "+TableName.HISTROY_TABLENAME);
			 db.execSQL("DROP TABLE IF EXISTS  "+TableName.SAVE_TABLENAME);
			 db.execSQL("DROP TABLE IF EXISTS  "+TableName.Movie_TABLENAME);
	         onCreate(db);
		}
	}
	
	 public boolean deleteDatabase(Context context) { 
		 return context.deleteDatabase(pipiPlayerDBName); 
	 }
}
