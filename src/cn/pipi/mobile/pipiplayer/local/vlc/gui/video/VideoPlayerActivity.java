package cn.pipi.mobile.pipiplayer.local.vlc.gui.video;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Presentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.MovieNumAdapter;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.local.libvlc.EventHandler;
import cn.pipi.mobile.pipiplayer.local.libvlc.IVideoPlayer;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVLC;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcException;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcUtil;
import cn.pipi.mobile.pipiplayer.local.libvlc.Media;
import cn.pipi.mobile.pipiplayer.local.vlc.AudioServiceController;
import cn.pipi.mobile.pipiplayer.local.vlc.MediaDatabase;
import cn.pipi.mobile.pipiplayer.local.vlc.Util;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.local.vlc.WeakHandler;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.CommonDialogs;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.CommonDialogs.MenuType;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.MainLocalActivity;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.PreferencesActivity;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.MD5Util;
import cn.pipi.mobile.pipiplayer.util.SdcardUtil;
import cn.pipi.mobile.pipiplayer.util.UITimer;
import cn.pipi.mobile.pipiplayer.util.UITimer.OnUITimer;

import com.umeng.analytics.MobclickAgent;

@SuppressLint("NewApi")
public class VideoPlayerActivity extends Activity implements IVideoPlayer {
    public final static String TAG = "VLC/VideoPlayerActivity";

    // Internal intent identifier to distinguish between internal launch and
    // external intent.
    public final static String PLAY_FROM_VIDEOGRID = "cn.pipi.mobile.pipiplayer.local.vlc.gui.video.PLAY_FROM_VIDEOGRID";

    private SurfaceView mSurface;
    private SurfaceView mSubtitlesSurface;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceHolder mSubtitlesSurfaceHolder;
    private FrameLayout mSurfaceFrame;
    private MediaRouter mMediaRouter;
    private MediaRouter.SimpleCallback mMediaRouterCallback;
    private SecondaryDisplay mPresentation;
    private LibVLC mLibVLC;
    private String mLocation;

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    /** Overlay */
    private View mOverlayHeader;
    private View mOverlayOption;
    private View mOverlayProgress;
    private static final int OVERLAY_TIMEOUT = 4000;
    private static final int OVERLAY_INFINITE = 3600000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int SURFACE_SIZE = 3;
    private static final int AUDIO_SERVICE_CONNECTION_SUCCESS = 5;
    private static final int AUDIO_SERVICE_CONNECTION_FAILED = 6;
    private static final int FADE_OUT_INFO = 4;
    private boolean mDragging;
    private boolean mShowing;
    private int mUiVisibility = -1;
    private SeekBar mSeekbar;
    private TextView mTitle;
    private TextView mSysTime;
    private TextView mBattery;
    private TextView mTime;
    private TextView mLength;
    private TextView mInfo;
    private ImageButton mPlayPause;
    //private ImageButton mBackward;
    //private ImageButton mForward;
    private boolean mEnableJumpButtons;
    private boolean mEnableBrightnessGesture;
    private boolean mDisplayRemainingTime = false;
    private ImageButton mAudioTrack;
    private ImageButton mSubtitle;
    private ImageButton mLock;
    private ImageButton mSize;
    //private ImageButton mMenu;
    private boolean mIsLocked = false;
    private int mLastAudioTrack = -1;
    private int mLastSpuTrack = -2;

    /**
     * For uninterrupted switching between audio and video mode
     */
    private boolean mSwitchingView;
    private boolean mEndReached;
    private boolean mCanSeek;

    // Playlist
    private int savedIndexPosition = -1;

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    //Volume
    private AudioManager mAudioManager;
    private int mAudioMax;
    private OnAudioFocusChangeListener mAudioFocusListener;

    //Touch Events
    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_VOLUME = 1;
    private static final int TOUCH_BRIGHTNESS = 2;
    private static final int TOUCH_SEEK = 3;
    private int mTouchAction;
    private int mSurfaceYDisplayRange;
    private float mTouchY, mTouchX, mVol;

    // Brightness
    private boolean mIsFirstBrightnessGesture = true;

    // Tracks & Subtitles
    private Map<Integer,String> mAudioTracksList;
    private Map<Integer,String> mSubtitleTracksList;
    /**
     * Used to store a selected subtitle; see onActivityResult.
     * It is possible to have multiple custom subs in one session
     * (just like desktop VLC allows you as well.)
     */
    private ArrayList<String> mSubtitleSelectedFiles = new ArrayList<String>();

