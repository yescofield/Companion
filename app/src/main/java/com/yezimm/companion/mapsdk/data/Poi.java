/**
 * Author:ZhangYongLiang
 * Date:2012��4��17��
 * e-mail:yongliangzhang@sohu-inc.om
 * 
 */
package com.nicenearby.sogou.discovery.mapsdk.data;

import java.util.List;

import com.sogou.map.mobile.geometry.Bound;
import com.sogou.map.mobile.geometry.Coordinate;
import com.sogou.map.mobile.geometry.Geometry;

/**
 * POI信息
 */
public class Poi extends Feature {

	private static final long serialVersionUID = 1L;
	/** 大类 */
	private String mCategory = "";
	/** 小类 */
	private String mSubCategory = "";

	/** 数据提供者的编号，收藏中用到的 */
	private String mCpid = "";
	/** 视野bound */
	private Bound mMapBound;
	/** 线面数据,使用PreparedLineString和来区分 */
	private List<Geometry> mPoints;

	/** 电话 */
	private String mPhone = "";
	/** 简介信息 */
	private String mPoiDesc = "";

	/** 代表数据聚类属性 */
	private PoiType mClustering;
	/** 用户在论坛纠错的点有此属性，属性值是被修改数据的dataid属性内容 */
	private String mOwnerId = "";



	/** 如果是银行、atm或加油站，则有详细分类信息，比如：中国工商银行、中国石化 */
	private CategoryDetailType mCategoryDetailType = CategoryDetailType.UNKNOWN;

	/** 是否有二级子节点*/
	private boolean mHasChildren;

	public Poi() {
	}

	/**
	 * 带关键字的构造方法
	 * @param name
	 */
	public Poi(String name) {
		setName(name);
	}

	/**
	 * 带坐标的构造方法
	 * @param x
	 * @param y
	 */
	public Poi(float x, float y) {
		setCoord(x, y);
	}

	/**
	 * 带坐标的构造方法
	 * @param x
	 * @param y
	 */
	public Poi(String name, float x, float y) {
		setName(name);
		setCoord(x, y);
	}
	/**
	 * 带坐标的构造方法
	 */
	public Poi(String name, Coordinate coord) {
		setName(name);
		setCoord(coord);
	}

	/**构造方法
	 * @param name：名称
	 * @param uid：uid
	 */
	public Poi(String name, String uid) {
		setName(name);
		setUid(uid);
	}

	/**构造方法
	 */
	public Poi(String name, String uid, Coordinate coord) {
		setName(name);
		setUid(uid);
		setCoord(coord);
	}
	/**构造方法
	 * @param name：名称
	 * @param uid：uid
	 * @param x：x坐标
	 * @param y：y坐标
	 */
	public Poi(String name, String uid, float x, float y) {
		setName(name);
		setUid(uid);
		setCoord(x, y);
	}


	/**
	 * 获取大类
	 * @return the category
	 */
	public String getCategory() {
		return mCategory;
	}

	/**
	 * 设置大类
	 */
	public void setCategory(String category) {
		this.mCategory = category;
	}

	/**
	 * 获取小类
	 */
	public String getSubCategory() {
		return mSubCategory;
	}

	/**
	 * 设置小类
	 */
	public void setSubCategory(String subCategory) {
		this.mSubCategory = subCategory;
	}


	/**
	 * 获取数据提供者的编号
	 */
	public String getCpid() {
		return mCpid;
	}

	/**
	 * 设置数据提供者的编号
	 */
	public void setCpid(String cpid) {
		this.mCpid = cpid;
	}

	/**
	 * 获取地图视野区域
	 * @return 可能为null
	 */
	public Bound getMapBound() {
		return mMapBound;
	}

	/**
	 * 设置地图视野区域
	 */
	public void setMapBound(Bound bound) {
		this.mMapBound = bound;
	}

	/**
	 * 获取点集合，点类型返回null，直接调用getCoord获取位置
	 */
	public List<Geometry> getPoints() {
		return mPoints;
	}

	/**
	 * 设置点信息
	 */
	public void setPoints(List<Geometry> points) {
		this.mPoints = points;
	}

	/**
	 * 获取电话号码
	 */
	public String getPhone() {
		return mPhone;
	}

	/**
	 * 设置电话号码
	 */
	public void setPhone(String phone) {
		this.mPhone = phone;
	}

