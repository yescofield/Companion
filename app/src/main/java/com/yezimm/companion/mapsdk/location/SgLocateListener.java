package com.nicenearby.sogou.discovery.mapsdk.location;

import com.sogou.map.loc.SGLocation;

/**
 * Created by yejihuang on 2015/5/25.
 */
public interface SgLocateListener {

    /** 位置更新 **/
    public void onLocationUpdate(SGLocation locationInfo);


    /**
     *  定位失败时的回调函数,
     code为错误类型,取值为
     SGLocation.CODE_PERMISSION_DENIED(受限)
     SGLocation.CODE_POSITION_UNAVAILABLE(定位失败)
     SGLocation.CODE_TIMEOUT(超时)
     message为具体错误信息
     * @param code
     * @param message
     */
    public void onLocationError(int code, String message);

}

