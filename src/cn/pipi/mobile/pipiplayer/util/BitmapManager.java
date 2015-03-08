package cn.pipi.mobile.pipiplayer.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.pipi.mobile.pipiplayer.local.vlc.BitmapCache;
import cn.pipi.mobile.pipiplayer.ui.Activity_Comment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

/**
 * 异步线程加载图片工具类 使用说明： BitmapManager bmpManager; bmpManager = new
 * BitmapManager(BitmapFactory.decodeResource(context.getResources(),
 * R.drawable.loading)); bmpManager.loadBitmap(imageURL, imageView);
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-6-25
 */
public class BitmapManager {

	private static ExecutorService pool;
//	private static Map<ImageView, String> imageViews;
	private Bitmap defaultBmp;
    private static int OUTTIME=5;//5秒
    static BitmapManager sInstance=null;
    private static AlphaAnimation alpha=null;
	static {
		pool = Executors.newFixedThreadPool(5); // 固定线程池
	}
	public static BitmapManager getInstance()  {
		synchronized (BitmapManager.class) {
			if (sInstance == null) {
				sInstance = new BitmapManager();
				 alpha=new AlphaAnimation(0, 1);
				 alpha.setDuration(800);
			}
		}
		return sInstance;
	}

	public BitmapManager() {
	}

	public BitmapManager(Bitmap def) {
		this.defaultBmp = def;

	}

	/**
	 * 设置默认图片
	 * 
	 * @param bmp
	 */
	public void setDefaultBmp(Bitmap bmp) {
		defaultBmp = bmp;
	}

	/**
	 * 加载图片
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadBitmap(String url, ImageView imageView) {
		loadBitmap(url, imageView, this.defaultBmp, 0, 0);
	}

	/**
	 * 加载图片-可设置加载失败后显示的默认图片
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp) {
		loadBitmap(url, imageView, defaultBmp, imageView.getWidth(), imageView.getHeight());
	}

	/**
	 * 加载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp,
			int width, int height) {
		Bitmap bitmap =BitmapCache.GetFromCache(imageView, url);
			if(bitmap!=null){
				//imageView.startAnimation(alpha);
				imageView.setImageBitmap(bitmap);
			}else {
				// 线程加载网络图片
				queueJob(url, imageView, width, height);
			}
	}
	

	/**
	 * 从网络中加载图片
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void queueJob(final String url, final ImageView imageView,
			final int width, final int height) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.obj != null) {
					//imageView.startAnimation(alpha);
					imageView.setImageBitmap((Bitmap) msg.obj);
				}
			}
		};

		pool.execute(new Runnable() {
			public void run() {
				Message message = Message.obtain();
				message.obj = downloadBitmap(url, width, height);
				handler.sendMessage(message);
			}
		});
	}

	/**
	 * 下载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param width
	 * @param height
	 */
	private Bitmap downloadBitmap(String url, int width, int height) {
		Bitmap bitmap = null;
		try {
			// http加载图片
			bitmap = getBitmapByUrl(url,width,height);
			// 放入缓存
		//	cache.put(url, new SoftReference<Bitmap>(bitmap));
			if(bitmap!=null){
				BitmapCache.addBitmapToCache(url, bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) {
		Bitmap bitmap = null;
		// 保存文件
		InputStream is=null;
		HttpURLConnection conn = null;
		try {
			URL imageUrl = new URL(url);
			conn = (HttpURLConnection)imageUrl.openConnection();
			conn.setConnectTimeout(10000);
			conn.connect();
			is = conn.getInputStream();
			 if (conn.getResponseCode() == 200) {
				 is = conn.getInputStream();
					if(is!=null)	{
						byte[] data=ImageUtils.readStream(is);  
				        if(data!=null){  
				            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  
				        }  
					}
				}
			return bitmap;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        is=null;
			}
				if(conn != null)
					conn.disconnect();
				conn=null;
		}

		return bitmap;
	}

	private Bitmap getBitmapByUrl(String url,int w,int h) {
		String filename = FileUtils.getFileName(url);
		String savePath=FileUtils.getFileImgCaches()+"/"+filename;
		return ImageUtils.loadBitmapFromWeb(url, savePath,w,h);
	}
	/**
	 * 取消正在下载的任务
	 */
	public synchronized void cancelTask() {
		if(pool != null){
			pool.shutdownNow();
			pool = null;
		}
	}
	public void getVideoThumbnail(final String urlPath, final ImageView imageView) {
		Bitmap bitmap =BitmapCache.GetFromCache(imageView, urlPath);
			if(bitmap!=null){
				imageView.setImageBitmap(bitmap);
			}else {
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.obj != null) {
							imageView.setImageBitmap((Bitmap) msg.obj);
						}
					}
				};
				pool.execute(new Runnable() {
					public void run() {
						Message message = Message.obtain();
						message.obj = ThumbnailUtils.createVideoThumbnail(urlPath,Thumbnails.MICRO_KIND );;
						handler.sendMessage(message);
					}
				});
			}
	}
	
	/**
	 * 获取Vcode图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getVcodeBitmap(String url) {
		Bitmap bitmap = null;
		// 保存文件
		InputStream is=null;
		HttpURLConnection conn = null;
		try {
			URL imageUrl = new URL(url);
			conn = (HttpURLConnection)imageUrl.openConnection();
			conn.setConnectTimeout(10000);
			conn.connect();
			 if (conn.getResponseCode() == 200) {
				 
				 is = conn.getInputStream();
				 String cookieVal = conn.getHeaderField("Set-Cookie");
				 Log.i("TAG999", "cookieVal="+cookieVal);
				 if (cookieVal != null) {
						//存储sessionid�?
					 Activity_Comment.SessionString = cookieVal.substring(0, cookieVal.indexOf(";"));
						Log.i("TAG999", "sid="+Activity_Comment.SessionString);
						}
				 
				 
					if(is!=null)	{
						byte[] data=ImageUtils.readStream(is);  
				        if(data!=null){  
				            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  
				        }  
				        
					}
				}
			return bitmap;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        is=null;
			}
				if(conn != null)
					conn.disconnect();
				conn=null;
		}

		return bitmap;
	}
	
}