	/**
	 * 获取简介信息
	 * @return the poiDesc
	 */
	public String getDesc() {
		return mPoiDesc;
	}

	/**
	 * 设置简介信息
	 * @param poiDesc the poiDesc to set
	 */
	public void setDesc(String poiDesc) {
		this.mPoiDesc = poiDesc;
	}

	/**
	 * 获取数据类别
	 * @return the clustering
	 */
	public PoiType getType() {
		return mClustering;
	}

	/**
	 * 设置数据类别
	 * @param clustering the clustering to set
	 */
	public void setType(PoiType clustering) {
		this.mClustering = clustering;
	}

	/**
	 * ownerId是用户在论坛纠错的点有此属性，属性值是被修改数据的dataid属性内容，获取ownerId
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return mOwnerId;
	}

	/**
	 * ownerId是用户在论坛纠错的点有此属性，属性值是被修改数据的dataid属性内容，设置ownerId
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.mOwnerId = ownerId;
	}






	/**
	 * 获取详细分类信息，比如：中国工商银行、中国石化
	 */
	public CategoryDetailType getCategoryDetailType() {
		return mCategoryDetailType;
	}

	/**
	 * 设置详细分类信息，比如：中国工商银行、中国石化
	 */
	public void setCategoryDetailType(CategoryDetailType categoryDetailType) {
		mCategoryDetailType = categoryDetailType;
	}

	/**
	 * 是否有二级子节点
	 */
	public boolean isHasChildren() {
		return mHasChildren;
	}

	/**
	 * 设置是否有二级子节点
	 */
	public void setHasChildren(boolean hasChildren) {
		mHasChildren = hasChildren;
	}

	/**
	 * 停车场状态
	 */
	public enum ParkStatus {
		FULL, // 满
		LITTLE, // 少
		EMPTY, // 空
		UNKNOWN, // 未知
	}

	/**
	 * 详细分类信息
	 */
	public enum CategoryDetailType {
		UNKNOWN, // 未知
		BC, // 中行
		ICBC, // 工行
		CBC, // 建行
		BOC, // 交行
		ABC, // 农行
		CMB, // 招商银行
		PSBC, // 邮政储蓄
		CITIC,	//中信
		CEB,	//光大
		SINOPEC, // 中石化
		CNPC, // 中石油
		SHELL, // 壳牌
	}

	/**
	 * 结构化点
	 */
	public static class StructuredPoi extends Poi {

		private static final long serialVersionUID = 1L;
		/** 是否在列表上显示 */
		private boolean mVisiable;
		/** 是否正门 */
		private boolean mMainDoor;
		/** 是否去过 */
		private boolean mBeen;

		/**
		 * 是否在列表上显示
		 */
		public boolean isVisiable() {
			return mVisiable;
		}

		/**
		 * 设置是否在列表上显示
		 */
		public void setVisiable(boolean visiable) {
			mVisiable = visiable;
		}

		/**
		 * 是否正门
		 */
		public boolean isMainDoor() {
			return mMainDoor;
		}

		/**
		 * 设置是否正门
		 */
		public void setMainDoor(boolean mainDoor) {
			mMainDoor = mainDoor;
		}

		/**
		 * 是否去过
		 */
		public boolean isBeen() {
			return mBeen;
		}

		/**
		 * 设置是否去过
		 */
		public void setBeen(boolean been) {
			mBeen = been;
		}

	}

	/**
	 * 拟合Poi
	 */
	public static class CalculatePoi extends Poi {

		private static final long serialVersionUID = 1L;

	}

	/** 代表数据属性 */
	public enum PoiType {
		/** 未知数据 */
		UNKNOWN,
		/** 普通数据 */
		NORMAL,
		/** 公交车站 */
		STOP,
		/** 地铁车站 */
		SUBWAY_STOP,
		/** 公交线路 */
		LINE,
		/** 地铁线路 */
		SUBWAY_LINE,
		/** 道路 */
		ROAD,
		/** 拟合类型*/
		Virtual_POI
	}

	/** 分类词类型 */
	public enum CategoryType {
		/** 普通类型 */
		NORMAL,
		/** 餐饮 */
		REPAST,
		/** 酒店 */
		HOTEL,
		/** 停车场 */
		PARK,
		/** 团购 */
		GROUPON,
	}
}
