package com.nicenearby.sogou.discovery.mapsdk.ctrl;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;

import com.nicenearby.sogou.discovery.utils.SysUtils;
import com.sogou.map.mobile.engine.core.MapView;
import com.sogou.map.mobile.engine.core.OverLine;
import com.sogou.map.mobile.engine.core.OverPoint;
import com.sogou.map.mobile.engine.core.OverPolygon;
import com.sogou.map.mobile.engine.core.Pixel;
import com.sogou.map.mobile.engine.core.OverPolygon.Style;
import com.sogou.map.mobile.geometry.Coordinate;

/**
 * 目前的overLay 的子类主要有：
 * OverPoint、 OverLine、 OverPology
 * 理想的做法是将所有的添加点、添加线、添加面
 *              删除点、删除线、删除面 等等操作都放在该类中，其他类就不要再生成方法了，太乱，不然sdk 更改新的实现，满工程找
 * @author luqingchao
 *
 */
public class MapViewOverLay {
	public static final int MAP_LAYER_POLYGON = 2;
	public static final int MAP_LAYER_LINE = 4;
	public static final int MAP_LAYER_SMALL_POINT = 7;
	public static final int MAP_LAYER_POINT = 8;
	public static final int MAP_LAYER_FAVOR = 6;
	//盖住收藏星星
	public static final int MAP_LAYER_PARK = MAP_LAYER_FAVOR + 3;
	public static final int MAP_LAYER_TURN_POINT = MAP_LAYER_PARK + 1;
	//地图图钉图层
	public static final int MAP_LAYER_SELECTED_POINT = MAP_LAYER_PARK + 2;

	public static final int MAP_LAYER_ANNOTATION_VIEW = 100;

	private static MapViewOverLay instance;

	private static MapWrapperController mMapCtrl;
	private static MapView mEngineMapView;

	public static MapViewOverLay getInstance(){
		if(instance == null){
			mMapCtrl = SysUtils.getMapCtrl();
			if(mMapCtrl != null){
				mEngineMapView = mMapCtrl.getEngineMapView();
			}
			instance = new MapViewOverLay();
		}
		return instance;
	}


	/**
	 * create the point feature, up to the center geo
	 *
	 * @param geo
	 * @param imgId
	 * @return
	 */
	public  OverPoint createOverPoint(Coordinate geo, int imgId, boolean center) {
		Bitmap bitmap = BitmapFactory.decodeResource(SysUtils.getMainActivity().getResources(), imgId);
		com.sogou.map.mobile.engine.core.Coordinate coord = new com.sogou.map.mobile.engine.core.Coordinate();
		coord.setX(geo.getX());
		coord.setY(geo.getY());
		int offY = bitmap.getHeight();
		if(center){
			offY = bitmap.getHeight() / 2;
		}
		OverPoint overPoint =  new OverPoint(coord, new Pixel(bitmap.getWidth() / 2 ,offY), bitmap);
		overPoint.attach = coord;
		return overPoint;
	}

