package cn.pipi.mobile.pipiplayer.db;

public class TableName {
	//数据库
	public static final String pipiPlayerDBName="pipiplayer.db";//
	
	//下载列表
	public static final String Movie_TABLENAME="pipiplayer_storeinfo";//表名
	public static final String Movie_ID="_id";//ID
	public static final String MovieID="sMovieID";
	public static final String MovieName="sMovieName";
	public static final String MovieImgUrl="sMovieImgUrl";
	
	public static final String MovieUrl="sMovieUrl";
	public static final String MovieLocalUrl="sMovieLocalUrl";
	public static final String MoviePlayProgress="sMoviePlayProgress";
	public static final String MovieStoreState="sMovieStoreState";
	public static final String MovieLoadState="sMovieLoadState";
	public static final String MovieSize="sMovieSize";
	public static final String MoviePlaySourKey="sMoviePlaySourKey";
	public static final String MoviePlayList="sMoviePlayList";
	public static final String MoviePlayPosition="sMoviePlayPosition";
	
	//历史
	public static final String HISTROY_TABLENAME="histroy_info";//表名

	//收藏
	public static final String SAVE_TABLENAME="save_info";//表名
		
	//历史搜索词汇
	public static final String KEYS_TABLENAME="keys_info";//表名
	public static final String KEY="key";//关键词
	
	
	//APK信息
	public static final String APK_TABLENAME="download_info";//表名
	public static final String APK_ID="_id";//ID
	public static final String APK_START="startpoint";
	public static final String APK_END="endpoint";
	public static final String APK_COMPLETE="compeleteload";
	public static final String APK_SIZE="filesize";
	public static final String APK_URL="url";
	public static final String APK_VERSION="version";
	public static final String APK_PATH="path";
	
	//集数表
	public static final String Down_TABLENAME="pipiplayer_downinfo";//表名
	public static final String DownID="sDownID";//ID 自增长
	public static final String DownMovieID="sMovieID";//关联电影
	public static final String DownName="sDownName";
	public static final String DownImg="sDownImg";
	public static final String DownUrl="sDownUrl";
	public static final String DownPath="sDownPath";
	public static final String DownTag="sDownTag";
	public static final String DownCount="sDownCount";
	public static final String DownPosition="sDownPosition";
	public static final String DownState="sDownState";
	public static final String ParseMode="sParseMode";
	public static final String DownTotalSize="sDownTotalSize";
	public static final String DownCompleteSize="sDownCompleteSize";
	public static final String DownIndex="sDownIndex";
	
	//片段表
		public static final String Task="pipiplayer_taskinfo";//表名
		public static final String TaskID="sTaskID";//ID
		public static final String TaskDownID="sDownID";//关联集数
		public static final String TaskUrl="sTaskUrl";
		public static final String TaskPath="sTaskPath";
		public static final String TaskState="sTaskState";
		public static final String TaskTotalSize="sTaskTotalSize";
		public static final String TaskCompleteSize="sTaskCompleteSize";
		public static final String TaskIndex="sTaskIndex";
		public static final String TaskTime="sTaskTime";
		
}
