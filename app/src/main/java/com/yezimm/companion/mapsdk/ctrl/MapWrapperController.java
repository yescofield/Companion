package com.nicenearby.sogou.discovery.mapsdk.ctrl;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.RelativeLayout;

import com.nicenearby.sogou.discovery.R;
import com.nicenearby.sogou.discovery.mapsdk.location.LocationInfo;
import com.nicenearby.sogou.discovery.utils.SysUtils;
import com.nicenearby.sogou.discovery.utils.ViewUtils;
import com.nicenearby.sogou.discovery.view.main.MainActivity;
import com.sogou.map.mobile.engine.core.Camera;
import com.sogou.map.mobile.engine.core.Camera.ICameraListener;
import com.sogou.map.mobile.engine.core.ECityInfo;
import com.sogou.map.mobile.engine.core.MapController.AnimationListener;
import com.sogou.map.mobile.engine.core.MapController.AnimationType;
import com.sogou.map.mobile.engine.core.MapGesture;
import com.sogou.map.mobile.engine.core.MapView;
import com.sogou.map.mobile.engine.core.MapView.MapViewListener;
import com.sogou.map.mobile.engine.core.MapViewStatusChangeListener;
import com.sogou.map.mobile.engine.core.Pixel;
import com.sogou.map.mobile.geometry.Bound;
import com.sogou.map.mobile.geometry.Coordinate;
import com.sogou.map.mobile.geometry.Point;

/**
 * 主要功能是转移把所有关于地图的操作从MainActivity中转移过来，避免前者过大 所有与地图相关的放大缩小，旋转，gps的控制，等等，都由该类实现。
 * 原来与mapview
 * ,engineMapview,gpsview,mapcontroller,enginemapcontroller相关的，全部由该类负责.
 * 该类整个程序只有一个，由MainActivity持有，其他需要操作map的函数，不要直接持有activity，可通过该类来进行.
 * 该类不进行任何逻辑控制，只对地图进行操作。
 */
public class MapWrapperController {

	/********************导航时的动画时长**********************************/
	private static final int ANIM_TIME_INTERAL_DEFAULT = 90;


	private static final int ANIM_TIME_FOLLOW_OR_NAV_INTERAL = 600;

	private Bitmap GpsImgResource;

	/**
	 * 实景图是否在显示,且为竖屏，如果为true,光速跟踪模式下，我的位置为屏幕十分之一，目前的做法是导航出实景图时，地图会重新resize一下，不是特别合理
	 */
	public boolean isPortalAndGarmenShow;

	public static int ANIM_TIME = 260;

	public static final int ECITY_MIN_ZOOM = 10;
	public static final int ECITY_MAX_ZOOM = 17;
	public static final int MAX_ZOOM = 18;
	public static final int MIN_ZOOM = 0;
	private Camera mEngineCamera;
	private com.sogou.map.mobile.engine.core.MapView mEngineMapView;
	private com.sogou.map.mobile.engine.core.MapController mEngineMapController;
	private com.sogou.map.mobile.engine.core.GpsView mEngineGpsView;

	//地图显示区域margin
	private int mMarginLeft = 0;
	private int mMarginTop = 0;
	private int mMarginRight = 0;
	private int mMarginButtom = 0;


	private boolean mIsMapInited = false;

	public MapWrapperController(MapView mapView) {
		mEngineMapView = mapView;
		mEngineMapController = mEngineMapView.getController();
		mEngineCamera = mEngineMapView.getCamera();
		mEngineGpsView = mEngineMapView.getGpsView();
		mEngineMapView.setDebugMode(false);
//		mEngineMapView.getUISettings().getScaleBar().setVisibility(View.VISIBLE);
		GpsImgResource = null;
	}

	public void setStreetMapEnable(boolean enable){
		mEngineMapView.enabledStreetMap(enable);
	}


	public com.sogou.map.mobile.engine.core.MapView getEngineMapView() {
		return mEngineMapView;
	}

	/**
	 * 判断地图是否初始化完成
	 *
	 * @return
	 */
	public boolean isMapInited() {
		return mIsMapInited;
	}

	public void setMapInited(boolean isMapinited){
		mIsMapInited = isMapinited;
	}