	public  OverPoint createOverPoint(com.sogou.map.mobile.engine.core.Coordinate shapePoint, int width,
									  int height, int offx, int offY, View view,Context context) {
		try{
			//启用绘图缓存
			view.setDrawingCacheEnabled(true);
			//调用下面这个方法非常重要，如果没有调用这个方法，得到的bitmap为null
			view.measure(MeasureSpec.makeMeasureSpec(256, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(256, MeasureSpec.UNSPECIFIED));
			//这个方法也非常重要，设置布局的尺寸和位置
			view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
			//获得绘图缓存中的Bitmap
			view.buildDrawingCache();
			Bitmap bitmap = view.getDrawingCache();
			OverPoint overPoint = new OverPoint(shapePoint, new Pixel(-offx,-offY), bitmap);
			overPoint.attach = shapePoint;
//
//			if (view != null) {
//				view.destroyDrawingCache();
//				view.setDrawingCacheEnabled(false);
//			}
			return overPoint;
		}catch(Exception e){
			return null;
		}

	}

	/**
	 * create the point feature, up to the center geo
	 *
	 * @param geo
	 * @param
	 * @return
	 */
	public  OverPoint createOverPoint(Coordinate geo, Bitmap image, boolean center) {
//		Point p = new Point(geo.getX(), geo.getY(), 0, null);
//		Drawable image = SysUtils.getApp().getResources().getDrawable(imgId);
//		StateListDrawable styles = com.sogou.map.mobile.mapsdk.ui.android.Style.createPointStyle(image, image, image);
//		PointFeature pf = new PointFeature(Long.toHexString(System.currentTimeMillis()), p, styles, -image.getIntrinsicWidth() / 2,
//		        -image.getIntrinsicHeight(), SysUtils.getApp());
//		if (SysUtils.checkCoordinateEmpty(geo)){
//			return null;
//		}
//		Bitmap bitmap = BitmapFactory.decodeResource(SysUtils.getApp().getResources(), imgId);
		com.sogou.map.mobile.engine.core.Coordinate coord = new com.sogou.map.mobile.engine.core.Coordinate();
		coord.setX(geo.getX());
		coord.setY(geo.getY());
		int offY = image.getHeight();
		if(center){
			offY = image.getHeight() / 2;
		}
		OverPoint overPoint = new OverPoint(coord, new Pixel(image.getWidth() / 2 ,offY), image);
		overPoint.attach = coord;
		return overPoint;
	}

	public  OverPoint createOverPoint(Coordinate geo, Drawable d, int offsetx, int offsetY) {
		BitmapDrawable bitmapdrawable = (BitmapDrawable)d;
		com.sogou.map.mobile.engine.core.Coordinate coord = new com.sogou.map.mobile.engine.core.Coordinate();
		coord.setX(geo.getX());
		coord.setY(geo.getY());
		OverPoint overPoint = new  OverPoint(coord, new Pixel(-offsetx ,-offsetY), bitmapdrawable.getBitmap());
		overPoint.attach = coord;
		return overPoint;
	}





	public void addPoint(OverPoint pf) {
		try{
			int order = mEngineMapView.getOverlayLayer().getOverlaySize(MapViewOverLay.MAP_LAYER_POINT);
			pf.setOrder(order);
			mEngineMapView.getOverlayLayer().addOverlay(MapViewOverLay.MAP_LAYER_POINT, pf);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void addPoint(OverPoint pf, int layer, int order) {
		try{
			pf.setOrder(order);
			mEngineMapView.getOverlayLayer().addOverlay(layer, pf);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void addPoint(OverPoint pf, int layer, int order, int minlevel, int maxlevel) {
		try{
			pf.setOrder(order);
			pf.setMaxDisplayLevel(maxlevel);
			pf.setMinDisplayLevel(minlevel);
			mEngineMapView.getOverlayLayer().addOverlay(layer, pf);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void addSmallPoint(OverPoint pf) {
		try{
			int order = mEngineMapView.getOverlayLayer().getOverlaySize(MapViewOverLay.MAP_LAYER_SMALL_POINT);
			pf.setOrder(order);
			mEngineMapView.getOverlayLayer().addOverlay(MapViewOverLay.MAP_LAYER_SMALL_POINT, pf);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void addSmallPoints(List<OverPoint> pfList) {
		try{
			for(OverPoint p : pfList){
				addSmallPoint(p);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void addLine(OverLine lf) {
		int order = mEngineMapView.getOverlayLayer().getOverlaySize(MapViewOverLay.MAP_LAYER_LINE);
		lf.setOrder(order);
		mEngineMapView.getOverlayLayer().addOverlay(MapViewOverLay.MAP_LAYER_LINE, lf);
	}

	/**
	 * 添加一条线
	 *
	 * @param
	 * @param layer 必须大于等于0.
	 *            层是用来Overlay的叠加关系的，例如线路可以是0层,转向箭头可以是1层,这样在一层添加的所有转向箭头都覆盖在0层添加的线路之上了
	 *            .而不用担心他们的叠加关系被加线的先后次序打乱.
	 * @param order 可以为任意整数值。用于控制在同一级中的overlay相互之间的叠加关系，值小的被值大的覆盖，
	 *            如果同一layer的两个overlay的order值相同，则他们的叠加顺序不可预知.
	 */
	public void addLine(OverLine lf, int layer, int order) {
		if(lf != null){
			lf.setOrder(order);
		}
		mEngineMapView.getOverlayLayer().addOverlay(layer, lf);
	}

	public OverPolygon addPolygon(Coordinate[] coords, int[] level, Style style) {
//		int simlify[] = new int[19];
//		for(int i=0; i<19; ++i){
//			simlify[i] = i;
//		}
		float arra[] = new float[coords.length*2];
		for(int i=0; i<coords.length; ++i){
			arra[i*2] = (float)coords[i].getX();
			arra[i*2+1] = (float)coords[i].getY();
		}
		OverPolygon polygon = new OverPolygon(arra, 2, level, 19, 50);
		polygon.setStyle(style);
		int order = mEngineMapView.getOverlayLayer().getOverlaySize(MapViewOverLay.MAP_LAYER_POLYGON);
		polygon.setOrder(order);
		mEngineMapView.getOverlayLayer().addOverlay(MapViewOverLay.MAP_LAYER_POLYGON, polygon);
		return polygon;
	}

	public void addPolygon(OverPolygon feature) {
		int order = mEngineMapView.getOverlayLayer().getOverlaySize(MapViewOverLay.MAP_LAYER_POLYGON);
		feature.setOrder(order);
		mEngineMapView.getOverlayLayer().addOverlay(MapViewOverLay.MAP_LAYER_POLYGON, feature);
	}

	public void removeAllSmallPoints() {
		try {
			mEngineMapView.getOverlayLayer().removeOverlays(MapViewOverLay.MAP_LAYER_SMALL_POINT);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void removePoint(OverPoint pf) {
		try{
			mEngineMapView.getOverlayLayer().removeOverlay(MapViewOverLay.MAP_LAYER_POINT, pf);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void removePoint(OverPoint pf, int layer) {
		try{
			mEngineMapView.getOverlayLayer().removeOverlay(layer, pf);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void removeAllFeatures() {
//		mSdkMapview.removeAllFeatures();
	}


	@Deprecated
	public void removeLine(OverLine pf) {
		try{
			mEngineMapView.getOverlayLayer().removeOverlay(MapViewOverLay.MAP_LAYER_LINE, pf);
		}catch(Exception e){}
	}

	public void removeLine(OverLine pf, int layer) {
		try{
			mEngineMapView.getOverlayLayer().removeOverlay(layer, pf);
		}catch(Exception e){

		}
	}

	public void removeAllPoint(int layer) {
		mEngineMapView.getOverlayLayer().removeOverlays(layer);
	}

	public void removePolygon(OverPolygon pf) {
		mEngineMapView.getOverlayLayer().removeOverlay(MapViewOverLay.MAP_LAYER_POLYGON, pf);
	}




}
