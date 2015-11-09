package com.nicenearby.sogou.discovery.mapsdk.data;

import java.io.Serializable;

/**
 * 签名授权服务数据类
 * @author yongliangzhang
 *
 */
public class SignServiceData implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	/** 应用id*/
	private String mAppId;

	/** 与appId对应的appKey */
	private String mAppKey;

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SignServiceData clone() {
		try {
			return (SignServiceData) super.clone();
		} catch (CloneNotSupportedException e) {
			// not reachable
			return this;
		}
	}

	/**
	 * 获取应用id
	 * @return 应用id
	 */
	public String getAppId() {
		return mAppId;
	}

	/**
	 * 设置应用id(必须)
	 * @param appId 应用id
	 */
	public void setAppId(String appId) {
		mAppId = appId;
	}

	/**
	 * 获取与appId对应的appKey
	 * @return the appKey
	 */
	public String getAppKey() {
		return mAppKey;
	}

	/**
	 * 设置与appId对应的appKey
	 * @param appKey the appKey to set
	 */
	public void setAppKey(String appKey) {
		mAppKey = appKey;
	}
}