	/**
	 * mo the sdk coor with animation
	 *
	 * @param sdkCoor
	 * @param smooth
	 */
	public void moveTo(com.sogou.map.mobile.geometry.Coordinate sdkCoor, boolean smooth) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		com.sogou.map.mobile.engine.core.Coordinate engineCoor = new com.sogou.map.mobile.engine.core.Coordinate(sdkCoor.getX(),
				sdkCoor.getY());
		moveTo(engineCoor, smooth);
	}
	///////////////////////////////////////////////
	/////具有回调接口//////////////////////////////////
	public void moveTo(com.sogou.map.mobile.geometry.Coordinate sdkCoor, Pixel pix, int animationType, long time, AnimationListener l){
		com.sogou.map.mobile.engine.core.Coordinate engineCoor = new com.sogou.map.mobile.engine.core.Coordinate(sdkCoor.getX(),
				sdkCoor.getY());
		mEngineMapController.moveTo(engineCoor, pix, animationType, time, l);
	}

	public void moveTo(com.sogou.map.mobile.engine.core.Coordinate engineCoor, Pixel pix, int animationType, long time, AnimationListener l){
		mEngineMapController.moveTo(engineCoor, pix, animationType, time, l);
	}

	public void zoomTo(int targetLayer, int animationType, long time, AnimationListener l){
		mEngineMapController.zoomTo(targetLayer, animationType, time, l);
	}

	public void rotateZTo(double targetRotate, boolean nearest,int animationType, long time, AnimationListener l){
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineMapController.rotateZTo(targetRotate, nearest, animationType, time, l);
//		mEngineMapController.rotateZTo(targetRotate, animationType, time, l);
	}

	//////////////////////////////////////////////////
	/**
	 * move the map to the location and zoom to a proper level with animation
	 *
	 * @param sdkCoor
	 * @param lvl
	 */
	public void moveTo(Coordinate sdkCoor, int lvl) {
		if(sdkCoor == null){
			return;
		}
//			mEngineMapController.moveTo(sdkCoor, lvl);
		com.sogou.map.mobile.engine.core.Coordinate coor = new com.sogou.map.mobile.engine.core.Coordinate();
		coor.setX(sdkCoor.getX());
		coor.setY(sdkCoor.getY());
		mEngineMapController.moveTo(coor, getCenterPix(), true, ANIM_TIME);
		mEngineMapController.zoomTo(lvl, true, ANIM_TIME);
	}


	/**
	 * @param targetLayer
	 * @param time
	 * @param smooth
	 */
	public void zoomTo(double targetLayer, boolean smooth, long time) {
		mEngineMapController.zoomTo(targetLayer, smooth, time);
	}

	/**
	 * mo the locaiton to the center of the screen
	 *
	 * @param enginTarget
	 */
	public void moveTo(com.sogou.map.mobile.engine.core.Coordinate enginTarget) {
		moveTo(enginTarget, true);
	}

	/**
	 * move the location to a the center with or not animation
	 *
	 * @param enginTarget
	 * @param smooth
	 */
	public void moveTo(com.sogou.map.mobile.engine.core.Coordinate enginTarget, boolean smooth) {
		mEngineMapController.moveTo(enginTarget, getCenterPix(), smooth, ANIM_TIME);
	}

	/**
	 * move the location to the anchor with or not animation
	 *
	 * @param enginTarget
	 * @param anchor
	 * @param smooth
	 */
	public void moveTo(com.sogou.map.mobile.engine.core.Coordinate enginTarget, Pixel anchor, boolean smooth) {
		mEngineMapController.moveTo(enginTarget, anchor, smooth, ANIM_TIME);
	}
	public void moveTo(com.sogou.map.mobile.engine.core.Coordinate enginTarget, Pixel anchor, boolean smooth, long time) {
		mEngineMapController.moveTo(enginTarget, anchor, smooth, time);
	}
	/**
	 * move the location to the anchor
	 *
	 * @param enginTarget
	 * @param anchor
	 */
	public void moveTo(com.sogou.map.mobile.engine.core.Coordinate enginTarget, Pixel anchor) {
		moveTo(enginTarget, anchor, true);
	}


	/**
	 * move the location to the anchor and zoom to a proper level
	 *
	 * @param loc
	 * @param anchor
	 * @param
	 */

	public void moveAndZoomMap(LocationInfo loc, Pixel anchor) {
		if (loc != null) {
			Point pnt = loc.getLocation();
			com.sogou.map.mobile.engine.core.Coordinate coor = new com.sogou.map.mobile.engine.core.Coordinate(pnt.getX(), pnt.getY());
			mEngineMapController.moveTo(coor, anchor, true, ANIM_TIME);
		}
	}

	/** 目前只支持X和Z **/
	public void setEnableRotate(char type, boolean enable) {
		switch (type) {
			case 'x':
				mEngineMapView.getGesture().setEnableRotateX(enable);
				break;

			case 'z':
				mEngineMapView.getGesture().setEnableRotateZ(enable);
				break;
		}
	}

	/**
	 * move the location to the anchor and rotate it to a degree
	 *
	 * @param loc
	 * @param anchor
	 * @param degree
	 */
	public void moveAndRotateMap(LocationInfo loc, Pixel anchor, float degree) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineMapController.rotateZTo(degree, true, true, ANIM_TIME);
		if (loc != null) {
			Point pnt = loc.getLocation();
			com.sogou.map.mobile.engine.core.Coordinate coor = new com.sogou.map.mobile.engine.core.Coordinate(pnt.getX(), pnt.getY());
			mEngineMapController.moveTo(coor, anchor, true, ANIM_TIME);
		}
	}

	public void RotateMapWhenDirectionChange(LocationInfo loc, Pixel anchor, float degree){
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineMapController.rotateZTo(degree, true, true, ANIM_TIME);
	}

	public void rotateMap(float degree) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineMapController.rotateZTo(degree, true, true, ANIM_TIME);
	}

	public void rotateMap(float degree, long time) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineMapController.rotateZTo(degree, true, true, time);
	}

	public void resetTo2DMap(boolean smooth){
		double degreeX = mEngineMapView.getCamera().getRotateX();
		double degreeZ = mEngineMapView.getCamera().getRotateZ();
		if(degreeX != 0 || degreeZ != 0){
			if (smooth) {
				mEngineMapController.occupyBefore();
				mEngineMapController.rotateXTo(0, true, ANIM_TIME);
				mEngineMapController.rotateZTo(0, true, true, ANIM_TIME);
				double level = mEngineMapView.getCamera().getCurrentLayer();
				mEngineMapController.zoomTo(level, true, ANIM_TIME);
				Pixel centerPixel = new Pixel(mEngineMapView.getWidth()/2, mEngineMapView.getHeight()/2);
				com.sogou.map.mobile.engine.core.Coordinate centerGeo = mEngineMapView.getCamera().rayGround(centerPixel);
				mEngineMapController.moveTo(centerGeo, centerPixel, true, ANIM_TIME);
				mEngineMapController.occupyAfter();
			} else {
				mEngineMapController.rotateXTo(0, false, 0);
				mEngineMapController.rotateZTo(0, false, true, 0);
			}
		}
	}

	public void roateZToWithOutAnimtion(){
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineMapController.rotateZTo(0, false, true, 0);
	}

	////////////////////////////////////////////
	boolean isResetTo2Dmap = false;
	public boolean isResetTo2DMap(){
		return isResetTo2Dmap;
	}

	public void setResetTo2DMap(boolean resetTo2dMap){
		isResetTo2Dmap = resetTo2dMap;
	}
	////////////////////////////////////////////
	public boolean is2DCameraView(){
		boolean v = false;
		if( mEngineMapView.getCamera().getRotateX() == 0){
			v = true;
		}
		return v;
	}

	public void rotateMap(float degree, Pixel anchor) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineMapController.rotateZTo(degree, true, true, ANIM_TIME);
		if (anchor != null) {
			rayGround(anchor);
			com.sogou.map.mobile.engine.core.Coordinate coor = rayGround(anchor);
			mEngineMapController.moveTo(coor, anchor, true, ANIM_TIME);
		}
	}

	public void rotateGps(float degree) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineGpsView.rotateTo(degree, true, true, ANIM_TIME_INTERAL_DEFAULT);
		setGpsVisibility(true);
	}


	public void setMapCenter(Coordinate mercator) {
		mEngineMapView.setCenter(mercator.getX(), mercator.getY());
	}

	public void zoomTo(Bound b) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		zoomTo(b,0,0,0,0);
	}

	public void zoomTo(int level, boolean smooth) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineMapController.zoomTo(level, smooth, ANIM_TIME);
	}

	/**
	 * 调整地图到指定的视野范围内
	 * @param  bound: 视野范围
	 * @param  width: 目标区域宽度(Pixel)
	 * @param  height: 目标区域高度(Pixel)
	 * @param  marginLeft: 地图的左留白
	 * @param  marginTop: 地图的上留白
	 * @param  marginRight: 地图的右留白
	 * @param  marginBottom: 地图的下留白
	 * @param  LimitLevel: 用户指定的放大级别上线
	 * @author renjie
	 * @date 2013.6.18
	 * */
	public void zoomToBound(Bound bound, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom, int LimitLevel,boolean smooth) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		int currentLevel = (int)mEngineMapView.getZoom();
		int containerWidth = width;
		int containerHeight = height;
		double levelAfterZoom = getLevelByBound(bound, containerWidth, containerHeight);
		int levelAfter = (int)levelAfterZoom;
		Pixel anchor = new Pixel((width / 2) + marginLeft, (height / 2) + marginTop);
		Coordinate center = bound.getCenter();
		com.sogou.map.mobile.engine.core.Coordinate coor_x_y = new com.sogou.map.mobile.engine.core.Coordinate(center.getX(), center.getY());
		if(levelAfter > LimitLevel){
			zoomTo(LimitLevel,smooth);
			moveTo(coor_x_y, anchor, smooth);
		}else{
			if(levelAfter == currentLevel){
				moveTo(coor_x_y, anchor, smooth);
			}else{
				zoomTo(levelAfter, false);
				moveTo(coor_x_y, anchor, smooth);
			}
		}
	}

	public com.sogou.map.mobile.engine.core.Coordinate rayGround(Pixel pix) {
		return mEngineCamera.rayGround(pix);
	}

	public com.sogou.map.mobile.engine.core.Coordinate rayGround(double pixX, double pixY) {
		return mEngineCamera.rayGround(pixX, pixY);
	}

