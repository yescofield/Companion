package com.yezimm.companion.mapsdk.ctrl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yezimm.companion.R;

public class MapGpsView {

   public static enum GpsViewStateType{
	   common_location_normal,common_location_arrowweak_normal,common_location_position_normal,
	   common_location_highlight,common_location_arrowweak_highlight,common_location_position_highlight,
   }
   
   private static MapGpsView instance;
   
   private Bitmap common_location_normal;
   private Bitmap common_location_arrowweak_normal;
   private Bitmap common_location_position_normal;
   private Bitmap common_location_highlight;
   private Bitmap common_location_arrowweak_highlight;
   private Bitmap common_location_position_highlight;
   
   public static MapGpsView getInstance(){
	   if(instance == null){
		   synchronized(MapGpsView.class){
			   if(instance == null){
				   instance = new MapGpsView();
			   }
		   }
	   }
	   return instance;
   }
   
   public Bitmap getGPSBitMap(GpsViewStateType type){
	   Bitmap bitmap = null;
	   if(type != null){
		   switch(type){
		   case common_location_arrowweak_highlight:
			   if(common_location_arrowweak_highlight == null || common_location_arrowweak_highlight.isRecycled()){
				   common_location_arrowweak_highlight = createBitmMap(R.mipmap.common_location_arrowweak_highlight);
			   }
			   bitmap = common_location_arrowweak_highlight ;
			   break;
		   case common_location_arrowweak_normal:
			   if(common_location_arrowweak_normal == null || common_location_arrowweak_normal.isRecycled()){
				   common_location_arrowweak_normal = createBitmMap(R.mipmap.common_location_arrowweak_normal);
			   }
			   bitmap = common_location_arrowweak_normal ;
			   break;
		   case common_location_highlight:
			   if(common_location_highlight == null || common_location_highlight.isRecycled()){
				   common_location_highlight = createBitmMap(R.mipmap.common_location_highlight);
			   }
			   bitmap = common_location_highlight ;
			   break;
		   case common_location_normal:
			   if(common_location_normal == null || common_location_normal.isRecycled()){
				   common_location_normal = createBitmMap(R.mipmap.common_location_normal);
			   }
			   bitmap = common_location_normal ;
			   break;
		   case common_location_position_highlight:
			   if(common_location_position_highlight == null || common_location_position_highlight.isRecycled()){
				   common_location_position_highlight = createBitmMap(R.mipmap.common_location_position_highlight);
			   }
			   bitmap = common_location_position_highlight ;
			   break;
		   case common_location_position_normal:
			   if(common_location_position_normal == null || common_location_position_normal.isRecycled()){
				   common_location_position_normal = createBitmMap(R.mipmap.common_location_position_normal);
			   }
			   bitmap = common_location_position_normal ;
			   break;
			   default:
				   if(common_location_arrowweak_highlight == null || common_location_arrowweak_highlight.isRecycled()){
					   common_location_arrowweak_highlight = createBitmMap(R.mipmap.common_location_arrowweak_highlight);
				   }
				   bitmap = common_location_arrowweak_highlight ;
		   }
	   }
	   return bitmap;
   }
   
   private Bitmap createBitmMap(int resId){
//	   Bitmap bitmap = BitmapFactory.decodeResource(SysUtils.getMainActivity().getResources(), resId);
//	   return bitmap;
	   return null;
   }
	
}
