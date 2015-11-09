package com.nicenearby.sogou.discovery.mapsdk.location;

/**
 * Created by yejihuang on 2015/5/25.
 */
import android.view.View;

import com.nicenearby.sogou.discovery.mapsdk.ctrl.MapWrapperController;
import com.nicenearby.sogou.discovery.utils.SysUtils;
import com.nicenearby.sogou.discovery.view.main.MainActivity;
import com.sogou.map.mobile.engine.core.LayerType;
import com.sogou.map.mobile.engine.core.Pixel;

import com.nicenearby.sogou.discovery.mapsdk.location.LocationController.LocationStatus;

public final class LocBtnManager {
    private MainActivity mMainActivity;
    private LocationController mLocCtrl;
    private MapWrapperController mMapCtrl;


    /** 之所以用mainactivity作为参数，是因为想明确表明该类是对mainactiviy的扩展，需要操作mainactivity上面的UI **/
    public LocBtnManager(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mMapCtrl = mMainActivity.getMapController();
        mLocCtrl = LocationController.getInstance();
    }

    private static LocBtnManager sInstance;

    public static LocBtnManager getInstance(MainActivity mainActivity) {
        if (sInstance == null) {
            sInstance = new LocBtnManager(mainActivity);
        }
        return sInstance;
    }

    public static void clear() {
        sInstance = null;
    }

    // when after locating, it will zoom;
    public void browsToNav() {
        LocationInfo lastLoc = LocationController.getCurrentLocationInfo();
        // 如果已经定过位置，则直接定位，
        if (lastLoc != null) {
            gotoNav();
            return;
        }else {
            SysUtils.getMainActivity().setLocBtnStatus(LocationStatus.LOCATING);
        }

    }

    // when after locating, it will zoom;
    public void browsToFollow() {
        LocationInfo lastLoc = LocationController.getCurrentLocationInfo();
        // 如果已经定过位置，则直接定位，
        if (lastLoc != null) {
            gotoFollow();
            return;
        }else {
            SysUtils.getMainActivity().setLocBtnStatus(LocationStatus.LOCATING);
        }

    }


    public void gotoBrows() {
        if(mLocCtrl == null){
            return;
        }
        mMainActivity.setLocBtnStatus(LocationStatus.BROWS);

        mLocCtrl.forceZoomtoMaxLevel(false);


//        if (!mMapCtrl.hasMapRotate())
//            mMainActivity.updateCompassRotate(0, 0);	//更新指南针显示隐藏状态


    }

    /**
     * 直接转移到nav状态。前提是已经获取到位置。
     *
     */

    public void gotoNav() {
//		LocationStatus originLocStatus = mLocCtrl.getLocationStatus();
        if(mLocCtrl == null){
            return;
        }
        mMapCtrl.setResetTo2DMap(false);
        if (!mMapCtrl.isLayerVisible(LayerType.LAYERTYPE_ECITY)) {
            mMapCtrl.setGpsDirectionVisibility(false);
        }

        LocationInfo lastLoc = LocationController.getCurrentLocationInfo();
        if (lastLoc == null) {
            return;
        }
        mMainActivity.setLocBtnStatus(LocationStatus.NAV);
        Pixel anchor = mMapCtrl.getCenterPix();
        if (mMapCtrl.getGpsVisibility() != View.VISIBLE) {
            mMapCtrl.setGpsVisibility(true);
//			SgLog.dFile(SgLocationManager.GPS_MISS_LOG, "gps restore due to gotoNav");
        }
        mMapCtrl.moveGpsTo(lastLoc);
        mMapCtrl.moveAndRotateMap(lastLoc, anchor, 0);
//        if (!mMapCtrl.hasMapRotate())
//            mMainActivity.updateCompassRotate(0, 0);	//更新指南针显示隐藏状态


    }

    /**
     * 能进如follow状态，说明肯定获取到位置了，此时地图转，gps不转。
     *
     */
    public void gotoFollow() {
        if(mLocCtrl == null){
            return;
        }
        mMainActivity.setLocBtnStatus(LocationStatus.FOLLOW);

        mMapCtrl.setResetTo2DMap(false);
        mMapCtrl.setGpsDirectionVisibility(true);

        LocationInfo lastLoc = LocationController.getCurrentLocationInfo();
        Pixel anchor = mMapCtrl.getCenterPix();
        if (lastLoc != null) {
            mMapCtrl.moveAndRotateMap(lastLoc, anchor,-lastLoc.getBearing());
//            if (!mMapCtrl.hasMapRotate())
//                mMainActivity.updateCompassRotate(0, -lastLoc.getBearing());	//更新指南针显示隐藏状态
        }



    }

}