//	public Layer getLayerByIndex(int idx) {
//		return mSdkMapview.getLayerByIndex(idx);
//	}

	/*****************************************************************/
	/**
	 * add MapListerner/MapViewListener/GestureListener/CameraLIistener to the
	 * map
	 *
	 * @param listener
	 */
	public void addMapListener(Object listener) {
		if (listener == null) {
			return;
		}
		boolean valid = false;
		if (listener instanceof MapViewListener) {
			mEngineMapView.addMapViewListener((MapViewListener) listener);
			valid = true;
		}
		if (listener instanceof MapGesture.IListener) {
			mEngineMapView.getGesture().addListener((MapGesture.IListener) listener);
			valid = true;
		}
		if (listener instanceof ICameraListener) {
			mEngineCamera.addListener((ICameraListener) listener);
			valid = true;
		}

		if (listener instanceof MapViewStatusChangeListener) {
			mEngineMapView.addMapStatusChangeListener((MapViewStatusChangeListener) listener);
			valid = true;
		}
		if (!valid) {
			throw new RuntimeException("bad map listener to add:" + listener.getClass().getName());
		}
	}

	/**
	 * remove MapListerner/MapViewListener/GestureListener/CameraLIistener to
	 * the map
	 *
	 * @param listener
	 */
	public void removeMapListener(Object listener) {
		if (listener == null) {
			return;
		}
		boolean valid = false;
		if (listener instanceof MapViewListener) {
			mEngineMapView.removeMapViewListener((MapViewListener) listener);
			valid = true;
		}
		if (listener instanceof MapGesture.IListener) {
			mEngineMapView.getGesture().removeListener((MapGesture.IListener) listener);
			valid = true;
		}
		if (listener instanceof ICameraListener) {
			mEngineCamera.removeListener((ICameraListener) listener);
			valid = true;
		}

		if (listener instanceof MapViewStatusChangeListener) {
			mEngineMapView.removeMapStatusChangeListener((MapViewStatusChangeListener) listener);
			valid = true;
		}

		if (!valid) {
			throw new RuntimeException("bad map listener to remove:" + listener.getClass().getName());
		}
	}

	/***************************************************************************/
	/**
	 * convertMercatorToPixel
	 *
	 * @param
	 */
	public Point mercator2pix(Coordinate sdkCoor) {
		Pixel pixel = mEngineCamera.rayScreen(new com.sogou.map.mobile.engine.core.Coordinate(sdkCoor.getX(),sdkCoor.getY(),0));
		return new Point((float)pixel.getX(), (float)pixel.getY());
	}

	public Pixel mercator2Pixel(Coordinate sdkCoor) {
		return mEngineCamera.rayScreen(new com.sogou.map.mobile.engine.core.Coordinate(sdkCoor.getX(), sdkCoor.getY(), 0.0D));
	}

	/**
	 * pix2mercator
	 *
	 * @param
	 */
	public Coordinate pix2mercator(Point pt) {
		if(pt == null){
			return null;
		}
		com.sogou.map.mobile.engine.core.Coordinate coor = mEngineCamera.rayGround(pt.getX(), pt.getY());
		return new Coordinate((float)coor.getX(),(float)coor.getY(),(float)coor.getZ());
	}

	/**
	 * get the pixel of the map center
	 *
	 * @return
	 */
	public Point getMapCenterPix() {
		Point point = new Point(mEngineMapView.getWidth()/2f,mEngineMapView.getHeight()/2f);
		return point;
	}

	/**
	 * get the pixel of the map center
	 *
	 * @return
	 */
	public com.sogou.map.mobile.engine.core.Coordinate getMapScreenCenter() {
		return mEngineMapView.getCamera().getScreenCenter();
	}

	public Coordinate getMapCenterMercator() {
		return new Coordinate((int)(mEngineCamera.getScreenCenter().getX()), (int)(mEngineCamera.getScreenCenter().getY()));
	}

	/**
	 * get the width of the map
	 *
	 * @return
	 */
	public int getMapW() {
		return mEngineMapView.getWidth();
	}

	/**
	 * get the height of the map
	 *
	 * @return
	 */
	public int getMapH() {
		return mEngineMapView.getHeight();
	}

	public boolean isTouching() {
		return false;
	}







	public int getLayerVisibleState() {
		int engineState = mEngineMapView.getLayerVisableState();
		int oldState = 0;

		if( (engineState&com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_SATELLITE) != 0){
			oldState |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_SATELLITE;
		}
		if( (engineState&com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_SATELLITE_ROAD) != 0){
			oldState |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_SATELLITE_ROAD;
		}
		if( (engineState&com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_TRANFIC) != 0){
			oldState |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_TRANFIC;
		}
		if( (engineState&com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_ECITY) != 0){
			oldState |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_ECITY;
		}
		if( (engineState&com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_IMAGE) != 0){
			oldState |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_IMAGE;
		}
		return oldState;
	}

	public void setLayerVisibleState(int layerState) {
		int engineLayerVisable = 0;
		if(isLayerVisable(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_GEO, layerState)){
			engineLayerVisable |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_GEO;
		}
		if(isLayerVisable(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_SATELLITE, layerState)){
			engineLayerVisable |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_SATELLITE;
		}
		if(isLayerVisable(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_SATELLITE_ROAD, layerState)){
			engineLayerVisable |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_SATELLITE_ROAD;
		}
		if(isLayerVisable(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_TRANFIC, layerState)){
			engineLayerVisable |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_TRANFIC;
		}
		if(isLayerVisable(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_ECITY, layerState)){
			engineLayerVisable |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_ECITY;
		}
		if(isLayerVisable(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_ECITY, layerState)){
			engineLayerVisable |= com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_ECITY;
		}
		mEngineMapView.setLayerVisable(engineLayerVisable, true,true);
	}
	boolean isLayerVisable(int  layerType,int visableState) {
		if (layerType == com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_GEO) {
			return !isLayerVisable(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_SATELLITE,visableState)
					&& !isLayerVisable(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_ECITY,visableState)
					&& !isLayerVisable(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_IMAGE,visableState);
		}
		return (visableState & layerType) != 0;
	}

	public void setLayerVisible(int layer, boolean visible) {
		mEngineMapView.setLayerVisable(layer, visible);
	}

	public void setLayerVisible(int layer, boolean visible,boolean isSingleMode) {
		mEngineMapView.setLayerVisable(layer, visible, isSingleMode);
	}

	public boolean isLayerVisible(int layer) {
		return mEngineMapView.isLayerVisable(layer);
	}

	public int getLevelByBound(Bound b) {
		return  (int) getLevelByBound(b, getMapW(), getMapH());
	}

	/**
	 * 根据像素区域，获得与制定Bound相匹配的地图级别
	 * @author renjie
	 * @date 2013.9.3
	 * */
	public double getLevelByBound(Bound b, int withPix, int heightPix) {
		float w = Math.abs(b.getMaxX() - b.getMinX());
		float h = Math.abs(b.getMaxY() - b.getMinY());
		double levelW = calculateLevel(w, withPix);
		double levelH = calculateLevel(h, heightPix);
		double lvl = Math.min(levelW, levelH);
		return lvl;
	}

	public int getLevelByBoundWithSDK(Bound b, int withPix, int heightPix){
		return (int) getLevelByBound(b, withPix, heightPix);

	}

	/**
	 * 根据指定的视野、边缘空白，获得地图级别。
	 * @author renjie
	 * @date 2013.6.24
	 * */
	public double getLevelByBound(Bound bound, int marginLeft, int marginTop, int marginRight, int marginBottom){
		int mapWidth = getMapW();
		int mapHeight = getMapH();
		int containerWidth = mapWidth - marginLeft - marginRight;
		int containerHeight = mapHeight - marginTop - marginBottom;
		double levelAfterZoom = getLevelByBound(bound, containerWidth, containerHeight);

		return levelAfterZoom;
	}

	public void setECityInfo(ECityInfo ecity) {
		mEngineMapView.setECityInfo(ecity);
	}

