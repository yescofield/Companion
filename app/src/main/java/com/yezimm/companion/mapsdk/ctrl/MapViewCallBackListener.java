package com.nicenearby.sogou.discovery.mapsdk.ctrl;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nicenearby.sogou.discovery.R;
import com.nicenearby.sogou.discovery.config.DConfig;
import com.nicenearby.sogou.discovery.mapsdk.data.Poi;
import com.nicenearby.sogou.discovery.mapsdk.location.LocBtnManager;
import com.nicenearby.sogou.discovery.mapsdk.location.LocationController;
import com.nicenearby.sogou.discovery.mapsdk.location.LocationController.LocationStatus;
import com.nicenearby.sogou.discovery.mapsdk.location.LocationInfo;
import com.nicenearby.sogou.discovery.utils.LocationUtils;
import com.nicenearby.sogou.discovery.utils.SysUtils;
import com.nicenearby.sogou.discovery.view.main.MainActivity;
import com.nicenearby.sogou.discovery.view.protocol.news.NewsParams;
import com.sogou.map.mobile.engine.core.Camera.ICameraListener;
import com.sogou.map.mobile.engine.core.MapGesture;
import com.sogou.map.mobile.engine.core.MapStatus;
import com.sogou.map.mobile.engine.core.MapViewStatusChangeListener;
import com.sogou.map.mobile.engine.core.OverPoint;
import com.sogou.map.mobile.engine.core.Pixel;
import com.sogou.map.mobile.geometry.Coordinate;
import com.sogou.map.mobile.geometry.Point;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

/**
 *  MapGesture.IListener 各种手势、拖动、长按等回调
 *  ICameraListener  级别、视野、旋转回调
 *  MapViewStatusChangeListener 地图静止回调
 * @author luqingchao
 *
 */
public class MapViewCallBackListener implements MapGesture.IListener, ICameraListener,MapViewStatusChangeListener{
	private static final int LONGPRESSPOINTLAYER = 1;
	private MainActivity mMainActivity = null;

	private LocationController mLocCtrl;

	public static final int MIN_DRAG_PIX = 30;

	private int mLastX, mLastY;
	private com.sogou.map.mobile.engine.core.Coordinate mLastMapCenterGeo;
	private Pixel mLastMapCenterPix;

	private boolean mLongPressWaiting;
	protected Coordinate mLongClickGeo = null;
	protected Poi mLongClickPoi = null;
	protected boolean isLongClicking = false;

	private boolean mZoomed = false;
//	private int mLastZoomLevel = 0;

	private OverPoint mLongPressOver;

	public MapViewCallBackListener() {
		mLongPressWaiting = false;
		//at this point, main activity should already be assigned to page
		mMainActivity = SysUtils.getMainActivity();
		if(mMainActivity == null){
			return;
		}
		mLocCtrl = LocationController.getInstance();
	}

	public void onMapClick(com.sogou.map.mobile.engine.core.Coordinate coordinate) {
	}

	public void onMapLevelChanged(byte level){
	}

	protected Coordinate getGeo() {
		if(LocationController.getCurrentLocationInfo() == null ||  LocationController.getCurrentLocationInfo().getLocation() == null){
			return null;
		}
		Point p = LocationController.getCurrentLocationInfo().getLocation();
		Coordinate geo = new Coordinate(p.getX(), p.getY());
		return geo;
	}

	@Override
	public boolean onClick(int arg0, int arg1) {
		onMapClick(SysUtils.getMapCtrl().rayGround(arg0,arg1));
		return false;
	}

	@Override
	public boolean onDrag(int fingerCount, int x, int y, double d, double d1) {
		MainActivity mMainActivity = SysUtils.getMainActivity();
		if(mMainActivity == null){
			return false;
		}
		LocBtnManager mLocBtnManager = LocBtnManager.getInstance(mMainActivity);


		Pixel currentCenterPix = SysUtils.getMapCtrl().rayScreen(mLastMapCenterGeo);

		if ((Math.abs(mLastX - x) > MIN_DRAG_PIX || Math.abs(mLastY - y) > MIN_DRAG_PIX) &&
				Math.abs(mLastMapCenterPix.getX() - currentCenterPix.getX()) > MIN_DRAG_PIX ||
				Math.abs(mLastMapCenterPix.getY() - currentCenterPix.getY()) > MIN_DRAG_PIX) {

			mLocBtnManager.gotoBrows();
			onDragOcurred(fingerCount, x, y, d, d1);
			if(mLongPressOver != null){
				MapViewOverLay.getInstance().removePoint(mLongPressOver, LONGPRESSPOINTLAYER) ;
			}
			mLastX = x;
			mLastY = y;
		}

		return false;
	}

