package cn.pipi.mobile.pipiplayer.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.beans.DownloadAPK;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.db.PipiDBHelp;
import cn.pipi.mobile.pipiplayer.db.TableName;
import cn.pipi.mobile.pipiplayer.util.StringUtils;

public class DBHelperDao {
private final String  TAG="DBHelperDao";

	private PipiDBHelp pipiDBHelp;
	private Cursor cursor;
	public static DBHelperDao dbHelperDao;
	private SQLiteDatabase database = null;
	public DBHelperDao() {
		pipiDBHelp = PipiDBHelp.getInstance();
		database = pipiDBHelp.getWritableDatabase();
	}

	public synchronized static DBHelperDao getDBHelperDaoInstace() {

		if (dbHelperDao == null) {
			dbHelperDao = new DBHelperDao();
		}
		return dbHelperDao;
	}

	
	public synchronized void dropTable(){
		String sql;
		try {
			//database = pipiDBHelp.getWritableDatabase();
			sql = " drop table " + PipiDBHelp.STORE_TABLENAME;
			
			database.execSQL(sql);
		} finally {
                 closeDB();
		}
	}
	public synchronized void creTable(){
		try {
			database = pipiDBHelp.getWritableDatabase();
			pipiDBHelp.onCreate(database);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public synchronized void updateTable(){
    dropTable();
    creTable();
	}
//////*****************下载记录模块***********************************	
	//判断下载网址是否存在
			public synchronized boolean isMovieStoreByUrl(String url) {
				// TODO Auto-generated method stub
				boolean isStore = false;
				try {
					database = pipiDBHelp.getReadableDatabase();

					String sql = "select sMovieStoreState from "
							+ PipiDBHelp.STORE_TABLENAME 
							+ " where "+TableName.MovieUrl+"= '" + url + "'";
					cursor = database.rawQuery(sql,null);
					if (cursor.moveToNext()) {
						isStore = true;
					}
				} finally {
					closeCursor();
				}

				return isStore;
			}
	/**
	 * 插入影片下载数据
	 */
	public synchronized long insertMovieStore(DownLoadInfo downLoadInfo) {
		if(isStoreByUrl(downLoadInfo.getDownUrl()))return PipiPlayerConstant.ISEXIT_NORMOL;
		long sec = -1;
		try {
			ContentValues values = new ContentValues();
			values.put(TableName.MovieID, downLoadInfo.getDownID());
			values.put(TableName.MovieName, downLoadInfo.getDownName());
			values.put(TableName.MovieImgUrl, downLoadInfo.getDownImg());
			values.put(TableName.MovieUrl, downLoadInfo.getDownUrl());
			values.put(TableName.MoviePlaySourKey, downLoadInfo.getDownTag());
			
			ArrayList<String> list=downLoadInfo.getPlayList();
			JSONArray array=new JSONArray();
			for(String string:list)
			array.put(string);
			
			values.put(TableName.MoviePlayList, array.toString());
			values.put(TableName.MoviePlayPosition, downLoadInfo.getDownPosition());
			Log.i("TAG999", "insertMovieStore MoviePlayPosition==="+downLoadInfo.getDownPosition());
			database = pipiDBHelp.getWritableDatabase();
			sec = database.insert(PipiDBHelp.STORE_TABLENAME, null,values);
		}  catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}
	//查找下载完成后本地保存路径
	public synchronized String getLocalByUrl(String sMovieurl) {
							// TODO Auto-generated method stub
							try {
								database = pipiDBHelp.getReadableDatabase();

								String sql = "select * from "
										+ PipiDBHelp.STORE_TABLENAME 
										+ " where "+TableName.MovieUrl+"= '" + sMovieurl + "'";
								cursor = database.rawQuery(sql,null);
								if (cursor.moveToNext()) {
								return cursor.getString(cursor.getColumnIndex(TableName.MovieLocalUrl));
								}
							}  catch (Exception e) {
								// TODO: handle exception
							}finally {
								closeCursor();
							}

							return null;
						}
	/**
	 * 查找当前下载状态
	 */
	public synchronized long getMovieStoreState(String movieUrl) {
		long sec = -3;
		String sql = null;
		try {
			database = pipiDBHelp.getWritableDatabase();
					sql = "select * from " + PipiDBHelp.STORE_TABLENAME
							+ " where sMovieUrl= "+ "'" + movieUrl + "'";
					cursor = database.rawQuery(sql,null);
					if (cursor.moveToNext()) {
					return cursor.getInt(cursor.getColumnIndex(TableName.MovieLoadState));
					}
		}  catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}
	/**
	 * 更新下载文件总大小
	 */
	public synchronized long updataMovieStoreSize( String movieUrl, long size) {
		long sec = -1;
		String sql = null;
		try {
			database = pipiDBHelp.getWritableDatabase();
					sql = "update " + PipiDBHelp.STORE_TABLENAME
					+ " set sMovieSize = " + size  
					+ " where sMovieUrl= "
					+ "'" + movieUrl + "'";
					database.execSQL(sql);
					sec=1;
		}  catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}
	/**
	 * 更新下载保存路径
	 */
	public synchronized long updataMovieStoreLocal( String movieUrl, String local) {
		long sec = -1;
		String sql = null;
		try {
			database = pipiDBHelp.getWritableDatabase();
					sql = "update " + PipiDBHelp.STORE_TABLENAME
					+ " set sMovieLocalUrl = '" + local + "' where sMovieUrl= "
					+ "'" + movieUrl + "'";
					database.execSQL(sql);
					sec=1;
		}  catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}
	/**
	 * 更新下载下载状�?
	 */
	public synchronized long updataMovieStoreState( String movieUrl, int load) {
		long sec = -1;
		String sql = null;
		try {
			database = pipiDBHelp.getWritableDatabase();
					sql = "update " + PipiDBHelp.STORE_TABLENAME
					+ " set sMovieLoadState = " + load  + " where sMovieUrl= "
					+ "'" + movieUrl + "'";
					database.execSQL(sql);
					sec=1;
		}  catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}
	/**
	 * 更新下载进度
	 */
	public synchronized long updataMovieStoreProgress(String MovieUrl, long PlayProgress) {
		long sec = -1;
		String sql = null;
		try {
			database = pipiDBHelp.getWritableDatabase();
			sql = "update " + PipiDBHelp.STORE_TABLENAME
					+ " set sMoviePlayProgress = "  +  PlayProgress + " where sMovieUrl= "
					+ "'" + MovieUrl + "'";
			database.execSQL(sql);
			sec=1;
		}  catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}

	/**
	 * 更新播放进度  
	 */
	public synchronized long updataMovieHistroyProgress(String MovieUrl, int PlayProgress,int position) {
		long sec = -1;
		String sql = null;
		try {
			database = pipiDBHelp.getWritableDatabase();
			sql = "update " + PipiDBHelp.HISTROY_TABLENAME
					+ " set sMoviePlayProgress = " +  PlayProgress
					+ " , sMoviePlayPosition = " +  position
					+ " where sMovieUrl= "+ "'" + MovieUrl + "'";
			database.execSQL(sql);
			sec=1;
		}  catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}
	/**
	 * 是否 初次下载
	 */
	public synchronized boolean isStoreByUrl(String sMovieUrl) {
		// TODO Auto-generated method stub
		boolean isStore = false;
		try {
			database = pipiDBHelp.getReadableDatabase();

			String sql = "select sMovieStoreState from "
					+ PipiDBHelp.STORE_TABLENAME 
         + " where "+TableName.MovieUrl+"= '" + sMovieUrl + "'";
			cursor = database.rawQuery(sql,
					null);
			if (cursor.moveToNext()) {
				isStore = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			closeCursor();
		}

		return isStore;
	}
	 public synchronized List<DownTask> getActiveDownLoad() {
	        List<DownTask> mDownLoadTaskList = new ArrayList<DownTask>();
	        SQLiteDatabase database = null;
	        Cursor cursor = null;
			String sql = "select  *  from " + PipiDBHelp.STORE_TABLENAME;

	        try {
	            database = pipiDBHelp.getReadableDatabase();
	            cursor = database.rawQuery(sql, null);
	            while (cursor.moveToNext()) {

	            	DownLoadInfo downInfo = new DownLoadInfo();
	            	downInfo.setDownID(cursor.getString(cursor
							.getColumnIndex("sMovieID")));
	            	downInfo.setDownName(cursor.getString(cursor
							.getColumnIndex("sMovieName")));
	            	downInfo.setDownImg(cursor.getString(cursor
							.getColumnIndex("sMovieImgUrl")));
	            	downInfo.setDownUrl(cursor.getString(cursor
							.getColumnIndex("sMovieUrl")));
	            	downInfo.setDownTotalSize(cursor.getLong(cursor
							.getColumnIndex("sMovieSize")));
	            	downInfo.setDownProgress(cursor.getLong(cursor
							.getColumnIndex("sMoviePlayProgress")));
					downInfo.setDownState(cursor.getInt(cursor
							.getColumnIndex("sMovieLoadState")));
					downInfo.setDownPosition(cursor.getInt(cursor
							.getColumnIndex(TableName.MoviePlayPosition)));
					downInfo.setDownTag(cursor.getString(cursor
							.getColumnIndex(TableName.MoviePlaySourKey)));
					String path = cursor.getString(cursor
							.getColumnIndex("sMovieLocalUrl"));
					downInfo.setDownPath(path);
					JSONArray array;
					try {
						array = new JSONArray(cursor.getString(cursor
								.getColumnIndex("sMoviePlayList")));
						ArrayList<String> list=new ArrayList<String>();
						for(int i=0;i<array.length();i++)
							list.add(array.getString(i));
							
						downInfo.setPlayList(list);
						//下载片段索引
						if(!TextUtils.isEmpty(path)){
							try {
								File file=new File(path);
								if(file!=null&&file.isDirectory()){
							downInfo.setDownIndex(file.listFiles().length-1>0?file.listFiles().length-1:0);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
	            	DownTask task = new DownTask(downInfo);
	            	if(downInfo.getDownState()== DownTask.TASK_FileMerge){
	            		//task.finish();//上次未合并完的再次进行合并
	            	}else if (downInfo.getDownState()!= DownTask.TASK_FINISHED) //已经下载完的
	            		 downInfo.setDownState(DownTask.TASK_PAUSE_DOWNLOAD);//未下载完的标示为暂停，让用户主动去下�?
	                mDownLoadTaskList.add(task);
	            }
	        } finally {
	            if (cursor != null) {
	                cursor.close();
	            }
	            database.close();
	        }
	        return mDownLoadTaskList;
	    }
	/**
	 * 得到影片下载列表
	 */

	public synchronized List<DownLoadInfo> getSmovieStores() {

		List<DownLoadInfo> resultsInfos = new ArrayList<DownLoadInfo>();
		String sql = "select  *  from " + PipiDBHelp.STORE_TABLENAME
				+ " order by "+TableName.Movie_ID+" desc ";
		try {
			database = pipiDBHelp.getWritableDatabase();
			cursor = database.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() != 0) {
				while (cursor.moveToNext()) {
					DownLoadInfo downInfo = new DownLoadInfo();
					downInfo.setDownID(cursor.getString(cursor
							.getColumnIndex("sMovieID")));
	            	downInfo.setDownName(cursor.getString(cursor
							.getColumnIndex("sMovieName")));
	            	downInfo.setDownImg(cursor.getString(cursor
							.getColumnIndex("sMovieImgUrl")));
	            	downInfo.setDownUrl(cursor.getString(cursor
							.getColumnIndex("sMovieUrl")));
	            	downInfo.setDownTotalSize(cursor.getLong(cursor
							.getColumnIndex("sMovieSize")));
	            	downInfo.setDownProgress(cursor.getLong(cursor
							.getColumnIndex("sMoviePlayProgress")));
					downInfo.setDownState(cursor.getInt(cursor
							.getColumnIndex("sMovieLoadState")));
					downInfo.setDownPosition(cursor.getInt(cursor
							.getColumnIndex(TableName.MoviePlayPosition)));
					downInfo.setDownTag(cursor.getString(cursor
							.getColumnIndex(TableName.MoviePlaySourKey)));
					String path = cursor.getString(cursor
							.getColumnIndex("sMovieLocalUrl"));
					downInfo.setDownPath(path);
					JSONArray array;
					try {
						array = new JSONArray(cursor.getString(cursor
								.getColumnIndex("sMoviePlayList")));
						ArrayList<String> list=new ArrayList<String>();
						for(int i=0;i<array.length();i++)
							list.add(array.getString(i));
							
						downInfo.setPlayList(list);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//下载片段索引
					if(!TextUtils.isEmpty(path)){
						try {
							File file=new File(path);
							if(file!=null&&file.isDirectory()){
						downInfo.setDownIndex(file.listFiles().length-1>0?file.listFiles().length-1:0);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					
					resultsInfos.add(downInfo);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			closeCursor();
		}
		return resultsInfos;
	}

	/**
	 * 删除下载表中 单条数据
	 * 
	 * 
	 */
	public synchronized void delSingleSmovieStore(String sMovieUrl) {
		String sql = "delete from " + PipiDBHelp.STORE_TABLENAME
				+ " where sMovieUrl = " + "'" + sMovieUrl + "'";

		try {
			database = pipiDBHelp.getWritableDatabase();
			database.execSQL(sql);
		}  catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 删除下载表中 删除数据
	 * 
	 * 
	 */
	public synchronized void delAllSmovieStore() {

		String sql = "delete from " + PipiDBHelp.STORE_TABLENAME;
		try {
			database = pipiDBHelp.getWritableDatabase();
			database.execSQL(sql);
		}  catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
//////*****************l历史记录模块***********************************	
	 //数据库中是否存在
		public synchronized boolean isMovieHistroyByID(String sMovieID) {
			// TODO Auto-generated method stub
			boolean isStore = false;
			try {
				database = pipiDBHelp.getReadableDatabase();
				String sql = "select sMovieID from "
						+ PipiDBHelp.HISTROY_TABLENAME 
						+ " where "+TableName.MovieID+"= '" + sMovieID + "'";
				cursor = database.rawQuery(sql,null);
				if (cursor.moveToNext()) {
					
					isStore = true;
				}
			}  catch (Exception e) {
				// TODO: handle exception
			}finally {
				closeCursor();
			}

			return isStore;
		}
		//查找第几集播放进度
	public synchronized long getHistroyProgressByID(String sMovieID,int position) {
					// TODO Auto-generated method stub
					try {
						database = pipiDBHelp.getReadableDatabase();

						String sql = "select * from "
								+ PipiDBHelp.HISTROY_TABLENAME 
								+ " where "+TableName.MovieID+"= '" + sMovieID + "'";
						cursor = database.rawQuery(sql,null);
						if (cursor.moveToNext()) {
							if(position==cursor.getInt(cursor
									.getColumnIndex(TableName.MoviePlayPosition))){
								long  progress = cursor.getLong(cursor
										.getColumnIndex(TableName.MoviePlayProgress));
								Log.i("TAG999", "insertMovieHistroy  = "+position+"*******"+progress);
							return progress;
							}
						}
					}  catch (Exception e) {
						// TODO: handle exception
					}finally {
						closeCursor();
					}

					return 0;
				}
	//查找播放当前第几集
	public synchronized int getHistroyPositionByID(String sMovieID) {
					// TODO Auto-generated method stub
					int position = 0;
					try {
						database = pipiDBHelp.getReadableDatabase();

						String sql = "select * from "
								+ PipiDBHelp.HISTROY_TABLENAME 
								+ " where "+TableName.MovieID+"= '" + sMovieID + "'";
						cursor = database.rawQuery(sql,null);
						if (cursor.moveToNext()) {
						  position = cursor.getInt(cursor
									.getColumnIndex(TableName.MoviePlayPosition));
						 if(position<0)position=0;;
						}
					}  catch (Exception e) {
						// TODO: handle exception
					}finally {
						closeCursor();
					}

					return position;
				}
	/**sMovieUrl
	 * 插入影片历史数据
	 */
	public synchronized long insertMovieHistroy(DownLoadInfo downInfo) {
		long sec = -1;
		try {
			ContentValues values = new ContentValues();
			values.put(TableName.MovieID, downInfo.getDownID());
			values.put(TableName.MovieName, downInfo.getDownName());
			values.put(TableName.MovieImgUrl, downInfo.getDownImg());
			values.put(TableName.MovieUrl, StringUtils.getDate());//MovieUrl 在此处替代插入时间
			values.put(TableName.MoviePlaySourKey, downInfo.getDownTag());
			values.put(TableName.MoviePlayProgress, downInfo.getDownProgress());
			values.put(TableName.MovieSize, downInfo.getDownTotalSize());
			values.put(TableName.MoviePlayPosition, downInfo.getDownPosition());
			values.put(TableName.MovieLocalUrl, downInfo.getDownPath());
			database = pipiDBHelp.getWritableDatabase();
			Log.i("TAG999", "insertMovieHistroy  = "+downInfo.getDownPosition()+"*******"+downInfo.getDownProgress());
			if(isMovieHistroyByID(downInfo.getDownID())){//存在记录，删除记录
			//	sec = database.update(PipiDBHelp.HISTROY_TABLENAME, values, "sMovieID=?",
			//			new String[]{downInfo.getDownID()});
				sec = database.delete(PipiDBHelp.HISTROY_TABLENAME, "sMovieID=?", new String[]{downInfo.getDownID()});
			}
				sec = database.insert(PipiDBHelp.HISTROY_TABLENAME, null,values);
		}  catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}

	/**
	 * 得到影片历史列表
	 */

	public synchronized List<DownLoadInfo> getSmovieHistroy() {

		List<DownLoadInfo> resultsInfos = new ArrayList<DownLoadInfo>();
		String sql = "select  *  from " + PipiDBHelp.HISTROY_TABLENAME +" order by "+TableName.Movie_ID+" desc";
		try {
			database = pipiDBHelp.getWritableDatabase();
			cursor = database.rawQuery(sql,null);
			if (cursor != null && cursor.getCount() != 0) {
				while (cursor.moveToNext()) {
					DownLoadInfo downInfo = new DownLoadInfo();
					downInfo.setDownID(cursor.getString(cursor
							.getColumnIndex("sMovieID")));
	            	downInfo.setDownName(cursor.getString(cursor
							.getColumnIndex("sMovieName")));
	            	downInfo.setDownImg(cursor.getString(cursor
							.getColumnIndex("sMovieImgUrl")));
	            	downInfo.setDownUrl(cursor.getString(cursor
							.getColumnIndex("sMovieUrl")));
	            	downInfo.setDownTotalSize(cursor.getInt(cursor
							.getColumnIndex("sMovieSize")));
	            	downInfo.setDownProgress(cursor.getInt(cursor
							.getColumnIndex("sMoviePlayProgress")));
					downInfo.setDownState(cursor.getInt(cursor
							.getColumnIndex("sMovieLoadState")));
					downInfo.setDownTag(cursor.getString(cursor
							.getColumnIndex(TableName.MoviePlaySourKey)));
					downInfo.setDownPath(cursor.getString(cursor
							.getColumnIndex("sMovieLocalUrl")));
					downInfo.setDownPosition(cursor.getInt(cursor
							.getColumnIndex(TableName.MoviePlayPosition)));
					resultsInfos.add(downInfo);
				}
			}
			

		}  catch (Exception e) {
			// TODO: handle exception
		}finally {
			closeCursor();
		}
		return resultsInfos;
	}
	/**
	 * 删除历史表中 单条数据
	 * 
	 * 
	 */
	public synchronized void delSingleSmovieHistroy(String sMovieID) {

		String sql = "delete from " + PipiDBHelp.HISTROY_TABLENAME
				+ " where sMovieID = " + "'" + sMovieID + "'";

		int sec = -1;
		try {
			database = pipiDBHelp.getWritableDatabase();
			// sec = database.delete(PipiDBHelp.STORE_TABLENAME, "sMovieID = ?",
			// new String[] { MoiveInfo.getMovieID() });
			database.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 删除历史表中 �?��数据
	 * 
	 * 
	 */
	public synchronized void delAllSmovieHistroy() {

		String sql = "delete from " + PipiDBHelp.HISTROY_TABLENAME;
		try {
			database = pipiDBHelp.getWritableDatabase();
			database.execSQL(sql);
		}  catch (Exception e) {
			// TODO: handle exception
		}
	}
//////*****************收藏记录模块***********************************
	 //数据库中是否存在
		public synchronized boolean isMovieSaveByID(String sMovieID) {
			// TODO Auto-generated method stub
			boolean isStore = false;
			try {
				database = pipiDBHelp.getReadableDatabase();

				String sql = "select sMovieID from "
						+ PipiDBHelp.SAVE_TABLENAME + " where sMovieID=?";
				cursor = database.rawQuery(sql,
						new String[] {sMovieID });
				if (cursor.moveToNext()) {
					isStore = true;
				}
			}  catch (Exception e) {
				// TODO: handle exception
			}finally {
				closeCursor();
			}

			return isStore;
		}
	/**
	 * 影片收藏
	 */
	public synchronized long insertMovieSave(DownLoadInfo info) {
		if(isMovieSaveByID(info.getDownID()))return PipiPlayerConstant.ISEXIT_NORMOL;
		long sec = -1;
		try {
			ContentValues values = new ContentValues();
			values.put(TableName.MovieID, info.getDownID());
			values.put(TableName.MovieName, info.getDownName());
			values.put(TableName.MovieImgUrl, info.getDownImg());
			database = pipiDBHelp.getWritableDatabase();
			sec = database.insert(PipiDBHelp.SAVE_TABLENAME, null,
					values);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}

	/**
	 * 得到影片收藏列表
	 */

	public synchronized List<DownLoadInfo> getSmovieSave() {

		List<DownLoadInfo> resultsInfos = new ArrayList<DownLoadInfo>();
		String sql = "select  *  from " + PipiDBHelp.SAVE_TABLENAME+" order by "+TableName.Movie_ID+" desc";
		try {
			database = pipiDBHelp.getWritableDatabase();
			cursor = database.rawQuery(sql,null);
			if (cursor != null && cursor.getCount() != 0) {
				while (cursor.moveToNext()) {
					DownLoadInfo info = new DownLoadInfo();
					info.setDownID(cursor.getString(cursor
							.getColumnIndex(TableName.MovieID)));
					info.setDownName(cursor.getString(cursor
							.getColumnIndex(TableName.MovieName)));
					info.setDownImg(cursor.getString(cursor
							.getColumnIndex(TableName.MovieImgUrl)));
					
					resultsInfos.add(info);
				}
			}else{
				
			}
			

		}  catch (Exception e) {
			// TODO: handle exception
		}finally {
			closeCursor();
		}
		return resultsInfos;
	}

	/**
	 * 删除收藏表中 单条数据
	 * 
	 * 
	 */
	public synchronized void delSingleSmovieSave(String sMovieID) {

		String sql = "delete from " + PipiDBHelp.SAVE_TABLENAME
				+ " where sMovieID = " + "'" + sMovieID + "'";

		int sec = -1;
		try {
			database = pipiDBHelp.getWritableDatabase();
			database.execSQL(sql);
		}  catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 删除收藏表中 �?��数据
	 * 
	 * 
	 */
	public synchronized void delAllSmovieSave() {

		String sql = "delete from " + PipiDBHelp.SAVE_TABLENAME;
		try {
			database = pipiDBHelp.getWritableDatabase();
			database.execSQL(sql);
		}  catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public synchronized long insertKeys(String key) {
		long sec = -1;
		try {
			ContentValues values = new ContentValues();
			values.put(TableName.KEY, key);
			database = pipiDBHelp.getWritableDatabase();
			//先执行删�?如果已经存在则删�? 
			String[] args = {String.valueOf(key)};
			database.delete(TableName.KEYS_TABLENAME, TableName.KEY+" = ?", args);
		//	delSingleKey(key);//避免重复
			sec = database.insert(TableName.KEYS_TABLENAME, null,
					values);
		}  catch (Exception e) {
			// TODO: handle exception
		}
		return sec;
	}
	public synchronized List<String> getKeys(int num) {

		List<String> keys = new ArrayList<String>();
		String sql = "select  *  from " + TableName.KEYS_TABLENAME
				+ " order by "+TableName.Movie_ID+" desc ";
		try {
			database = pipiDBHelp.getWritableDatabase();
			cursor = database.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() != 0) {
				while (cursor.moveToNext()) {
					if(keys.size()<6){
						keys.add(cursor.getString(cursor
								.getColumnIndex(TableName.KEY)));
					}else{
						delSingleKey(cursor.getString(cursor
								.getColumnIndex(TableName.KEY)));
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			closeCursor();
		}
		
		return keys;
	}
	public synchronized void delAllKeys() {

		String sql = "delete from " + TableName.KEYS_TABLENAME;
		try {
			database = pipiDBHelp.getWritableDatabase();
			database.execSQL(sql);
		}  catch (Exception e) {
			// TODO: handle exception
		}
	}
	public synchronized boolean isKeyBykey(String key) {
		// TODO Auto-generated method stub
		boolean isStore = false;
		try {
			String sql = "select "+TableName.KEY+" from "
					+ TableName.KEYS_TABLENAME 
         + " where "+TableName.KEYS_TABLENAME+"= '" + key + "'";
			cursor = database.rawQuery(sql,
					null);
			if (cursor.moveToNext()) {
				isStore = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			closeCursor();
		}

		return isStore;
	}
	public synchronized void delSingleKey(String key) {

		String sql = "delete from " + TableName.KEYS_TABLENAME
				+ " where "+TableName.KEY+" = " + "'" + key + "'";
		int sec = -1;
		try {
			database = pipiDBHelp.getWritableDatabase();
			database.execSQL(sql);
		}  catch (Exception e) {
		}
	}
	private void closeDB(){
		if (database != null) {
			database.close();
			database = null;
		}
	}
	private void closeCursor(){
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	
	
	///********************APK断点下载区域*****************************************************
	
	public boolean isDownload(String version){
		String sql = "select * from download_info where version=?";
		try {
			database = pipiDBHelp.getWritableDatabase();
			 cursor = database.rawQuery(sql, new String[]{version});
			if(cursor.moveToNext()){
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			closeCursor();
		}
		return false;
	}
	
	/**
	 * 保存APK下载的断点的信息
	 */
	public void saveInfos(DownloadAPK info) {
		 if(isDownload(info.getVersion()))deleteInfos(info.getVersion());
		try {
			 String sql = "insert into download_info ( startpoint , endpoint , compeleteload , filesize , url , version , path ) values (?,?,?,?,?,?,?)";
	           Object[] bindArgs = { info.getStartPoint(),
	                    info.getEndPoint(),info.getCompleteload(),info.getLoadFileSize(),
	                    info.getUrl(),info.getVersion(),info.getPath() };
	           database = pipiDBHelp.getWritableDatabase();
	           database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			// TODO: handle exception
			
		}
    }
	/**
	 * 得到APK下载的断点的信息
	 * @param urlstr
	 * @return
	 */
	 public DownloadAPK getInfos(String version) {
         String sql = "select * from download_info where version = ? ";
         try {
        	 database = pipiDBHelp.getWritableDatabase();
        	  cursor = database.rawQuery(sql, new String[] { version });
              if (cursor.moveToNext()) {
                  DownloadAPK info = new DownloadAPK(
                		  cursor.getInt(cursor.getColumnIndex(TableName.APK_START)),
                		  cursor.getInt(cursor.getColumnIndex(TableName.APK_END)),
                		  cursor.getInt(cursor.getColumnIndex(TableName.APK_SIZE)),
                		  cursor.getInt(cursor.getColumnIndex(TableName.APK_COMPLETE)),
                		  cursor.getString(cursor.getColumnIndex(TableName.APK_URL)),
                		  cursor.getString(cursor.getColumnIndex(TableName.APK_VERSION)),
                		  cursor.getString(cursor.getColumnIndex(TableName.APK_PATH)));
                  return info;
              }
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			closeCursor();
		}
         return null;
     }
	 /**
	  * 更新APK下载点的信息
	  * @param info
	  */
	 public void updateInfos(DownloadAPK info){
		 String sql = "update download_info set compeleteload = ? where version = ? ";
		 try {
			 Object[] values = {info.getCompleteload(),info.getVersion()};
			 database = pipiDBHelp.getWritableDatabase();
			 database.execSQL(sql, values);
		} catch (Exception e) {
			// TODO: handle exception
		}
	 }
	 public void deleteInfos(String version){
		 String sql = "delete from download_info where version = ? ";
		 try {
			 Object[] vlaue = {version};
			 database = pipiDBHelp.getWritableDatabase();
			 database.execSQL(sql,vlaue);
		} catch (Exception e) {
			// TODO: handle exception
		}
	 }
    
}