//	public View getPop() {
//		return mSdkMapview.getPop();
//	}

	public double getPixelGeoLength() {
		if(mEngineCamera == null){
			return 0;
		}
		return mEngineCamera.getPixelGeoLength();
	}

	public Pixel rayScreen(com.sogou.map.mobile.engine.core.Coordinate engineCoor) {
		if(mEngineCamera == null){
			return null;
		}
		return mEngineCamera.rayScreen(engineCoor);
	}


	public void moveGpsTo(LocationInfo location) {
		if(location == null || location.getLocation() == null){
			return;
		}
		com.sogou.map.mobile.engine.core.Coordinate coord = new com.sogou.map.mobile.engine.core.Coordinate(location.getLocation().getX(),location.getLocation().getY());
		mEngineGpsView.moveTo(coord, true, ANIM_TIME);
		setGpsVisibility(true);
	}



	/**************************************/

//	public void clearCache() {
//		mEngineMapView.clearCache();
//	}

	public Bound getBound() {
		Bound bound = new Bound();
		int width = getMapW();
		int height = getMapH();
		com.sogou.map.mobile.engine.core.MapView engineMapView = mEngineMapView;
		int skyHeight = (int)(engineMapView.getCamera().getSkyBoxHeightRatio()*(double)height);
		ArrayList<Coordinate> verts = new ArrayList<Coordinate>();
		verts.add(convertPixelToMercator(0, skyHeight));
		verts.add(convertPixelToMercator(width, skyHeight));
		verts.add(convertPixelToMercator(width, height));
		verts.add(convertPixelToMercator(0,height));
		float minX = 0;
		float minY = 0;
		float maxX = 0;
		float maxY = 0;
		boolean first = true;
		for(Coordinate coor : verts){
			float coorX = coor.getX();
			float coorY = coor.getY();
			if(first){
				minX = maxX = coorX;
				minY = maxY = coorY;
				first = false;
			} else {
				if(coorX < minX){
					minX = coorX;
				}
				if(coorY < minY){
					minY = coorY;
				}
				if(coorX > maxX){
					maxX = coorX;
				}
				if(coorY > maxY){
					maxY = coorY;
				}
			}
		}

		bound.setMinX(minX);
		bound.setMaxX(maxX);
		bound.setMinY(minY);
		bound.setMaxY(maxY);
		return bound;
	}

	Coordinate convertPixelToMercator(float x,float y){
		com.sogou.map.mobile.engine.core.Coordinate coor = mEngineCamera.rayGround(x, y);
		return new Coordinate((float)coor.getX(),(float)coor.getY(),(float)coor.getZ());
	}

	public Bound getBound2D(){
		return getBound();
	}

	public double getCameraLayer() {
		return mEngineCamera.getCurrentLayer();
	}

	public int getZoom() {
		return (int) mEngineMapView.getZoom();
	}

	public void setZoomMax(int level) {
		mEngineCamera.setZoomMax(level);
	}

	public int getZoomMax() {
		return (int) mEngineCamera.getZoomMax();
	}

	public void setZoomMin(int level) {
		mEngineCamera.setZoomMin(level);
	}

	public int getZoomMin() {
		return (int) mEngineCamera.getZoomMin();
	}

