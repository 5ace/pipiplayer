package cn.pipi.mobile.pipiplayer.local.vlc;

import java.io.File;

import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.ImageUtils;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;

public class BitmapCache {

    public final static String TAG = "PipiPlayer/BitmapCache";
    private final static boolean LOG_ENABLED = false;

    private static BitmapCache mInstance;
    private final LruCache<String, Bitmap> mMemCache;

    public static BitmapCache getInstance() {
        if (mInstance == null)
            mInstance = new BitmapCache();
        return mInstance;
    }

    private BitmapCache() {

        // Get the global context of the application
        Context context = VLCApplication.getAppContext();

        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/5th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 5;

        Log.d(TAG, "LRUCache size sets to " + cacheSize);

        mMemCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };
    }

    public Bitmap getBitmapFromMemCache(String key) {
        final Bitmap b = mMemCache.get(key);
        if (LOG_ENABLED)
            Log.d(TAG, (b == null) ? "Cache miss" : "Cache found");
        if (b != null && b.isRecycled()) {
            /* A recycled bitmap cannot be used again */
            mMemCache.remove(key);
            return null;
        }
        return b;
    }

    public void addBitmapToMemCache(String key, Bitmap bitmap) {
        if (key != null && bitmap != null && getBitmapFromMemCache(key) == null)
            mMemCache.put(key, bitmap);
    }
    public static void addBitmapToCache(String key, Bitmap bitmap) {
    	BitmapCache cache = BitmapCache.getInstance();
        if (key != null && bitmap != null && cache.getBitmapFromMemCache(key) == null)
        	cache.mMemCache.put(key, bitmap);
    }
    private Bitmap getBitmapFromMemCache(int resId) {
        return getBitmapFromMemCache("res:" + resId);
    }

    private void addBitmapToMemCache(int resId, Bitmap bitmap) {
        addBitmapToMemCache("res:" + resId, bitmap);
    }

    public void clear() {
        mMemCache.evictAll();
    }

    public static Bitmap GetFromResource(View v, int resId) {
        BitmapCache cache = BitmapCache.getInstance();
        Bitmap bitmap = cache.getBitmapFromMemCache(resId);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(v.getResources(), resId);
            cache.addBitmapToMemCache(resId, bitmap);
        }
        return bitmap;
    }
    public static Bitmap GetFromCache(View v, String resId) {
        BitmapCache cache = BitmapCache.getInstance();
        Bitmap bitmap = cache.getBitmapFromMemCache(resId);
        if (bitmap == null) {
        	// 加载SD卡中的图片缓存
        	String filename = FileUtils.getFileName(resId);
        	String filepath = FileUtils.getFileImgCaches()+"/"+filename;
        	File file = new File(filepath);
        	if (file!=null&&file.exists()) {//加入缓存
        	//file.setLastModified(System.currentTimeMillis());//最后时间修改
        	bitmap = ImageUtils.getBitmapByPath(file.getPath());
        	cache.addBitmapToMemCache(resId, bitmap);
        }
        }
        return bitmap;
    }
    
}
