package com.nicenearby.sogou.discovery.mapsdk.data;

import com.sogou.map.mobile.geometry.Coordinate;

/**
 *
 * 特征类,包括坐标，名称，uid，dataid信息等
 * @author yongliangzhang
 *
 */
public class Feature {

	private static final long serialVersionUID = 1L;
	/** 坐标 */
	private Coordinate mCoord;
	/** 名称 */
	private String mName = "";
	/** uid */
	private String mUid = "";
	/** 数据的编号 */
	private String mDataId = "";
	/** 距离*/
	private String mPoiDis = "-1.0";

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Feature clone() {
		try {
			Feature ret = (Feature) super.clone();
			if (mCoord != null) {
				ret.mCoord = new Coordinate(mCoord);
			}
			return ret;
		} catch (CloneNotSupportedException e) {
			// not reachable
			return this;
		}
	}

	/**
	 * 设置坐标信息
	 * @param x
	 * @param y
	 */
	public void setCoord(float x, float y) {
		mCoord = new Coordinate(x, y);
	}

	/**
	 * 设置坐标信息
	 * @param coord
	 */
	public void setCoord(Coordinate coord) {
		mCoord = coord;
	}

	/**
	 * 获取坐标信息
	 * @return
	 */
	public Coordinate getCoord() {
		return mCoord;

	}

	/**
	 * @return the mName
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param name the mName to set
	 */
	public void setName(String name) {
		this.mName = name;
	}

	/**
	 * @return the mUid
	 */
	public String getUid() {
		return mUid;
	}

	/**
	 * @param uid the mUid to set
	 */
	public void setUid(String uid) {
		this.mUid = uid;
	}

	/**
	 * 获取数据的编号
	 * @return the dataId
	 */
	public String getDataId() {
		return mDataId;
	}

	/**
	 * 设置数据的编号
	 * @param dataId the dataId to set
	 */
	public void setDataId(String dataId) {
		this.mDataId = dataId;
	}

	public String getDis() {
		return mPoiDis;
	}

	public void setDis(String mPoiDis) {
		this.mPoiDis = mPoiDis;
	}
}