//	public void setLogoType(int logoType) {
//		mSdkMapview.setLogoType(logoType);
//	}

	public void refreshMap() {
//		mEngineMapView.refreshMap();
	}

//	public void clearTileViewCache() {
//		mEngineMapView.clearCache();
//	}

	/**
	 * 设置比例尺显示位置 以及比例尺文字的位置
	 * 注意:底边距 要比定位按钮多出 定位按钮背景图片的透明边距 的大小
	 * @author daweiliu 2013-5-17
	 * @param logoParams
	 * @param textAlginLeft
	 */
	public void setLogoLayoutParam(RelativeLayout.LayoutParams logoParams, boolean textAlginLeft) {
		// mapsdk底层LogoLayer底部有一个8dp的padding值
		logoParams.bottomMargin = logoParams.bottomMargin - ViewUtils.getPixel(SysUtils.getMainActivity(), 8);
//		mEngineMapView.setLogoLayoutParam(logoParams, textAlginLeft);
//		mEngineMapView.getUISettings().getScaleBar().setLayoutParams(logoParams);
		mEngineMapView.setScaleBarLayoutParam(logoParams, textAlginLeft);
	}

	public void setCompassStyle(int margin) {
		MainActivity context = SysUtils.getMainActivity();
		if(context == null){
			return;
		}

		RelativeLayout.LayoutParams rateParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rateParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
		rateParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		rateParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		int gpsWidth = BitmapFactory.decodeResource(context.getResources(), R.mipmap.toolbar_background2).getWidth();
		int dp6 = ViewUtils.getPixel(context, 6);
		rateParam.setMargins(gpsWidth + 2 * (int)context.getResources().getDimension(R.dimen.common_map_button_margin), 0, 0, dp6  + (int)context.getResources().getDimension(R.dimen.common_map_button_margin) + ViewUtils.getPixel(SysUtils.getMainActivity(), 3));
		setLogoLayoutParam(rateParam, true);// 文字左对齐
	}

	public void setEnginMapSize(int width, int height) {
		mEngineMapView.setSize(width, height);
	}

	public void setPreloadMode(int mode) {
		mEngineMapView.setPreloadMode(mode);
	}

	public int getCurrentLayerId() {
		int level = (int) mEngineMapView.getZoom();
		if (level == 18) {
			return 792;
		} else {
			return 728 - level;
		}
	}

	public int getLowerLayerId() {
		int level = (int) mEngineMapView.getZoom();
		if (level == 18) {
			return 792;
		} else {
			level++;
			if (level == 18) {
				return 792;
			} else {
				return 728 - level;
			}
		}
	}

	public float getTileGeoOriWidth(int level) {
		return (float) mEngineCamera.getTileGeoWidthForLevel(level);
//		return mSdkMapview.getTileGeoOriWidth();
	}

	public float getTileGeoOriHeight(int level) {
		return (float) mEngineCamera.getTileGeoHeightForLevel(level);
//		return mSdkMapview.getTileGeoOriHeight();
	}

