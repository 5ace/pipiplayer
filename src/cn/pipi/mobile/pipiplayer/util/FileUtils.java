package cn.pipi.mobile.pipiplayer.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 文件操作工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class FileUtils {

	/**
	 * 判断SD卡是否存在
	 * @return boolean(true表示SD卡存在，false表示SD卡不存在)
	 */
	public static String APKDirCaches;
	public static String fileCaches;
	public static String fileImgCaches;
	public static String fileConfDir;
	private final static int FILEOUTTIME=7*24;//图片过期时间  暂时设为7天
	
	public static String RootPath=null;//被选择安装的路径
	

	/**
	 * 创建相关 所需目录
	 */
	public static boolean makeAppCacheDir() {
		//检索是否创建过路径
		String sd=Environment.getExternalStorageDirectory().getAbsolutePath();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(VLCApplication.getAppContext());
		String sdPath=pref.getString("pipicache", sd);
		File parent = new File(sdPath);
		if (parent.exists()) {
			RootPath=sdPath+"/pipiplayer";
		}else{
			pref.edit().putString("pipicache", sd).commit();
			RootPath=sd+"/pipiplayer";
		}
		File Root = new File(RootPath);
		if(!Root.exists()){
			Root.mkdirs();
		}
		if(!Root.exists())return false;
		//File CacheImageDir = new File(RootPath, "CacheImageDir");
		File CacheImageDir = VLCApplication.getAppContext().getCacheDir();
		File Caches = new File(RootPath, "Caches");
		File CacheAPKDir = new File(RootPath, "APKDir");
		File CacheConfDir = new File(RootPath, "CacheConfDir");
		if (!CacheImageDir.exists()) {
			CacheImageDir.mkdirs();
		} 
		fileImgCaches=CacheImageDir.getAbsolutePath();
		Log.i("TAG9999", "fileImgCaches ===== "+fileImgCaches);
		
		if (!Caches.exists()) {
			Caches.mkdirs();
		}
		fileCaches = Caches.getAbsolutePath() + File.separator;
		
		if (!CacheConfDir.exists()) {
			CacheConfDir.mkdirs();
		}
		fileConfDir = CacheConfDir.getAbsolutePath() + File.separator;
		Log.i("TAG999","downCenter fileConfDir-->"+fileConfDir);
		
		if (CacheAPKDir.exists()) {
			APKDirCaches = CacheAPKDir.getAbsolutePath() ;
		}
		parent=null;
		CacheImageDir=null;
		Caches=null;
		CacheAPKDir=null;
	/*	//检测无用文件
		new Thread(){
			 @Override
		        public void run() {
				 deleteUselessFile();
			 }
		}.start();*/
		return true;
	}
	/***
	 * 创建文件  生成APK文件
	 */
	public static File createFile(String name) {
		File APKDir = new File(RootPath, "APKDir");
		if (!APKDir.exists()) {
			APKDir.mkdirs();
		} 
		
		StringBuffer string=new StringBuffer(APKDir.getAbsolutePath());
		string.append(File.separator);
		string.append(name);
		File file=new File(string.toString());
		APKDir=null;
		return file;
	}
	
	/**
	 * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * @param context
	 * @param msg
	 */
	public static void write(Context context, String fileName, String content) {
		if (content == null)
			content = "";

		try {
			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			fos.write(content.getBytes());

			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文本文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String readInStream(FileInputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

	/**
	 * 向手机写图片
	 * 
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static boolean writeFile(byte[] buffer, String folder,
			String fileName) {
		boolean writeSucc = false;

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

		String folderPath = "";
		if (sdCardExist) {
			folderPath = Environment.getExternalStorageDirectory()
					+ File.separator + folder + File.separator;
		} else {
			writeSucc = false;
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folderPath + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
			writeSucc = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writeSucc;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (StringUtils.isEmpty(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return "";
		}
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
				point);
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (StringUtils.isEmpty(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 获取文件大小
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取目录文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 获取目录文件个数
	 * 
	 * @param f
	 * @return
	 */
	public long getFileList(File dir) {
		long count = 0;
		File[] files = dir.listFiles();
		count = files.length;
		for (File file : files) {
			if (file.isDirectory()) {
				count = count + getFileList(file);// 递归
				count--;
			}
		}
		return count;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String name) {
		boolean status;
		if (!name.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + name);
			status = newPath.exists();
		} else {
			status = false;
		}
		return status;

	}

	/**
	 * 计算SD卡的剩余空间
	 * 
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 * 
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status;
		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdirs();
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 检查是否安装SD卡
	 * 
	 * @return
	 */
	public static boolean checkSaveLocationExists() {
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * 
	 * @param fileName
	 * @return
	 */
	public static void deleteDirectory(String path) {
		SecurityManager checker = new SecurityManager();
		if (!path.equals("")) {
			File newPath = new File(path);
			if(newPath==null) 
				return;
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/"
								+ listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} 
	}

	/** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static void deleteFile(String filePath) {
		if (!TextUtils.isEmpty(filePath)) {
			File newPath = new File(filePath);
			if (newPath!=null&&newPath.isFile()) {
					newPath.delete();
		} }
	}
    public static void deleteMovieFile(String urlstr){//删除跟上次播放不一致的影片
    	SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(VLCApplication.getAppContext());
    	String preMovie=sharedPreferences.getString("preMovie", null);//上次影片
    	sharedPreferences.edit().putString("preMovie", urlstr).commit();//保存文件名，用来下次判断是否播放同1影片
    	if(preMovie==null||DBHelperDao.getDBHelperDaoInstace().isStoreByUrl(preMovie))
    		return;//上次影片在下载记录，不能删除
		if(!preMovie.equals(urlstr)){//匹配是否一致
				deleteFinishedFile(preMovie);
			}
}
    public static void deleteFinishedFile(String url){//删除影片文件
    File file=new File(FileUtils.getFileCaches());
	if (!file.exists()) return;
	final String ppfilmHashID=MD5Util.getMD5HashIDByUrl(url);
	if(ppfilmHashID==null) return;
	File[] childFiles = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isFile()&&pathname.getAbsolutePath().contains(ppfilmHashID);
			}
		});
		if(childFiles!=null&&childFiles.length!=0)
		for (File file1 : childFiles) {
				file1.delete();
		}
    }
    
    public static void deleteAPKFile(){//删除APK文件
    	if(APKDirCaches==null)return;
    	File file=new File(APKDirCaches);
		if (!file.exists()) return;
	File[] childFiles = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname!=null;
			}
		});
		if(childFiles!=null&&childFiles.length!=0)
		for (File file1 : childFiles) {
			file1.delete();
		}
    }
    public static void deleteUselessFile(){//删除过期文件
    	File file=new File(FileUtils.getFileImgCaches());
		if (!file.exists()) return;
		//获取系统时间
	File[] childFiles = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if(pathname==null)return false;
				return (DataUtil.DatetoInt()-DataUtil.DatetoInt(pathname))>FILEOUTTIME;
			}
		});
		if(childFiles!=null&&childFiles.length!=0)
		for (File file1 : childFiles) {
			file1.delete();
		}
    }
	/**
	 * 获取文件名
	 */
	private static String getFileName(String savePath, HttpURLConnection conn) {
		String filename = savePath.substring(savePath.lastIndexOf('/') + 1);
		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null)
					break;
				if ("content-disposition".equals(conn.getHeaderFieldKey(i)
						.toLowerCase())) {
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(
							mine.toLowerCase());
					if (m.find())
						return m.group(1);
				}
			}
			filename = UUID.randomUUID() + ".tmp";// 默认取一个文件名
		}
		return filename;
	}
	
	
	/**
	 * 清空文件缓存
	 */
	static List<String> HashIDList=null;
	public static void clearCache(boolean image) {
		File file=new File(FileUtils.getFileCaches());
		if (!file.exists()) return;
		
		 HashIDList=DownCenter.getExistingInstance().getDownTaskListHashID();
		 if(HashIDList==null) return;
		 
		 File[] childFiles = file.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isFile()&&MD5Util.getMD5HashIDByName(pathname, HashIDList);
				}
			});
		if(childFiles!=null&&childFiles.length!=0)
		for (File file1 : childFiles) {
			file1.delete();
		}
		//回收
		HashIDList.clear();
		HashIDList=null;
		
		if(image){
			//清理缓存图片
			deleteFilesByDirectory(new File(fileImgCaches));
		}
	}
	//防止意外导致静态变量被回收，重新生成路径变量
	public static String getAPKDirCaches() {
		if(TextUtils.isEmpty(APKDirCaches))makeAppCacheDir();
		return APKDirCaches;
	}
	public static void setAPKDirCaches(String aPKDirCaches) {
		APKDirCaches = aPKDirCaches;
	}
	public static String getFileCaches() {
		if(TextUtils.isEmpty(fileCaches))makeAppCacheDir();
		return fileCaches;
	}
	public static void setFileCaches(String fileCaches) {
		FileUtils.fileCaches = fileCaches;
	}
	public static String getFileImgCaches() {
		if(TextUtils.isEmpty(fileImgCaches))makeAppCacheDir();
		return fileImgCaches;
	}
	public static void setFileImgCaches(String fileImgCaches) {
		FileUtils.fileImgCaches = fileImgCaches;
	}
	public static String getFileConfDir() {
		if(TextUtils.isEmpty(fileConfDir))makeAppCacheDir();
		return fileConfDir;
	}
	public static void setFileConfDir(String fileConfDir) {
		FileUtils.fileConfDir = fileConfDir;
	}

	
	
}