	@Override
	public boolean onDragInit(int fingerCount, int x, int y) {
		mLastX = x;
		mLastY = y;
		mLastMapCenterGeo = SysUtils.getMapCtrl().getMapScreenCenter();
		mLastMapCenterPix = SysUtils.getMapCtrl().rayScreen(mLastMapCenterGeo);
		return false;
	}

	@Override
	public boolean onDragOver() {
		return false;
	}

	@Override
	public boolean onFling(double arg0, double arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLongClick(final int x, final int y) {
		isLongClicking = true;
		mLongClickPoi = null;
		MainActivity mainActivity = SysUtils.getMainActivity();
		if(mainActivity != null){
			mainActivity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					mLongClickGeo = mMainActivity.getMapController().pix2mercator(new Point(x, y));
					doLongPress();
				}

			});
		}
		return false;
	}

	@Override
	public boolean onMutiFingerClick(int arg0, int arg1, int arg2) {
		return false;
	}


	@Override
	public boolean onMutiTimeClick(int arg0, int arg1, int arg2) {
		if(mMainActivity == null){
			return false;
		}
		MapWrapperController mMapCtrl = mMainActivity.getMapController();
		LocBtnManager locBtnMgr = LocBtnManager.getInstance(mMainActivity);

		LocationStatus locStatus = mLocCtrl.getLocationStatus();
		LocationInfo loc = LocationController.getCurrentLocationInfo();
		if (locStatus == LocationStatus.LOCATING) {
			// 正在定位过程中，有drag事件操作，定位完成后，将恢复至普通状态
			locBtnMgr.gotoBrows();
		}
		if ((LocationStatus.NAV == locStatus || LocationStatus.FOLLOW == locStatus) && loc != null) {
			Point p = loc.getLocation();
			com.sogou.map.mobile.geometry.Coordinate sdkCoor = new com.sogou.map.mobile.geometry.Coordinate(p.getX(), p.getY());
			mMapCtrl.moveTo(sdkCoor,false);
		}
		return false;
	}


	@Override
	public void onTouchUp(int i, int j, int k) {
		if (!SysUtils.getMapCtrl().isTouching() && isLongClicking) {
			isLongClicking = false;
			final int m = i;
			final int n = j;
			final int t = k;
		}
	}


	/**
	 * 由于地图输入事件（Click）会伴随其他的事件产生（Drag、Move）。
	 * 而当用户长按地图时，要求画出来的图钉大点不能消失。为了避免上述的事件冲突，引入此标志量。
	 * PS：当产生LongPress事件时，将此标志量设为：true；当LongPress事件处理时，第二个Popwin产生完毕后，将此标志量设为：false。
	 * @date 2013.5.21
	 * @author renjie
	 * */
	public boolean isLongPressWaiting(){
		return mLongPressWaiting;
	}

	/**
	 * 由于地图输入事件（Click）会伴随其他的事件产生（Drag、Move）。
	 * 而当用户长按地图时，要求画出来的图钉大点不能消失。为了避免上述的事件冲突，引入此标志量。
	 * PS：当产生LongPress事件时，将此标志量设为：true；当LongPress事件处理时，第二个Popwin产生完毕后，将此标志量设为：false。
	 * @date 2013.5.21
	 * @author renjie
	 * */
	public void setLongPressWaiting(boolean state){
		mLongPressWaiting = state;
	}

	/**
	 * 显示查询中popwin
	 * @author chenchen
	 */
	private void doLongPress() {
		if (mLongClickGeo == null)
			return;

		String caption = "地图上的点";
		mLongClickPoi = new Poi();
		mLongClickPoi.setName(caption);
		mLongClickPoi.setCoord(mLongClickGeo);
		mLongClickPoi.setType(Poi.PoiType.NORMAL);

		showLongClickDiag();
		setLongPressWaiting(true);

		if(mMainActivity == null){
			return;
		}
		LocBtnManager locBtnMgr = LocBtnManager.getInstance(mMainActivity);

		LocationStatus locStatus = mLocCtrl.getLocationStatus();
		if (locStatus == LocationStatus.LOCATING) {
			// 正在定位过程中，有drag事件操作，定位完成后，将恢复至普通状态
			locBtnMgr.gotoBrows();
		}
		if (locStatus == LocationStatus.BROWS) {
			return;
		}
		if (locStatus == LocationStatus.NAV || locStatus == LocationStatus.FOLLOW) {
			locBtnMgr.gotoBrows();
		}
	}

	private void showLongClickDiag() {
//		if(mLongPressOver != null){
//			MapViewOverLay.getInstance().removePoint(mLongPressOver, LONGPRESSPOINTLAYER) ;
//		}
//		mLongPressOver = MapViewOverLay.getInstance().createOverPoint(mLongClickGeo, R.mipmap.pop_immap_2, false);
//		if(mLongPressOver == null){
//			return;
//		}
//		Pixel pixel = SysUtils.getMapCtrl().get3in4Pix();
//		if(mLongClickGeo != null){
//			com.sogou.map.mobile.engine.core.Coordinate enginTarget
//			     = new com.sogou.map.mobile.engine.core.Coordinate(mLongClickGeo.getX(), mLongClickGeo.getY());
//			SysUtils.getMapCtrl().moveTo(enginTarget, pixel, true);
//		}
//		MapViewOverLay.getInstance().addPoint(mLongPressOver, LONGPRESSPOINTLAYER, 0);
//
//		Dialog dialog;
//		dialog = new Dialog.Builder(mMainActivity).setTitle("上传您的当前位置坐标信息")
//				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int arg1) {
//						 dialog.dismiss();
//					}
//				}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(final DialogInterface dialog, int which) {
//						if(mLongClickGeo != null){
//							Log.e("luqingchao", "mLongClickGeo.getX()----" + mLongClickGeo.getX() +
//									"  mLongClickGeo.getY() " + mLongClickGeo.getY());
//						}
//					    dialog.dismiss();
//
//					}
//				}).create();
//		Window window = dialog.getWindow();
//		window.setGravity(Gravity.TOP);   //window.setGravity(Gravity.BOTTOM);
//		dialog.show();
//		dialog.setCanceledOnTouchOutside(true);

		//获取最新当前坐标
//		float lat = mLongClickGeo.getX() ;
//		float lon = mLongClickGeo.getY() ;
//		double []loc = LocationUtils.getLocationInfo(new Coordinate(lat, lon));
//		NewsParams newsParams = new NewsParams(loc[0], loc[1]);
//
//		Gson gson = new Gson();
//		RequestParams params = new RequestParams();
//		String json = newsParams.getParameterString().toString();
//		StringEntity entity = null;
//		try {
//			entity = new StringEntity(json);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		params.setBodyEntity(entity);
//
//		HttpUtils http = new HttpUtils();
//		http.configTimeout(10000);
//		http.send(HttpRequest.HttpMethod.POST, DConfig.IP_PORT, params, new RequestCallBack() {
//
//			@Override
//			public void onSuccess(ResponseInfo responseInfo) {
//				Toast.makeText(SysUtils.getMainActivity(), "发生成功", Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void onFailure(HttpException e, String s) {
//				Toast.makeText(SysUtils.getMainActivity(), "发生失败", Toast.LENGTH_SHORT).show();
//			}
//		});

	}


	private void onDragOcurred(int fingerCount, int x, int y, double d, double d1) {
	}


	@Override
	public void onLocationZChanged(double arg0) {
		if (!mZoomed) {
			mZoomed = true;
			onZoomOcurred();
		}
	}



	private void onZoomOcurred() {
	}




	@Override
	public void onMapStatusChangeFinished(MapStatus status) {

	}


	@Override
	public void onMapStatusChangeStart(MapStatus status) {
	}


	@Override
	public void onMapStatusChanging(MapStatus arg0) {
	}

	/***
	 * 通知地图静止
	 */


	@Override
	public void onLocationXYChanged(double arg0, double arg1) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onRotateXChanged(double arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onRotateZChanged(double arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onFlingOver() {
		// TODO Auto-generated method stub

	}


	@Override
	public void onTouchDown(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onTouchMove(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}


	public boolean isDialogShow() {
//		if(mQuitDialog != null && mQuitDialog.isShowing()){
//			return true;
//		}
		return false;
	}


	public void dismissDialog() {
//		if(mQuitDialog != null && mQuitDialog.isShowing()){
//			 mQuitDialog.dismiss();
//		}

	}

}