//	public void removeSmallPointsByTile(Tile tile) {
//		mSdkMapview.removeSmallPointsByTile(tile);
//	}

	public float convertScreenLengthToGeoX(float dis) {
		return (float) (dis * mEngineCamera.getPixelGeoLength());
//		return mSdkMapview.convertScreenLengthToGeoX(dis);
	}

	public float convertScreenLengthToGeoY(float dis) {
		return (float) (dis * mEngineCamera.getPixelGeoLength());
//		return mSdkMapview.convertScreenLengthToGeoY(dis);
	}

	public void zoomTo(Bound bound, int marginLeft, int marginTop, int marginRight, int marginBottom) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		zoomToBound(bound,getMapW(),getMapH(),marginLeft,marginTop,marginRight,marginBottom, 0, true);
//		zoomTo(bound, marginLeft, marginTop, marginRight, marginBottom);
	}

//	public int getGpsHight() {
//		return mEngineGpsView.
//	}

	public void setGpsVisibility(boolean visible) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
		mEngineGpsView.setPointViewVisable(visible);
		if (!visible) {
			mEngineGpsView.setCircleRediosGeo(0);
		}
	}

	/**
	 * 设置精度圈填充色
	 * @param color
	 */
	public void setGpsCircleFillColor(int color){
		mEngineGpsView.setCircleFillColor(color);
	}
	/**
	 * 设置精度圈边框色
	 * @param color
	 */
	public void setGpsCircleEdgeColor(int color){
		mEngineGpsView.setCircleEdgeColor(color);
	}
	/**
	 * 设置精度圈边框宽度
	 * @param
	 */
	public void setGpsCircleEdgePixelWidth(int width){
		mEngineGpsView.setCircleEdgePixelWidth(width);
	}

	public int getGpsVisibility() {
		boolean isvisable = mEngineGpsView.isPointViewVisable();
		int visibility = isvisable ? View.VISIBLE: View.INVISIBLE;
		return visibility;
	}

	public void setGpsImg(Bitmap bitmap) {
		if(bitmap != GpsImgResource){
			GpsImgResource = bitmap;
		}else {
			return;
		}
		try{
			if(bitmap!=null){
				mEngineGpsView.setPointView(bitmap, bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth()/2, bitmap.getHeight()/2);
			}
		}catch(UnsatisfiedLinkError e){
		}catch(OutOfMemoryError e){

		}
	}

	// 3d relative
	public void setGpsShadowImg() {
		Bitmap bitmap = BitmapFactory.decodeResource(SysUtils.getMainActivity().getResources(), R.mipmap.common_location_shadow_3d);
		if(bitmap!=null){
			mEngineGpsView.setPointViewShadow(bitmap, bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth()/2, bitmap.getHeight()/2);
		}
	}

	public void setGpsDirectionVisibility(boolean visible) {
		mEngineGpsView.setDirectionViewVisable(visible);
	}


	public void setGpsDirectionImg() {
		Bitmap bitmap = BitmapFactory.decodeResource(SysUtils.getMainActivity().getResources(), R.mipmap.direction_3d);
		if(bitmap!=null){
			mEngineGpsView.setDirectionView(bitmap, bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth()/2, bitmap.getHeight()/2);
		}
	}

	// 3d relative
	public void setGpsDirectionShdowImg() {

		Bitmap bitmap = BitmapFactory.decodeResource(SysUtils.getMainActivity().getResources(), R.mipmap.direction_shadow_3d);
		if(bitmap!=null){
			mEngineGpsView.setDirectionViewShadow(bitmap, bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth()/2, bitmap.getHeight()/2);
		}
	}

	public void updateGpsDirectionSize() {
		//do nothing.....
	}



	public static Coordinate engineCoordToGeoMetryCoord(com.sogou.map.mobile.engine.core.Coordinate coord){
		Coordinate geo = null;
		if(coord == null){
			return null;
		}
		geo = new Coordinate();
		geo.setX((float) coord.getX());
		geo.setY((float) coord.getY());
		return geo;
	}



	/**
	 * get the 3/4 of the height of the map
	 *
	 * @param
	 * @return
	 */
	public Pixel get3in4Pix() {
		int w = mEngineMapView.getWidth() - mMarginLeft - mMarginRight;
		int h = mEngineMapView.getHeight() - mMarginTop - mMarginButtom;
		double anchorX = mMarginLeft + w / 2;
		double anchorY;
		Pixel pixel = null;
		if(isPortalAndGarmenShow){
			anchorY = mMarginTop + h * 0.78;
			pixel = new Pixel(anchorX, anchorY);
		}else {
			anchorY = mMarginTop + h * 3 / 4;
			pixel = new Pixel(anchorX, anchorY);
		}
		return pixel;
	}

	/**
	 * get the 1/2 of the height of the map
	 *
	 * @param
	 * @return
	 */
	public Pixel getCenterPix() {
		Pixel pixel = null;
		int w = mEngineMapView.getWidth() - mMarginLeft - mMarginRight;
		int h = mEngineMapView.getHeight() - mMarginTop - mMarginButtom;
		pixel =  new Pixel(mMarginLeft + w / 2, mMarginTop + h / 2);
		return pixel;
	}

	/**
	 * 地图显示区域margin
	 * @param marginLeft
	 * @param marginTop
	 * @param marginRight
	 * @param marginButtom
	 * add by huifang 20131205
	 */
	public void setMargin(int marginLeft, int marginTop, int marginRight, int marginButtom) {
		mMarginLeft = marginLeft;
		mMarginTop = marginTop;
		mMarginRight = marginRight;
		mMarginButtom = marginButtom;
	}

	public float getMapRotate() {
		return (float) mEngineCamera.getRotateZ();
	}

	public boolean hasMapRotate() {
		if (isLayerVisible(com.sogou.map.mobile.engine.core.LayerType.LAYERTYPE_ECITY))
			return true;

		return (getMapRotate() != 0 || mEngineCamera.getRotateX() != 0);
	}

	/**
	 * 根据传入的墨卡托长度和屏幕的长度，计算出其所需的地图level
	 *
	 * @param mercatorLen
	 * @param pixLen
	 * @return
	 */
	public double calculateLevel(double mercatorLen, double pixLen) {
		double mercatorPerPix = mEngineCamera.getPixelGeoHeightForLevel(getZoomMax());
		double newMercatorPerPix = mercatorLen / pixLen;
		double times = mercatorPerPix / newMercatorPerPix;
		double deltaLvl = Math.log(times) / Math.log(2);
		double newLvl = getZoomMax() + deltaLvl;
		newLvl = Math.max(getZoomMin(), newLvl);
		newLvl = Math.min(getZoomMax(), newLvl);
		return newLvl;
	}

	/**
	 * 计算每个像素对应多少地理长度(米)
	 */
	public double getMerCatorPerPix(int level){
		return mEngineCamera.getPixelGeoHeightForLevel(level);
	}



	public void moveGpsToConstantSpeed(Coordinate sdkCoor) {
//		if(isInbgOrScreenOff()){
//			return;
//		}
//		if(LocationController.getInstance().isInTrafficDogMode()){
//			mEngineGpsView.moveTo(new com.sogou.map.mobile.engine.core.Coordinate(sdkCoor.getX(),sdkCoor.getY()), true, ANIM_TIME_INTERAL_TRAFFIC_DOG);
//		}else {
		mEngineGpsView.moveTo(new com.sogou.map.mobile.engine.core.Coordinate(sdkCoor.getX(),sdkCoor.getY()), true, ANIM_TIME_INTERAL_DEFAULT);
//		}
		setGpsVisibility(true);
	}

	// 3d relative
	public double getSkyBoxHeightRatio(){
//		return 0;
		return mEngineMapView.getCamera().getSkyBoxHeightRatio();
	}


	// 3d relative
	public double getSkyBoxHeight(){
//		return 0;
		return mEngineMapView.getCamera().getSkyBoxHeightRatio() *  mEngineMapView.getHeight();
	}


	public void UseLowPower(boolean isEnable){
		try{
			if(isEnable){
				mEngineMapView.setPowerLevel(com.sogou.map.mobile.engine.core.MapView.PowerLevel.POWER_LEVEL_LOW);
				// 3d relative
				mEngineMapView.getMapConfig().setAnnotDensity(0.4);
			}else {
				mEngineMapView.setPowerLevel(com.sogou.map.mobile.engine.core.MapView.PowerLevel.POWER_LEVEL_NORMAL);
				// 3d relative
				mEngineMapView.getMapConfig().setAnnotDensity(1);
			}
		}catch(Throwable e){

		}


	}

	/**
	 * 导航过程中是否渲染地图
	 * @param start： true:渲染地图，false:不渲染地图
	 */
	public void startOrStopRender(boolean start){
		if(start){
			mEngineMapView.startRender();
		}else {
			mEngineMapView.stopRender();
		}
	}

	/**
	 * 设置建筑是否在最后两级显示
	 * @ visible 设置为真则显示。否则不显示
	 */
	public void setBuildingVisible(boolean visible){
		mEngineMapView.setBuildingVisible(visible);
	}


	public double getRotateX(){
		double roatex  = mEngineMapView.getCamera().getRotateX();
		return roatex;
	}


	public void setDragMinDistance(int i) {

	}

	public void setLogoType(int logoPhone) {
		mEngineMapView.setLogoType(logoPhone);
	}

	public void setTranficSupportCities(List<String> tranficSupportCities) {
		// TODO Auto-generated method stub

	}

	/**
	 * 设置可见，还是不可见
	 * @param type 0代表不可见，1代表可见
	 */
	public void setMapViewVisiable (int type) {
		if (type == 1)
			mEngineMapView.setVisibility(View.VISIBLE);
		else if (type == 0)
			mEngineMapView.setVisibility(View.GONE);
	}


}