    // Whether fallback from HW acceleration to SW decoding was done.
    private boolean mDisabledHardwareAcceleration = false;
    private int mPreviousHardwareAccelerationMode;
    private DownCenter mDownCenterinstance=null;
    private boolean goNextMovie=false;
    private TextView player_speed,player_buffer; 
    private Button layout_selete_movie_num;	
    private LinearLayout bufferlayout;
    private ImageView control_sound,control_progress,control_light;
    private DownLoadInfo downInfo=null;
    private float m=0f;
    private SharedPreferences preferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_mini_player);
        getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (LibVlcUtil.isICSOrLater())
            getWindow().getDecorView().findViewById(android.R.id.content).setOnSystemUiVisibilityChangeListener(
                    new OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if (visibility == mUiVisibility)
                                return;
                            setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
                            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE && !mShowing && !isFinishing()) {
                                showOverlay();
                            }
                            mUiVisibility = visibility;
                        }
                    }
            );

        /** initialize Views an their Events */
        mOverlayHeader = findViewById(R.id.player_overlay_header);
        mOverlayOption = findViewById(R.id.option_overlay);
        mOverlayProgress = findViewById(R.id.progress_overlay);

        /* header */
        mTitle = (TextView) findViewById(R.id.player_overlay_title);
        mSysTime = (TextView) findViewById(R.id.player_overlay_systime);
        mBattery = (TextView) findViewById(R.id.player_overlay_battery);

        // Position and remaining time
        mTime = (TextView) findViewById(R.id.player_overlay_time);
        mTime.setOnClickListener(mRemainingTimeListener);
        mLength = (TextView) findViewById(R.id.player_overlay_length);
        mLength.setOnClickListener(mRemainingTimeListener);

        // the info textView is not on the overlay
        mInfo = (TextView) findViewById(R.id.player_overlay_info);


        mEnableJumpButtons = preferences.getBoolean("enable_jump_buttons", false);
        mPlayPause = (ImageButton) findViewById(R.id.player_overlay_play);
        mPlayPause.setOnClickListener(mPlayPauseListener);

        mLock = (ImageButton) findViewById(R.id.lock_overlay_button);
        mLock.setOnClickListener(mLockListener);
        mSize = (ImageButton) findViewById(R.id.player_overlay_size);
        mSize.setOnClickListener(mSizeListener);

        //mMenu = (ImageButton) findViewById(R.id.player_overlay_adv_function);

        mSurface = (SurfaceView) findViewById(R.id.player_surface);
        mSurfaceHolder = mSurface.getHolder();
        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);
        String chroma = preferences.getString("chroma_format", "");
        Log.i(TAG, "chroma = " +chroma);
        if(LibVlcUtil.isGingerbreadOrLater() && chroma.equals("YV12")) {
            mSurfaceHolder.setFormat(ImageFormat.YV12);
        } else if (chroma.equals("RV16")) {
            mSurfaceHolder.setFormat(PixelFormat.RGB_565);
        } else {
            mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        }
        mSurfaceHolder.addCallback(mSurfaceCallback);

        mSubtitlesSurface = (SurfaceView) findViewById(R.id.subtitles_surface);
        mSubtitlesSurfaceHolder = mSubtitlesSurface.getHolder();
        mSubtitlesSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
        mSubtitlesSurface.setZOrderMediaOverlay(true);
        mSubtitlesSurfaceHolder.addCallback(mSubtitlesSurfaceCallback);

        mSeekbar = (SeekBar) findViewById(R.id.player_overlay_seekbar);
        mSeekbar.setOnSeekBarChangeListener(mSeekListener);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mSwitchingView = false;
        mEndReached = false;

        // Clear the resume time, since it is only used for resumes in external
        // videos.
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(PreferencesActivity.VIDEO_RESUME_TIME, -1);
        // Also clear the subs list, because it is supposed to be per session
        // only (like desktop VLC). We don't want the customs subtitle file
        // to persist forever with this video.
        editor.putString(PreferencesActivity.VIDEO_SUBTITLE_FILES, null);
        editor.commit();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(VLCApplication.SLEEP_INTENT);
        registerReceiver(mReceiver, filter);
        mDownCenterinstance = DownCenter.getExistingInstance();
        try {
            mLibVLC = Util.getLibVlcInstance();
        } catch (LibVlcException e) {
            Log.d(TAG, "LibVLC initialisation failed");
            return;
        }
        /* Only show the subtitles surface when using "Full Acceleration" mode */
        if (mLibVLC.getHardwareAcceleration() == 2)
            mSubtitlesSurface.setVisibility(View.VISIBLE);
        
        Log.i(TAG, "getHardwareAcceleration = " +mLibVLC.getHardwareAcceleration());
        // Signal to LibVLC that the videoPlayerActivity was created, thus the
        // SurfaceView is now available for MediaCodec direct rendering.
        mLibVLC.eventVideoPlayerActivityCreated(true);

        EventHandler em = EventHandler.getInstance();
        em.addHandler(eventHandler);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);


        if (LibVlcUtil.isJellyBeanMR1OrLater()) {
        	 Log.i(TAG, "isJellyBeanMR1OrLater = true" );
            // Get the media router service (miracast)
            mMediaRouter = (MediaRouter)getSystemService(Context.MEDIA_ROUTER_SERVICE);
            mMediaRouterCallback = new MediaRouter.SimpleCallback() {
                @Override
                public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                    Log.d(TAG, "onRouteSelected: type=" + type + ", info=" + info);
                    updatePresentation();
                }

                @Override
                public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                    Log.d(TAG, "onRouteUnselected: type=" + type + ", info=" + info);
                    updatePresentation();
                }

                @Override
                public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info) {
                    Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);
                    updatePresentation();
                }
            };
        }
        player_speed =(TextView) findViewById(R.id.player_speed);
        layout_selete_movie_num=(Button) findViewById(R.id.btn_selete_movie_num);
        layout_selete_movie_num.setOnClickListener(mSelete_movie_numListener);
        player_buffer=(TextView) findViewById(R.id.player_pre_speed);
        bufferlayout=(LinearLayout) findViewById(R.id.bufferlayout);
        
        getFirstGesture(preferences);
        if (mIsFirstBrightnessGesture) initBrightnessTouch();//调整屏幕亮度
      /*  new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Debug.startMethodTracing("pipi");
			}
		}).start();*/
    }

    @SuppressLint("NewApi")
	@Override
    protected void onPause() {
        super.onPause();
       // Debug.stopMethodTracing();
        if(mLocation.indexOf("ppfilm://") > -1)
		{Log.i(TAG,"onPause");
        	mDownCenterinstance.StopPlaytaskRead();
		}
        if (mMediaRouter != null) {
            // Stop listening for changes to media routes.
            mMediaRouter.removeCallback(mMediaRouterCallback);
        }
        if(mSwitchingView) {
            Log.d(TAG, "mLocation = \"" + mLocation + "\"");
            AudioServiceController.getInstance().showWithoutParse(savedIndexPosition);
            AudioServiceController.getInstance().unbindAudioService(this);
            return;
        }

        long time = mLibVLC.getTime();
        long length = mLibVLC.getLength();
        //remove saved position if in the last 5 seconds
        if (length - time < 5000)
            time = 0;
        else
            time -= 5000; // go back 5 seconds, to compensate loading time

        /*
         * Pausing here generates errors because the vout is constantly
         * trying to refresh itself every 80ms while the surface is not
         * accessible anymore.
         * To workaround that, we keep the last known position in the playlist
         * in savedIndexPosition to be able to restore it during onResume().
         */
        mLibVLC.stop();

        mSurface.setKeepScreenOn(false);

        SharedPreferences.Editor editor = preferences.edit();
        // Save position
        if (time >= 0 && mCanSeek) {
            if(MediaDatabase.getInstance().mediaItemExists(mLocation)) {
                MediaDatabase.getInstance().updateMedia(
                        mLocation,
                        MediaDatabase.mediaColumn.MEDIA_TIME,
                        time);
            } else {
                // Video file not in media library, store time just for onResume()
                editor.putLong(PreferencesActivity.VIDEO_RESUME_TIME, time);
            }
            if(downInfo!=null){
            	downInfo.setDownProgress(time);
            	downInfo.setDownTotalSize(length);
            	DBHelperDao.getDBHelperDaoInstace().insertMovieHistroy(downInfo);
            }
        }
        // Save selected subtitles
        String subtitleList_serialized = null;
        if(mSubtitleSelectedFiles.size() > 0) {
            Log.d(TAG, "Saving selected subtitle files");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(mSubtitleSelectedFiles);
                subtitleList_serialized = bos.toString();
            } catch(IOException e) {}
        }
        editor.putString(PreferencesActivity.VIDEO_SUBTITLE_FILES, subtitleList_serialized);

        editor.commit();
        AudioServiceController.getInstance().unbindAudioService(this);
        MobclickAgent.onPause(this);
    }

    @SuppressLint("NewApi")
	@Override
    protected void onStop() {
        super.onStop();
        Log.i("TAG999", "onStop");
        // Dismiss the presentation when the activity is not visible.
        if (mPresentation != null) {
            Log.i(TAG, "Dismissing presentation because the activity is no longer visible.");
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TAG999", "onDestroy");
        unregisterReceiver(mReceiver);
        if (mLibVLC != null && !mSwitchingView) {
        	 if(mLocation.indexOf("ppfilm://") > -1)
     		{
     		mDownCenterinstance.DestroyPlaytak();
     		}	
       	speedTimer.stop();
           mLibVLC.stop();
       }
        EventHandler em = EventHandler.getInstance();
        em.removeHandler(eventHandler);

        // MediaCodec opaque direct rendering should not be used anymore since there is no surface to attach.
        mLibVLC.eventVideoPlayerActivityCreated(false);
        // HW acceleration was temporarily disabled because of an error, restore the previous value.
        if (mDisabledHardwareAcceleration)
            mLibVLC.setHardwareAcceleration(mPreviousHardwareAccelerationMode);

        mAudioManager = null;
        if(goNextMovie){
        	if(MD5Util.getFromHttpfilm(downInfo.getDownUrl())
        			&&DBHelperDao.getDBHelperDaoInstace().getMovieStoreState(downInfo.getDownUrl())!=DownTask.TASK_FINISHED){
        		DataUtil.getToast("该集未下载完，聚合资源下载完才能播放");
        	}else{
        		downInfo.setDownProgress(0);
				start(VideoPlayerActivity.this, downInfo);
            	goNextMovie=false;
            	Log.i("TAG999", "开启下集了");
			}
        }
    }

    @SuppressLint("NewApi")
	@Override
    protected void onResume() {
        super.onResume();
        mSwitchingView = false;
        AudioServiceController.getInstance().bindAudioService(this,
                new AudioServiceController.AudioServiceConnectionListener() {
            @Override
            public void onConnectionSuccess() {
                mHandler.sendEmptyMessage(AUDIO_SERVICE_CONNECTION_SUCCESS);
            }

            @Override
            public void onConnectionFailed() {
                mHandler.sendEmptyMessage(AUDIO_SERVICE_CONNECTION_FAILED);
            }
        });

        if (mMediaRouter != null) {
            // Listen for changes to media routes.
            mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
        }

        MobclickAgent.onResume(this); 
    }

    private void startPlayback() {
        load();

        /*
         * if the activity has been paused by pressing the power button,
         * pressing it again will show the lock screen.
         * But onResume will also be called, even if vlc-android is still in the background.
         * To workaround that, pause playback if the lockscreen is displayed
         */
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLibVLC != null && mLibVLC.isPlaying()) {
                    KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
                    if (km.inKeyguardRestrictedInputMode())
                        mLibVLC.pause();
                }
            }}, 500);

        // Add any selected subtitle file from the file picker
        if(mSubtitleSelectedFiles.size() > 0) {
            for(String file : mSubtitleSelectedFiles) {
                Log.i(TAG, "Adding user-selected subtitle " + file);
                mLibVLC.addSubtitleTrack(file);
            }
        }

        if (LibVlcUtil.isJellyBeanMR1OrLater()) {
            updatePresentation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) return;

        if(data.getDataString() == null) {
            Log.d(TAG, "Subtitle selection dialog was cancelled");
        }
        if(data.getData() == null) return;

        String uri = data.getData().getPath();
        if(requestCode == CommonDialogs.INTENT_SPECIFIC) {
            Log.d(TAG, "Specific subtitle file: " + uri);
        } else if(requestCode == CommonDialogs.INTENT_GENERIC) {
            Log.d(TAG, "Generic subtitle file: " + uri);
        }
        mSubtitleSelectedFiles.add(data.getData().getPath());
    }

    public static void start(Context context, String location) {
        start(context, location, null, -1, false, false);
    }

    public static void start(Context context, String location, Boolean fromStart) {
        start(context, location, null, -1, false, fromStart);
    }

    public static void start(Context context, String location, String title, Boolean dontParse) {
        start(context, location, title, -1, dontParse, false);
    }

    public static void start(Context context, String location, String title, int position, Boolean dontParse) {
        start(context, location, title, position, dontParse, false);
    }

    public static void start(Context context, String location, String title, int position, Boolean dontParse, Boolean fromStart) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.setAction(VideoPlayerActivity.PLAY_FROM_VIDEOGRID);
        intent.putExtra("itemLocation", location);
        intent.putExtra("itemTitle", title);
        intent.putExtra("dontParse", dontParse);
        intent.putExtra("fromStart", fromStart);
        intent.putExtra("itemPosition", position);

        if (dontParse)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        context.startActivity(intent);
    }
    public static void start(Context context, DownLoadInfo downInfo) {//增加是否来自影片详情页面
    	Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.setAction(VideoPlayerActivity.PLAY_FROM_VIDEOGRID);
        intent.putExtra("downInfo", downInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
        MobclickAgent.onEvent(context, "PlayVideo", downInfo.getDownID());
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
                int batteryLevel = intent.getIntExtra("level", 0);
                /* if (batteryLevel >= 50)
                mBattery.setTextColor(Color.GREEN);
            else if (batteryLevel >= 30)
                mBattery.setTextColor(Color.YELLOW);
            else
                mBattery.setTextColor(Color.RED);*/
            if (batteryLevel >= 90)
                mBattery.setBackgroundResource(R.drawable.dianchi_5);
            else if (batteryLevel >= 70)
            	mBattery.setBackgroundResource(R.drawable.dianchi_4);
            else if (batteryLevel >= 50)
            	mBattery.setBackgroundResource(R.drawable.dianchi_3);
            else if (batteryLevel >= 30)
            	mBattery.setBackgroundResource(R.drawable.dianchi_2);
            else if (batteryLevel >= 10)
            	mBattery.setBackgroundResource(R.drawable.dianchi_1);
            else
                mBattery.setBackgroundResource(R.drawable.dianchi_0);
            //if(isAdded())
            //mBattery.setText(getString(R.string.battery)+String.format("%d%%", batteryLevel));
        }

            else if (action.equalsIgnoreCase(VLCApplication.SLEEP_INTENT)) {
                finish();
            }
        }
    };

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        showOverlay();
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        if (width * height == 0)
            return;

        // store video size
        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth  = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;
        Message msg = mHandler.obtainMessage(SURFACE_SIZE);
        mHandler.sendMessage(msg);
    }

    /**
     * Lock screen rotation
     */
    private void lockScreen() {
        showInfo(R.string.locked, 1000);
        mLock.setBackgroundResource(R.drawable.ic_lock_glow);
        mTime.setEnabled(false);
        mSeekbar.setEnabled(false);
        mLength.setEnabled(false);
        hideOverlay(true);
    }

    /**
     * Remove screen lock
     */
    private void unlockScreen() {
        showInfo(R.string.unlocked, 1000);
        mLock.setBackgroundResource(R.drawable.ic_lock);
        mTime.setEnabled(true);
        mSeekbar.setEnabled(true);
        mLength.setEnabled(true);
        //mMenu.setEnabled(true);
        mShowing = false;
        showOverlay();
    }

    /**
     * Show text in the info view for "duration" milliseconds
     * @param text
     * @param duration
     */
    private void showInfo(String text, int duration) {
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    private void showInfo(int textid, int duration) {
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(textid);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    /**
     * Show text in the info view
     * @param text
     */
    private void showInfo(String text) {
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);
        mHandler.removeMessages(FADE_OUT_INFO);
    }

    /**
     * hide the info view with "delay" milliseconds delay
     * @param delay
     */
    private void hideInfo(int delay) {
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, delay);
    }

    /**
     * hide the info view
     */
    private void hideInfo() {
        hideInfo(0);
    }

    private void fadeOutInfo() {
        if (mInfo.getVisibility() == View.VISIBLE)
            mInfo.startAnimation(AnimationUtils.loadAnimation(
                    VideoPlayerActivity.this, android.R.anim.fade_out));
        mInfo.setVisibility(View.INVISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void changeAudioFocus(boolean gain) {
        if(!LibVlcUtil.isFroyoOrLater()) // NOP if not supported
            return;

        if (mAudioFocusListener == null) {
            mAudioFocusListener = new OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    /*
                     * Pause playback during alerts and notifications
                     */
                    switch (focusChange)
                    {
                        case AudioManager.AUDIOFOCUS_LOSS:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            if (mLibVLC.isPlaying())
                                mLibVLC.pause();
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                            if (!mLibVLC.isPlaying())
                                mLibVLC.play();
                            break;
                    }
                }
            };
        }

        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        if(gain)
            am.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        else
            am.abandonAudioFocus(mAudioFocusListener);
    }

    /**
     *  Handle libvlc asynchronous events
     */
    private final Handler eventHandler = new VideoPlayerEventHandler(this);

    private static class VideoPlayerEventHandler extends WeakHandler<VideoPlayerActivity> {
        public VideoPlayerEventHandler(VideoPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayerActivity activity = getOwner();
            if(activity == null) return;
            // Do not handle events if we are leaving the VideoPlayerActivity
            if (activity.mSwitchingView) return;

            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaParsedChanged:
                    Log.i(TAG, "MediaParsedChanged");
                    if (activity.mLibVLC.getVideoTracksCount() < 1) {
                        Log.i(TAG, "No video track, open in audio mode");
                        activity.switchToAudioMode();
                    }
                    break;
                case EventHandler.MediaPlayerBuffering:
                    Bundle b= msg.getData();
                    float progress=b.getFloat("data");
                    activity.getBuffer(progress);
                    break; 
                case EventHandler.MediaPlayerPlaying:
                    Log.i(TAG, "MediaPlayerPlaying");
                    activity.showOverlay();
                    activity.setESTracks();
                    activity.changeAudioFocus(true);
                    break;
                case EventHandler.MediaPlayerPaused:
                    Log.i(TAG, "MediaPlayerPaused");
                    break;
                case EventHandler.MediaPlayerStopped:
                    Log.i(TAG, "MediaPlayerStopped");
                    activity.changeAudioFocus(false);
                    break;
                case EventHandler.MediaPlayerEndReached:
                    Log.i(TAG, "MediaPlayerEndReached");
                    activity.changeAudioFocus(false);
                    activity.endReached();
                    break;
                case EventHandler.MediaPlayerVout:
                    activity.handleVout(msg);
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    if (!activity.mCanSeek)
                        activity.mCanSeek = true;
                    //don't spam the logs
                    break;
                case EventHandler.MediaPlayerEncounteredError:
                    Log.i(TAG, "MediaPlayerEncounteredError");
                    activity.encounteredError();
                    break;
                case EventHandler.HardwareAccelerationError:
                    Log.i(TAG, "HardwareAccelerationError");
                    activity.handleHardwareAccelerationError();
                    break;
                case EventHandler.MEDIA_PLAYER_BUFFERING_SPEED_UPDATE: {//速度更新
                	activity.getSpeed();
					break;
				}
                default:
                    Log.e(TAG, String.format("Event not handled (0x%x)", msg.getData().getInt("event")));
                    break;
            }
            activity.updateOverlayPausePlay();
        }
    };

    /**
     * Handle resize of the surface and the overlay
     */
    private final Handler mHandler = new VideoPlayerHandler(this);

    private static class VideoPlayerHandler extends WeakHandler<VideoPlayerActivity> {
        public VideoPlayerHandler(VideoPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayerActivity activity = getOwner();
            if(activity == null) // WeakReference could be GC'ed early
                return;

            switch (msg.what) {
                case FADE_OUT:
                    activity.hideOverlay(false);
                    break;
                case SHOW_PROGRESS:
                    int pos = activity.setOverlayProgress();
                    if (activity.canShowProgress()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
                case SURFACE_SIZE:
                    activity.changeSurfaceSize();
                    break;
                case FADE_OUT_INFO:
                    activity.fadeOutInfo();
                    break;
                case AUDIO_SERVICE_CONNECTION_SUCCESS:
                    activity.startPlayback();
                    break;
                case AUDIO_SERVICE_CONNECTION_FAILED:
                    activity.finish();
                    break;
            }
        }
    };

    private boolean canShowProgress() {
        return !mDragging && mShowing && mLibVLC.isPlaying();
    }

    private void endReached() {
        if(mLibVLC.getMediaList().expandMedia(savedIndexPosition) == 0) {
            Log.d(TAG, "Found a video playlist, expanding it");
            eventHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    load();
                }
            }, 1000);
        } else {
            /* Exit player when reaching the end */
            mEndReached = true;
            if(downInfo!=null){
       		 int i=downInfo.getDownPosition();
       		 if(downInfo.getPlayList()!=null
       				 &&downInfo.getDownPosition()<downInfo.getPlayList().size()-1){
       			 downInfo.setDownPosition(i+1);
       			 downInfo.setDownUrl(downInfo.getPlayList().get(i+1));
       			 goNextMovie=true;
       			Log.i("TAG999", "mEndReached goNextMovie="+goNextMovie);
       		 }
       	 }
            finish();
        }
    }

    private void encounteredError() {
        /* Encountered Error, exit player with a message */
        AlertDialog dialog = new AlertDialog.Builder(VideoPlayerActivity.this)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        })
        .setTitle(R.string.encountered_error_title)
        .setMessage(R.string.encountered_error_message)
        .create();
        dialog.show();
    }

    public void eventHardwareAccelerationError() {
        EventHandler em = EventHandler.getInstance();
        em.callback(EventHandler.HardwareAccelerationError, new Bundle());
    }

    private void handleHardwareAccelerationError() {
        mLibVLC.stop();
        AlertDialog dialog = new AlertDialog.Builder(VideoPlayerActivity.this)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mDisabledHardwareAcceleration = true;
                mPreviousHardwareAccelerationMode = mLibVLC.getHardwareAcceleration();
                mLibVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);
                mSubtitlesSurface.setVisibility(View.INVISIBLE);
                load();
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        })
        .setTitle(R.string.hardware_acceleration_error_title)
        .setMessage(R.string.hardware_acceleration_error_message)
        .create();
        dialog.show();
    }

    private void handleVout(Message msg) {
        if (msg.getData().getInt("data") == 0 && !mEndReached) {
            /* Video track lost, open in audio mode */
            Log.i(TAG, "Video track lost, switching to audio");
            mSwitchingView = true;
            finish();
        }
    }

    private void switchToAudioMode() {
        mSwitchingView = true;
        // Show the MainActivity if it is not in background.
        if (getIntent().getAction() != null
            && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Intent i = new Intent(this, MainLocalActivity.class);
            startActivity(i);
        }
        finish();
    }

    @SuppressLint("NewApi")
	private void changeSurfaceSize() {
        int sw;
        int sh;

        // get screen size
        if (mPresentation == null) {
            sw = getWindow().getDecorView().getWidth();
            sh = getWindow().getDecorView().getHeight();
        } else {
            sw = mPresentation.getWindow().getDecorView().getWidth();
            sh = mPresentation.getWindow().getDecorView().getHeight();
        }

        double dw = sw, dh = sh;
        boolean isPortrait;

        if (mPresentation == null) {
            // getWindow().getDecorView() doesn't always take orientation into account, we have to correct the values
            isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        } else {
            isPortrait = false;
        }

        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }

        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }

        // compute the aspect ratio
        double ar, vw;
        double density = (double)mSarNum / (double)mSarDen;
        if (density == 1.0) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double)mVideoVisibleWidth / (double)mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * density;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_FIT_HORIZONTAL:
                dh = dw / ar;
                break;
            case SURFACE_FIT_VERTICAL:
                dw = dh * ar;
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = vw;
                break;
        }

        SurfaceView surface;
        SurfaceView subtitlesSurface;
        SurfaceHolder surfaceHolder;
        SurfaceHolder subtitlesSurfaceHolder;
        FrameLayout surfaceFrame;

        if (mPresentation == null) {
            surface = mSurface;
            subtitlesSurface = mSubtitlesSurface;
            surfaceHolder = mSurfaceHolder;
            subtitlesSurfaceHolder = mSubtitlesSurfaceHolder;
            surfaceFrame = mSurfaceFrame;
        } else {
            surface = mPresentation.mSurface;
            subtitlesSurface = mPresentation.mSubtitlesSurface;
            surfaceHolder = mPresentation.mSurfaceHolder;
            subtitlesSurfaceHolder = mPresentation.mSubtitlesSurfaceHolder;
            surfaceFrame = mPresentation.mSurfaceFrame;
        }

        // force surface buffer size
        surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
        subtitlesSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        LayoutParams lp = surface.getLayoutParams();
        lp.width  = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        surface.setLayoutParams(lp);
        subtitlesSurface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = surfaceFrame.getLayoutParams();
        lp.width = (int) Math.floor(dw);
        lp.height = (int) Math.floor(dh);
        surfaceFrame.setLayoutParams(lp);

        surface.invalidate();
        subtitlesSurface.invalidate();
    }

    /**
     * show/hide the overlay
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
        if (mIsLocked) {
        	Log.i("TAG999", "mIsLocked="+mIsLocked);
            // locked, only handle show/hide & ignore all actions
            if (event.getAction() == MotionEvent.ACTION_UP) {
            	Log.i("TAG999", "ACTION_UP="+mShowing);
                if (!mShowing) {
                    showOverlay();
                } else {
                    hideOverlay(true);
                }
            }
            return true;
        }

        DisplayMetrics screen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screen);
        if (mSurfaceYDisplayRange == 0)
            mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);

        float y_changed = event.getRawY() - mTouchY;
        float x_changed = event.getRawX() - mTouchX;

        // coef is the gradient's move to determine a neutral zone
        float coef = Math.abs (y_changed / x_changed);
        float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);

        switch (event.getAction()) {

        case MotionEvent.ACTION_DOWN:
            // Audio
            mTouchY = event.getRawY();
            mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mTouchAction = TOUCH_NONE;
            // Seek
            mTouchX = event.getRawX();
            break;

        case MotionEvent.ACTION_MOVE:
            if (coef > 2) {
                if (mTouchX > (screen.widthPixels / 2)){
                    doVolumeTouch(y_changed);
                }else {
                    doBrightnessTouch(y_changed);
                }
                if(Util.hasNavBar())
                    showOverlay();
            }
            // Seek (Right or Left move)
            doSeekTouch(coef, xgesturesize, false);
            break;

        case MotionEvent.ACTION_UP:
            // Audio or Brightness
            if ( mTouchAction == TOUCH_NONE) {
                if (!mShowing) {
                    showOverlay();
                } else {
                    hideOverlay(true);
                }
            }
            // Seek
            doSeekTouch(coef, xgesturesize, true);
            break;
        }
       // return mTouchAction != TOUCH_NONE;
        return true;
	}

    private void doSeekTouch(float coef, float gesturesize, boolean seek) {
        // No seek action if coef > 0.5 and gesturesize < 1cm
        if (coef > 0.5 || Math.abs(gesturesize) < 1 || !mCanSeek)
            return;

        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_SEEK)
            return;
        mTouchAction = TOUCH_SEEK;

        // Always show seekbar when searching
        if (!mShowing) showOverlay();

        long length = mLibVLC.getLength();
        long time = mLibVLC.getTime();

        // Size of the jump, 10 minutes max (600000), with a bi-cubic progression, for a 8cm gesture
        int jump = (int) (Math.signum(gesturesize) * ((600000 * Math.pow((gesturesize / 8), 4)) + 3000));

        // Adjust the jump
        if ((jump > 0) && ((time + jump) > length))
            jump = (int) (length - time);
        if ((jump < 0) && ((time + jump) < 0))
            jump = (int) -time;

        //Jump !
        if (seek && length > 0)
            mLibVLC.setTime(time + jump);

        if (length > 0)
            //Show the jump's size
            showInfo(String.format("%s%s (%s)",
                    jump >= 0 ? "+" : "",
                    Util.millisToString(jump),
                    Util.millisToString(time + jump)), 1000);
        else
            showInfo(R.string.unseekable_stream, 1000);
    }

    private void doVolumeTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_VOLUME)
            return;
        int delta = -(int) ((y_changed / mSurfaceYDisplayRange) * mAudioMax);
        int vol = (int) Math.min(Math.max(mVol + delta, 0), mAudioMax);
        if (delta != 0) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
            mTouchAction = TOUCH_VOLUME;
            showInfo(getString(R.string.volume) + '\u00A0' + Integer.toString(vol),1000);
        }
    }

    private void initBrightnessTouch() {
        float brightnesstemp = preferences.getFloat("screenBrightness", 0);
        // Initialize the layoutParams screen brightness
        if(brightnesstemp==0){//第一次调整亮度，用系统亮度
        	try {
                brightnesstemp = android.provider.Settings.System.getInt(getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
            } catch (SettingNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightnesstemp;
        getWindow().setAttributes(lp);
        mIsFirstBrightnessGesture = false;
    }

    private void doBrightnessTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_BRIGHTNESS)
            return;
        mTouchAction = TOUCH_BRIGHTNESS;

        // Set delta : 0.07f is arbitrary for now, it possibly will change in the future
        float delta = - y_changed / mSurfaceYDisplayRange * 0.2f;

        // Estimate and adjust Brightness
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =  Math.min(Math.max(lp.screenBrightness + delta, 0.01f), 1);
        preferences.edit().putFloat("screenBrightness", lp.screenBrightness).commit();//保存亮度
        // Set Brightness
        getWindow().setAttributes(lp);
        showInfo(getString(R.string.brightness) + '\u00A0' + Math.round(lp.screenBrightness*15),1000);
    }

    /**
     * handle changes of the seekbar (slicer)
     */
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            showOverlay(OVERLAY_INFINITE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            showOverlay();
            hideInfo();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && mCanSeek) {
                mLibVLC.setTime(progress);
                setOverlayProgress();
                mTime.setText(Util.millisToString(progress));
                showInfo(Util.millisToString(progress));
            }

        }
    };


    /**
    *
    */
    private final OnClickListener mPlayPauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mLibVLC.isPlaying())
                pause();
            else
                play();
            showOverlay();
        }
    };

    /**
    *
    */
    private final OnClickListener mBackwardListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            seek(-10000);
        }
    };

    /**
    *
    */
    private final OnClickListener mSelete_movie_numListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	createMovieNumDialog();
        }
    };
    
    public void seek(int delta) {
        // unseekable stream
        if(mLibVLC.getLength() <= 0 || !mCanSeek) return;

        long position = mLibVLC.getTime() + delta;
        if (position < 0) position = 0;
        mLibVLC.setTime(position);
        showOverlay();
    }

    /**
    *
    */
   private final OnClickListener mLockListener = new OnClickListener() {

       @Override
       public void onClick(View v) {
           if (!mIsLocked) {
          	 mIsLocked = true;
               lockScreen();
          }else{
          	 mIsLocked = false;
               unlockScreen();
          }
      }
   };

    /**
     *
     */
    private final OnClickListener mSizeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (mCurrentSize < SURFACE_ORIGINAL) {
                mCurrentSize++;
            } else {
                mCurrentSize = 0;
            }
            changeSurfaceSize();
            switch (mCurrentSize) {
                case SURFACE_BEST_FIT:
                    showInfo(R.string.surface_best_fit, 1000);
                    break;
                case SURFACE_FIT_HORIZONTAL:
                    showInfo(R.string.surface_fit_horizontal, 1000);
                    break;
                case SURFACE_FIT_VERTICAL:
                    showInfo(R.string.surface_fit_vertical, 1000);
                    break;
                case SURFACE_FILL:
                    showInfo(R.string.surface_fill, 1000);
                    break;
                case SURFACE_16_9:
                    showInfo("16:9", 1000);
                    break;
                case SURFACE_4_3:
                    showInfo("4:3", 1000);
                    break;
                case SURFACE_ORIGINAL:
                    showInfo(R.string.surface_original, 1000);
                    break;
            }
            showOverlay();
        }
    };

    private final OnClickListener mRemainingTimeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mDisplayRemainingTime = !mDisplayRemainingTime;
            showOverlay();
        }
    };

    /**
     * attach and disattach surface to the lib
     */
    private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(format == PixelFormat.RGBX_8888)
                Log.d(TAG, "Pixel format is RGBX_8888");
            else if(format == PixelFormat.RGB_565)
                Log.d(TAG, "Pixel format is RGB_565");
            else if(format == ImageFormat.YV12)
                Log.d(TAG, "Pixel format is YV12");
            else
                Log.d(TAG, "Pixel format is other/unknown");
            mLibVLC.attachSurface(holder.getSurface(), VideoPlayerActivity.this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mLibVLC.detachSurface();
        }
    };

    private final SurfaceHolder.Callback mSubtitlesSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mLibVLC.attachSubtitlesSurface(holder.getSurface());
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mLibVLC.detachSubtitlesSurface();
        }
    };

    /**
     * show overlay the the default timeout
     */
    private void showOverlay() {
        if (mPresentation == null)
            showOverlay(OVERLAY_TIMEOUT);
        else
            showOverlay(OVERLAY_INFINITE); // Hack until we have fullscreen controls
    }

    /**
     * show overlay
     */
    private void showOverlay(int timeout) {
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (!mShowing) {
        	Log.i("TAG999", "mShowing");
            mShowing = true;
          dimStatusBar(false);
            if(!mIsLocked){
            	 if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                 	mOverlayHeader.setVisibility(View.VISIBLE);
                 }else{
                 	mOverlayHeader.setVisibility(View.INVISIBLE);
                 }
                 mOverlayProgress.setVisibility(View.VISIBLE);
            }
            mLock.setVisibility(View.VISIBLE);
        }
        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
        updateOverlayPausePlay();
    }


    /**
     * hider overlay
     */
    private void hideOverlay(boolean fromUser) {
        if (mPresentation != null)
            return; // Hack until we have fullscreen controls

        if (mShowing) {
                mHandler.removeMessages(SHOW_PROGRESS);
                mOverlayProgress.setVisibility(View.INVISIBLE);
                mOverlayHeader.setVisibility(View.INVISIBLE);
            mShowing = false;
            mLock.setVisibility(View.INVISIBLE);
            dimStatusBar(true);
        }
    }

    /**
     * Dim the status bar and/or navigation icons when needed on Android 3.x.
     * Hide it on Android 4.0 and later
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void dimStatusBar(boolean dim) {
        if (!LibVlcUtil.isHoneycombOrLater() || !Util.hasNavBar())
            return;
        int layout = 0;
        if (!Util.hasCombBar() && LibVlcUtil.isJellyBeanOrLater())
            layout = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        int visibility =  (dim ? (Util.hasCombBar()
                ? View.SYSTEM_UI_FLAG_LOW_PROFILE
                        : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                        : View.SYSTEM_UI_FLAG_VISIBLE) | layout;
        mSurface.setSystemUiVisibility(visibility);
        mSubtitlesSurface.setSystemUiVisibility(visibility);
    }

    private void updateOverlayPausePlay() {
        if (mLibVLC == null) {
            return;
        }

        mPlayPause.setBackgroundResource(mLibVLC.isPlaying()
                ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    /**
     * update the overlay
     */
    private int setOverlayProgress() {
        if (mLibVLC == null) {
            return 0;
        }
        int time = (int) mLibVLC.getTime();
        int length = (int) mLibVLC.getLength();
        if (length == 0) {
            Media media = MediaDatabase.getInstance().getMedia(mLocation);
            if (media != null)
                length = (int) media.getLength();
        }

        // Update all view elements
        boolean isSeekable = mEnableJumpButtons && length > 0;
        //mBackward.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
        //mForward.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
        mSeekbar.setMax(length);
        mSeekbar.setProgress(time);
        mSysTime.setText(DateFormat.getTimeFormat(this).format(new Date(System.currentTimeMillis())));
        if (time >= 0) mTime.setText(Util.millisToString(time));
        if (length >= 0) mLength.setText(mDisplayRemainingTime && length > 0
                ? "- " + Util.millisToString(length - time)
                : Util.millisToString(length));

        return time;
    }

    private void setESTracks() {
        if (mLastAudioTrack >= 0) {
            mLibVLC.setAudioTrack(mLastAudioTrack);
            mLastAudioTrack = -1;
        }
        if (mLastSpuTrack >= -1) {
            mLibVLC.setSpuTrack(mLastSpuTrack);
            mLastSpuTrack = -2;
        }
    }


    /**
     *
     */
    private void play() {
        mLibVLC.play();
        mSurface.setKeepScreenOn(true);
    }

    /**
     *
     */
    private void pause() {
        mLibVLC.pause();
        mSurface.setKeepScreenOn(false);
    }

    /**
     * External extras:
     * - position (long) - position of the video to start with (in ms)
     */
    private void load() {
        mLocation = null;
        String title = getResources().getString(R.string.title);
        boolean dontParse = false;
        boolean fromStart = false;
        String itemTitle = null;
        int itemPosition = -1; // Index in the media list as passed by AudioServer (used only for vout transition internally)
        long intentPosition = -1; // position passed in by intent (ms)

        if (getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            /* Started from external application */
            if (getIntent().getData() != null
                    && getIntent().getData().getScheme() != null
                    && getIntent().getData().getScheme().equals("content")) {
                if(getIntent().getData().getHost().equals("media") || getIntent().getData().getHost().equals("mms")) {
                    // Media URI
                    Cursor cursor = managedQuery(getIntent().getData(), new String[]{ MediaStore.Video.Media.DATA }, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    if (cursor.moveToFirst())
                        mLocation = LibVLC.PathToURI(cursor.getString(column_index));
                } else if(getIntent().getData().getHost().equals("com.fsck.k9.attachmentprovider")
                       || getIntent().getData().getHost().equals("gmail-ls")) {
                    // Mail-based apps - download the stream to a temporary file and play it
                    try {
                        Cursor cursor = getContentResolver().query(getIntent().getData(), new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        cursor.moveToFirst();
                        String filename = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                        Log.i(TAG, "Getting file " + filename + " from content:// URI");
                        InputStream is = getContentResolver().openInputStream(getIntent().getData());
                        OutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Download/" + filename);
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while((bytesRead = is.read(buffer)) >= 0) {
                            os.write(buffer, 0, bytesRead);
                        }
                        os.close();
                        is.close();
                        mLocation = LibVLC.PathToURI(Environment.getExternalStorageDirectory().getPath() + "/Download/" + filename);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't download file from mail URI");
                        encounteredError();
                    }
                } else {
                    // other content-based URI (probably file pickers)
                    mLocation = getIntent().getData().getPath();
                }
            } else {
                // Plain URI
                mLocation = getIntent().getDataString();
                // Remove VLC prefix if needed
                if (mLocation.startsWith("vlc://")) {
                    mLocation = mLocation.substring(6);
                }
                // Decode URI
                if (!mLocation.contains("/")){
                    try {
                        mLocation = URLDecoder.decode(mLocation,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(getIntent().getExtras() != null)
                intentPosition = getIntent().getExtras().getLong("position", -1);
        } else if(getIntent().getAction() != null
                && getIntent().getAction().equals(PLAY_FROM_VIDEOGRID) && getIntent().getExtras() != null) {
            /* Started from VideoListActivity */
            downInfo=(DownLoadInfo) getIntent().getSerializableExtra("downInfo");
            if(downInfo!=null){
            	if(MD5Util.getFromHttpfilm(downInfo.getDownUrl())){
            		mLocation = "file://"+DBHelperDao.getDBHelperDaoInstace().getLocalByUrl(downInfo.getDownUrl());
            	}else{
            		mLocation = downInfo.getDownUrl();
            	}
                itemTitle = downInfo.getDownName();
                dontParse = false;
                fromStart = PipiPlayerConstant.getInstance().playfromhistroy;
                itemPosition = downInfo.getDownPosition();
                if(downInfo.getPlayList()!=null&&downInfo.getPlayList().size()>1){
                	itemTitle = itemTitle+"."+(itemPosition+1);
                	 layout_selete_movie_num.setVisibility(0);
                }
                intentPosition=DBHelperDao.getDBHelperDaoInstace().getHistroyProgressByID(downInfo.getDownID(),downInfo.getDownPosition());
             }else{
        	 mLocation = getIntent().getExtras().getString("itemLocation");
             itemTitle = getIntent().getExtras().getString("itemTitle");
             dontParse = getIntent().getExtras().getBoolean("dontParse");
             fromStart = getIntent().getExtras().getBoolean("fromStart");
             itemPosition = getIntent().getExtras().getInt("itemPosition", -1);
            }
        }
        Log.i(TAG, "mLocation="+mLocation);
        mSurface.setKeepScreenOn(true);
        if(mLocation.indexOf("ppfilm://") > -1){
        	mLocation = mDownCenterinstance.startPlayTask2(mLocation);
        	Log.i(TAG, "startPlayTask2==="+mLocation);
        	if(!mLocation.startsWith("file:"))
        		speedTimer.start();//标识还是播放本地文件
        }else{
        	player_speed.setVisibility(0);
        	player_speed.setText(getString(R.string.locale_movie))  ; 	
        	}
        /* Start / resume playback */
        if(dontParse && itemPosition >= 0) {
            // Provided externally from AudioService
            Log.d(TAG, "Continuing playback from AudioService at index " + itemPosition);
            savedIndexPosition = itemPosition;
            if(!mLibVLC.isPlaying()) {
                // AudioService-transitioned playback for item after sleep and resume
                mLibVLC.playIndex(savedIndexPosition);
                dontParse = false;
            }
        } else if (savedIndexPosition > -1) {
            AudioServiceController.getInstance().stop(); // Stop the previous playback.
            mLibVLC.setMediaList();
            mLibVLC.playIndex(savedIndexPosition);
        } else if (mLocation != null && mLocation.length() > 0 && !dontParse) {
            AudioServiceController.getInstance().stop(); // Stop the previous playback.
            mLibVLC.setMediaList();
            mLibVLC.getMediaList().add(new Media(mLibVLC, mLocation));
            savedIndexPosition = mLibVLC.getMediaList().size() - 1;
            mLibVLC.playIndex(savedIndexPosition);
        }
        mCanSeek = false;

        if (mLocation != null && mLocation.length() > 0 && !dontParse) {
            // restore last position
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Media media = MediaDatabase.getInstance().getMedia(mLocation);
            if(media != null) {
                // in media library
                if(media.getTime() > 0 && !fromStart)
                    mLibVLC.setTime(media.getTime());

                mLastAudioTrack = media.getAudioTrack();
                mLastSpuTrack = media.getSpuTrack();
            } else {
                // not in media library
                long rTime = preferences.getLong(PreferencesActivity.VIDEO_RESUME_TIME, -1);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(PreferencesActivity.VIDEO_RESUME_TIME, -1);
                editor.commit();
                if(rTime > 0)
                    mLibVLC.setTime(rTime);

                if(intentPosition > 0)
                    mLibVLC.setTime(intentPosition);
            }

            String subtitleList_serialized = preferences.getString(PreferencesActivity.VIDEO_SUBTITLE_FILES, null);
            ArrayList<String> prefsList = new ArrayList<String>();
            if(subtitleList_serialized != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(subtitleList_serialized.getBytes());
                try {
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    prefsList = (ArrayList<String>)ois.readObject();
                } catch(ClassNotFoundException e) {}
                  catch (StreamCorruptedException e) {}
                  catch (IOException e) {}
            }
            for(String x : prefsList){
                if(!mSubtitleSelectedFiles.contains(x))
                    mSubtitleSelectedFiles.add(x);
             }

            try {
            	if(itemTitle != null) {
                    title = itemTitle;
                }else{
                	title = URLDecoder.decode(mLocation, "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
            } catch (IllegalArgumentException e) {
            }
            if (title.startsWith("file:")) {
                title = new File(title).getName();
                int dotIndex = title.lastIndexOf('.');
                if (dotIndex != -1)
                    title = title.substring(0, dotIndex);
            }
        } else if(itemTitle != null) {
            title = itemTitle;
        }
        mTitle.setText(title);
    }

    @SuppressWarnings("deprecation")
    private int getScreenRotation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO /* Android 2.2 has getRotation */) {
            try {
                Method m = display.getClass().getDeclaredMethod("getRotation");
                return (Integer) m.invoke(display);
            } catch (Exception e) {
                return Surface.ROTATION_0;
            }
        } else {
            return display.getOrientation();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private int getScreenOrientation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rot = getScreenRotation();
        /*
         * Since getRotation() returns the screen's "natural" orientation,
         * which is not guaranteed to be SCREEN_ORIENTATION_PORTRAIT,
         * we have to invert the SCREEN_ORIENTATION value if it is "naturally"
         * landscape.
         */
        @SuppressWarnings("deprecation")
        boolean defaultWide = display.getWidth() > display.getHeight();
        if(rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270)
            defaultWide = !defaultWide;
        if(defaultWide) {
            switch (rot) {
            case Surface.ROTATION_0:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case Surface.ROTATION_180:
                // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            case Surface.ROTATION_270:
                // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            default:
                return 0;
            }
        } else {
            switch (rot) {
            case Surface.ROTATION_0:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_180:
                // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            case Surface.ROTATION_270:
                // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            default:
                return 0;
            }
        }
    }

    public void showAdvancedOptions(View v) {
        CommonDialogs.advancedOptions(this, v, MenuType.Video);
    }
    //缓冲速度显示
   	UITimer speedTimer = new UITimer(1000,new OnUITimer()
       {
           public void onTimer() {
           	getSpeed();
           }
       });

   	private void getSpeed(){
    	if(mDownCenterinstance.getPlayTaskPercent()!=100){
    		String speed=SdcardUtil.formatSize(VLCApplication.getAppContext(),mDownCenterinstance.getPlayTaskSpeed());
    		player_speed.setText(speed+"/s");
    	}else{
    		player_speed.setText(VLCApplication.getAppContext().getString(R.string.locale_movie));
    	}
	}
	
	private void getBuffer(float progress){
		if(progress<100){
			if(bufferlayout.getVisibility()!=View.VISIBLE)
				bufferlayout.setVisibility(View.VISIBLE);
			StringBuffer speed=new StringBuffer(getString(R.string.buffer_loading));
			speed.append(progress);
			speed.append("%");
			player_buffer.setText(speed)  ;
		}else{
			if(bufferlayout.getVisibility()==View.VISIBLE)
				bufferlayout.setVisibility(View.GONE);
		}
	}
	

      Dialog dialog;
      private void createMovieNumDialog(){//影片集数选择�?    		  
    	  View layout = LayoutInflater.from(VideoPlayerActivity.this).inflate( R.layout.dialog_gridview, null);
    		   GridView  gv = (GridView) layout.findViewById(R.id.gridView1);
    	      MovieNumAdapter adapter=new MovieNumAdapter(downInfo.getPlayList());
    	      adapter.setPositinID(downInfo.getDownPosition());
    	      gv.setAdapter(adapter);
    	      gv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if(downInfo!=null&&downInfo.getPlayList().size()>arg2&&arg2!=downInfo.getDownPosition()){
						downInfo.setDownPosition(arg2);
						downInfo.setDownUrl(downInfo.getPlayList().get(arg2));
						goNextMovie=true;
						Log.i("TAG999", "onItemClick goNextMovie="+goNextMovie);
					}
					dialog.dismiss();
					dialog=null;
					finish();
				}
			});
    	      
    	      dialog=new Dialog(this, R.style.info_dialog);
    	      dialog.setContentView(layout);
    	      dialog.setCancelable(true);
    	      dialog.setCanceledOnTouchOutside(true);
    	      
    	     Window w=dialog.getWindow();
    	     w.setGravity(Gravity.RIGHT|Gravity.TOP);
             WindowManager.LayoutParams lp =w.getAttributes();
             lp.x=0;
             lp.y=90;
             dialog.onWindowAttributesChanged(lp);  
             dialog.show();
      }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updatePresentation() {
        if (mMediaRouter == null)
            return;

        // Get the current route and its presentation display.
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
            MediaRouter.ROUTE_TYPE_LIVE_VIDEO);

        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;

        // Dismiss the current presentation if the display has changed.
        if (mPresentation != null && mPresentation.getDisplay() != presentationDisplay) {
            Log.i(TAG, "Dismissing presentation because the current route no longer "
                    + "has a presentation display.");
            mLibVLC.stop();
            finish(); //TODO restore the video on the new display instead of closing
            mPresentation.dismiss();
            mPresentation = null;
        }

        // Show a new presentation if needed.
        if (mPresentation == null && presentationDisplay != null) {
            Log.i(TAG, "Showing presentation on display: " + presentationDisplay);
            mPresentation = new SecondaryDisplay(this, presentationDisplay);
            mPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                Log.w(TAG, "Couldn't show presentation!  Display was removed in "
                        + "the meantime.", ex);
                mPresentation = null;
            }
        }
    }

    /**
     * Listens for when presentations are dismissed.
     */
    private final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (dialog == mPresentation) {
                Log.i(TAG, "Presentation was dismissed.");
                mPresentation = null;
            }
        }
    };

	private final static class SecondaryDisplay extends Presentation {
        public final static String TAG = "VLC/SecondaryDisplay";

        private Context mContext;
        private SurfaceView mSurface;
        private SurfaceView mSubtitlesSurface;
        private SurfaceHolder mSurfaceHolder;
        private SurfaceHolder mSubtitlesSurfaceHolder;
        private FrameLayout mSurfaceFrame;
        private LibVLC mLibVLC;

		public SecondaryDisplay(Context context, Display display) {
            super(context, display);
            if (context instanceof Activity) {
                setOwnerActivity((Activity) context);
            }
            mContext = context;

            try {
                mLibVLC = Util.getLibVlcInstance();
            } catch (LibVlcException e) {
                Log.d(TAG, "LibVLC initialisation failed");
                return;
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		@SuppressLint("NewApi")
		@Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.local_player_remote);

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);

            mSurface = (SurfaceView) findViewById(R.id.remote_player_surface);
            mSurfaceHolder = mSurface.getHolder();
            mSurfaceFrame = (FrameLayout) findViewById(R.id.remote_player_surface_frame);
            String chroma = pref.getString("chroma_format", "");
            if(LibVlcUtil.isGingerbreadOrLater() && chroma.equals("YV12")) {
                mSurfaceHolder.setFormat(ImageFormat.YV12);
            } else if (chroma.equals("RV16")) {
                mSurfaceHolder.setFormat(PixelFormat.RGB_565);
            } else {
                mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
            }

            VideoPlayerActivity activity = (VideoPlayerActivity)getOwnerActivity();
            if (activity == null) {
                Log.e(TAG, "Failed to get the VideoPlayerActivity instance, secondary display won't work");
                return;
            }

            mSurfaceHolder.addCallback(activity.mSurfaceCallback);

            mSubtitlesSurface = (SurfaceView) findViewById(R.id.remote_subtitles_surface);
            mSubtitlesSurfaceHolder = mSubtitlesSurface.getHolder();
            mSubtitlesSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
            mSubtitlesSurface.setZOrderMediaOverlay(true);
            mSubtitlesSurfaceHolder.addCallback(activity.mSubtitlesSurfaceCallback);

            /* Only show the subtitles surface when using "Full Acceleration" mode */
            if (mLibVLC != null && mLibVLC.getHardwareAcceleration() == 2)
                mSubtitlesSurface.setVisibility(View.VISIBLE);
        }
    }
    
    private void getFirstGesture(final SharedPreferences preferences){
  	  boolean isShowGesture = preferences.getBoolean(PreferencesActivity.VIDEO_CONTROL_GESTURE, false);//还未展现过手�?        
  	if(!isShowGesture){
      View layout = LayoutInflater.from(VideoPlayerActivity.this).inflate( R.layout.control_gesture, null);
 	      
 	      dialog=new Dialog(this, R.style.mhorprogress);
 	      dialog.setContentView(layout);
 	      dialog.setCancelable(true);
 	      dialog.setCanceledOnTouchOutside(true);
 	      
 	     Window w=dialog.getWindow();
          WindowManager.LayoutParams lp =w.getAttributes();
          lp.x=LayoutParams.FILL_PARENT;
          lp.y=LayoutParams.FILL_PARENT;
          dialog.onWindowAttributesChanged(lp);  
          dialog.show();
      	   
        	final RelativeLayout control_gesture_layout=(RelativeLayout) layout.findViewById(R.id.control_gesture_layout);
        	final TextView control_introduce=(TextView) layout.findViewById(R.id.control_introduce);
        	  control_sound=(ImageView) layout.findViewById(R.id.control_sound);
            control_progress=(ImageView) layout.findViewById(R.id.control_progress);
            control_light=(ImageView) layout.findViewById(R.id.control_light);
            control_gesture_layout.setVisibility(0);
            final int LENGTH=5;//5个像素移动距�?标识手势正确
            control_gesture_layout.setTag(R.id.control_light);
            control_gesture_layout.setOnTouchListener(new OnTouchListener() {
  				@Override
    				public boolean onTouch(View v, MotionEvent event) {
    					// TODO Auto-generated method stub
    					if(event.getAction() == MotionEvent.ACTION_DOWN){
    						m=event.getY();
    					}else if(event.getAction() == MotionEvent.ACTION_UP){
    						if((Integer)v.getTag()==R.id.control_light){
    							if(Math.abs(event.getY()-m)>LENGTH){
    								 control_light.setVisibility(8);
    	      						 control_introduce.setText(getString(R.string.control_sound));
    	      						 control_sound.setVisibility(0);
    	      						 control_gesture_layout.setTag(R.id.control_sound);
    							}
    							
    						}else if((Integer)v.getTag()==R.id.control_sound){
    							if(Math.abs(event.getY()-m)>LENGTH){
    	  							control_sound.setVisibility(8);
    	  							control_introduce.setText(getString(R.string.control_progress));
    	  							control_progress.setVisibility(0);
    	  						control_gesture_layout.setTag(R.id.control_progress);
    	  						}
    							
    						}else if((Integer)v.getTag()==R.id.control_progress){
    	  						if(Math.abs(event.getX()-m)>LENGTH){
    	  						    control_progress.setVisibility(8);
    	  						    control_gesture_layout.setVisibility(8);
    	  						    control_introduce.setText(getString(R.string.control_complete));
    	  						    preferences.edit().putBoolean(PreferencesActivity.VIDEO_CONTROL_GESTURE, true).commit();
    	  						    mHandler.postDelayed(new Runnable() {
    								@Override
    								public void run() {
    									// TODO Auto-generated method stub
    									dialog.dismiss();
    									dialog=null;
    								}
    							}, 1000);
    	  						}
    	  					
    						}
    					}
    					return true;
    				}
    			});
  	}
        }
    
}
