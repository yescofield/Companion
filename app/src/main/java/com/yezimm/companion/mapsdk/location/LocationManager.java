package com.nicenearby.sogou.discovery.mapsdk.location;

/**
 * Created by yejihuang on 2015/5/25.
 */
import com.nicenearby.sogou.discovery.view.main.MainActivity;
import com.sogou.map.loc.SGErrorListener;
import com.sogou.map.loc.SGLocClient;
import com.sogou.map.loc.SGLocListener;
import com.sogou.map.loc.SGLocation;

import android.content.Context;
import android.util.Log;

public class LocationManager {

    static
    {
        System.loadLibrary("sogouenc");
    }

//	public static LocationManager mInstance;

    private  SGLocClient mSGLocClient;

    private SGLocListener mSGLocListener;

    private SGErrorListener mSGErrorListener;

    private SgLocateListener mSgLocationListener;

//
//	public static LocationManager getInstance(Context context){
//		if(mInstance == null){
//			synchronized(LocationManager.class){
//				if(mInstance == null){
//					if(context == null){
//						return null;
//					}
//					mInstance = new LocationManager();
//					mSGLocClient = new SGLocClient(context);
//
//					mSGLocClient.setKey("6a4d9297f22818175b603fc65f5c2e57ac278d8f");
////					initListenerList();
//				}
//			}
//		}
//		return mInstance;
//	}


    public  LocationManager (Context context){
        try{
            if(context == null){
                return ;
            }
            mSGLocClient = new SGLocClient(context);
            mSGLocClient.setKey("6a4d9297f22818175b603fc65f5c2e57ac278d8f");
        }catch(Throwable e){
            e.printStackTrace();
        }

    }


    public  void setStrategy(int strategrys){
        if(mSGLocClient != null){
            mSGLocClient.setStrategy(strategrys);
        }
    }

    private  void addLocListener(SGLocListener listener){
        if(listener != null){
            mSGLocListener = listener;
        }
        if(listener != null && mSGLocClient != null){
            mSGLocClient.addLocListener(listener);
        }
    }


    private  void addErrorListener(SGErrorListener listener){
        if(listener != null){
            mSGErrorListener = listener;
        }
        if(listener != null && mSGLocClient != null){
            mSGLocClient.addErrorListener(listener);
        }
    }

//   public  void removeErrorListener(){
//	   mSGErrorListener = null;
//   }
//
//   public void removeLocationListener(){
//	   mSGLocListener = null;
//   }

    /**
     * 开始定位
     * @param watchperiodTime
     */
    public synchronized void startLocation(int watchperiodTime){
        if( mSGLocClient != null){
            Log.e(MainActivity.TAG, "startLocation   "+ watchperiodTime);
            mSGLocClient.watchLocation(watchperiodTime);
        }
    }

    /**
     * 开始一次定位
     */
    public synchronized void requestOnceLocation(){
        Log.e(MainActivity.TAG, "requestOnceLocation");
        if( mSGLocClient != null){
            mSGLocClient.requestLocation();
        }
    }

    /**
     * 结束定位
     */
    public synchronized void stopLocation(){
        if( mSGLocClient != null){
            mSGLocClient.clearWatch();
            mSGLocClient.clearLocListener();
            mSGLocClient.clearErrorListener();
        }
        mSGErrorListener = null;
        mSGLocListener = null;
    }

    public synchronized void addSgLocationListener(SgLocateListener sgLocationListener){
        mSgLocationListener = sgLocationListener;
        addLocListener(new SGLocListener() {
            @Override
            public void onLocationUpdate(SGLocation arg0) {
                if(mSgLocationListener != null){
                    mSgLocationListener.onLocationUpdate(arg0);
                }
            }
        });
        addErrorListener(new SGErrorListener() {

            @Override
            public void onError(int arg0, String arg1) {
                if(mSgLocationListener != null){
                    mSgLocationListener.onLocationError(arg0,arg1);
                }
            }
        });
    }
}
