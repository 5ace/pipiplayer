package cn.pipi.mobile.pipiplayer.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import android.view.TextureView;

public class MovieInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String img;
	private String state;//更新至12集
	private String subtitle;//结婚有理，我想结婚
	private String dafen_num;//4.0
	private String type;//电影
	private String area;//区域
	private String year;//年份
	private String mType;//类型 爱情
	private String actor;//演员
	private String director;//演员
	private String desc;//详情
	private List<String> movieTag;//影片来源标签
	private Map<String, ArrayList<String>> playList;//影片地址
	
	private long progress;//播放进度
	private int position;//播放第几集
	private String path;//存放地址
	private String tag;//当前选中标签
	
	private boolean ordered = true;//默认正序
	private String header;  //用于首页分组 
	private int headerId;  //用于首页分组 
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		if(!TextUtils.isEmpty(img)&&img.startsWith("http")){
			this.img = img;
		}else{
			this.img="http://img.pipi.cn/movies/126X168/" +img;
		}
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getDafen_num() {
		return dafen_num;
	}
	public void setDafen_num(String dafen_num) {
		this.dafen_num = dafen_num;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getmType() {
		return mType;
	}
	public void setmType(String mType) {
		this.mType = mType;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public List<String> getMovieTag() {
		return movieTag;
	}
	public void setMovieTag(List<String> movieTag) {
		this.movieTag = movieTag;
	}
	public Map<String, ArrayList<String>> getPlayList() {
		return playList;
	}
	public void setPlayList(Map<String, ArrayList<String>> playList) {
		this.playList = playList;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public long getProgress() {
		return progress;
	}
	public void setProgress(long progress) {
		this.progress = progress;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public boolean isOrdered() {
		return ordered;
	}
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public int getHeaderId() {
		return headerId;
	}
	public void setHeaderId(int headerId) {
		this.headerId = headerId;
	}
	
	
	
}
