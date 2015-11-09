package com.nicenearby.sogou.discovery.mapsdk.location;

/**
 * Created by yejihuang on 2015/5/25.
 */
import com.lidroid.xutils.exception.HttpException;
import com.nicenearby.sogou.discovery.mapsdk.ctrl.MapWrapperController;
import com.nicenearby.sogou.discovery.utils.SysUtils;
import com.nicenearby.sogou.discovery.view.main.MainActivity;
import com.nicenearby.sogou.discovery.view.news.NewsDownload;
import com.sogou.map.loc.SGLocClient;
import com.sogou.map.loc.SGLocation;

import android.content.Context;
import android.util.Log;

/**
 * 处理所有业务逻辑，具体逻辑主要有MapLocaitonController处理. 主要功能如下： 处理按钮三状态和loading.
 * 对定位的频率和出错进行默认控制。
 *
 * 不涉及具体UI的改变
 *
 * @author qishengxing
 */
public class LocationController {
    public static final int NEED_ZOOM_MAX_LEVEL_LOC = 15;
    public static final int NEED_ZOOM_MAX_LEVEL_NAV = 15;
    public static final int NEED_ZOOM_MIN_LEVEL_NAV = 14;

    public static final int NEED_ZOOM_MIN_LEVEL_TRAFFICDOG = 13;

    public static final int NEED_ZOOM_MIN_LEVEL = 8;

    public static enum LocationStatus {
        LOCATING, BROWS, NAV, FOLLOW
    };

    private LocationManager mLocationMgr;
    private LocationStatus mPreLocationStatus = LocationStatus.NAV;// 记录上一次是跟踪还是光束跟踪
    private LocationStatus mLocationStatus = LocationStatus.BROWS;// 当前定位状态
    private boolean mIsNaving = false;

    /***
     * 是否在路况电子狗情景
     */
    private boolean mIsTrafficDogNaving = false;
//	private boolean mIsUsingNavLoc = false;

    private boolean mForceZoomtoMaxLevel = false;



    /*** singleton related **/
    private Context mContext;

    private LocationController() {
        mContext = SysUtils.getMainActivity();
        mLocationMgr = new LocationManager(mContext);
        mLocationMgr.setStrategy(SGLocClient.NETWORK_ONLY);
    }

    private static  LocationController sInstance;
    private static LocationInfo mLocationInfo;


    /** 不加任何参数，方式把Acivity作为context传入
     * 由于调用比较频繁，采用double check 的方式 **/
    public static  LocationController getInstance() {
        if (sInstance == null) {
            synchronized (LocationController.class) {
                if(sInstance == null){
                    sInstance = new LocationController();
                }
            }
        }
        return sInstance;
    }


    public void destroy(){
        sInstance = null;
        if (mLocationMgr != null) {
            mLocationMgr = null;
        }
    }



    /** 可能为空，如果过期，也为空 ***/
    public static LocationInfo getCurrentLocationInfo() {
        return mLocationInfo;
    }


    public void start(int watchperiodTime) {
        if (mLocationMgr != null){
            mLocationMgr.startLocation(watchperiodTime);
//			mLocationMgr.requestOnceLocation();
        }
    }

    public void stop() {
        if (mLocationMgr != null)
            mLocationMgr.stopLocation();
    }





    public void setLocationStatus(LocationStatus status) {
        mLocationStatus = status;
    }

    public LocationStatus getLocationStatus() {
        return mLocationStatus;
    }

    public void setPreLocationStatus(LocationStatus status) {
        mPreLocationStatus = status;
    }

    public LocationStatus getPreLocationStatus() {
        return mPreLocationStatus;
    }


    public void forceZoomtoMaxLevel(boolean doZoom) {
        mForceZoomtoMaxLevel = doZoom;
    }

    public boolean isForceZoomtoMaxLevel() {
        return mForceZoomtoMaxLevel;
    }

    public int getZoomMaxLvl() {
        int needZoomMaxLvl;
        needZoomMaxLvl = NEED_ZOOM_MAX_LEVEL_LOC;
        return needZoomMaxLvl;
    }

    /**
     * get the default level when it is need to auto zoom when navigating or
     * locating
     *
     * @param mapCtrl
     * @return
     */
    public int getPropMapLevel(MapWrapperController mapCtrl) {
        int curLevel = mapCtrl.getZoom();
        int needZoomMaxLvl = getZoomMaxLvl();
        if (curLevel < NEED_ZOOM_MIN_LEVEL) {
            return needZoomMaxLvl;
        } else {
            return curLevel;
        }
    }


    public void addListener(final MainActivity.OnShowPopListener listener) {
        Log.e(MainActivity.TAG, "LocationController addListener");

        mLocationMgr.addSgLocationListener(new SgLocateListener() {

            @Override
            public void onLocationUpdate(SGLocation locationInfo) {
                LocationStatus locatatus = LocationController.getInstance().getLocationStatus();
                mLocationInfo = new LocationInfo(locationInfo);
                Log.e(MainActivity.TAG, "onLocationUpdate locationInfob  here" + mLocationInfo);
                MainActivity mainActivity = SysUtils.getMainActivity();
                if(mainActivity == null){
                    return;
                }
                if(locatatus != null ){
                    if(locatatus == LocationStatus.LOCATING){
                        LocBtnManager.getInstance(mainActivity).browsToNav();
                        SysUtils.getMapCtrl().zoomTo(12, true);
                    }else if(locatatus == LocationStatus.BROWS){
                        SysUtils.getMapCtrl().moveGpsTo(mLocationInfo);
                    }else if(locatatus == LocationStatus.NAV){
                        LocBtnManager.getInstance(mainActivity).browsToNav();
                    }else if(locatatus == LocationStatus.FOLLOW){
                        LocBtnManager.getInstance(mainActivity).browsToFollow();
                    }
                }
                NewsDownload.getInstance().setOnSuccessListener(
                        new NewsDownload.OnSuccessListener() {
                    @Override
                    public void onStart() {
                        listener.onStart();
                    }
                    @Override
                    public void onSuccess() {
                        listener.showPop();
                    }
                    @Override
                    public void onFailure(HttpException error) {
                        listener.showError(error);
                    }
                });
            }

            @Override
            public void onLocationError(int code, String message) {
                Log.e(MainActivity.TAG, "onLocationError   " + code +  "   message  "+ message);
            }
        });
    }

//	public void playGuidance(String guidance, int level, int liveTime) {
//		if (mLocationMgr != null) {
//			mLocationMgr.playGuidance(guidance, 2, liveTime);
//		}
//	}

}

