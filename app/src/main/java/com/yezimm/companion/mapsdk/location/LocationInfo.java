package com.nicenearby.sogou.discovery.mapsdk.location;

import com.sogou.map.loc.SGLocation;
import com.sogou.map.mobile.geometry.Point;

/**
 * Created by yejihuang on 2015/5/25.
 */
public class LocationInfo {

    private LocationInfo oriLocationInfo = null;

    private float accuracy = -1; // 精确度
    private boolean validBearing = false; // 方向有效
    private float bearing = 0; // 方向
    public Point location = null; // 坐标
    private float speed = 0; // 速度
    private long time = 0; // 时间
    private int confidence; // 置信度

    private float hdop;// 水平分量精度因子：为纬度和经度等误差平方和的开根号值

    private int fix;// 定位卫星数

    private int locType = SGLocation.TYPE_NET;
    private int linkUid;

    /****是否在规划路线上******/
    private boolean isOnRoute = true;

    private int priIndex;
    private int[] naviLinkkeys;

    private int mapMatchStatus;

    private String timeInfo;


    public boolean isOnRoute() {
        return isOnRoute;
    }

    public void setOnRoute(boolean isOnRoute) {
        this.isOnRoute = isOnRoute;
    }



    public LocationInfo(Point location, float accuracy, long time, float speed, float bearing, float hdop, int fix
            ,int linkUid, int priIndex, int[] naviLinkKeys) {
        this.location = location;
        this.accuracy = accuracy;
        this.time = time;
        this.speed = speed;
        this.bearing = bearing;
        this.confidence = SGLocation.CONFIDENCE_HIGH;
        this.setFix(fix);
        this.setHdop(hdop);
        this.isOnRoute = true;
    }

    public LocationInfo(SGLocation loc) {
        this.location = new Point((float) loc.getLongitude(), (float) loc.getLatitude());
        this.location.setZ((float) loc.getAltitude());
        this.accuracy = loc.getAccuracy();
        this.time = loc.getCreateTime();
        this.speed = loc.getSpeed();
        this.bearing = loc.getBearing();
        this.confidence = loc.getConfidence();
        this.setLocType(loc.getType());
        this.oriLocationInfo = new LocationInfo(this.location, this.accuracy, this.time, this.speed, this.bearing, 0, 0,
                0, 0, null);
    }

    public LocationInfo(LocationInfo loc) {
        Point p = loc.getLocation();
        if (p != null) {
            this.location = new Point(p.getX(), p.getY(), p.getZ());
        }
        this.accuracy = loc.accuracy;
        this.time = loc.time;
        this.speed = loc.speed;
        this.bearing = loc.bearing;
        this.confidence = loc.confidence;
        this.setFix(loc.getFix());
        this.setHdop((float)loc.getHdop());
        this.setLocType(loc.locType);
        this.isOnRoute = loc.isOnRoute;
        if (loc.oriLocationInfo != null) {
            this.oriLocationInfo = loc.oriLocationInfo;
        }
    }



    public LocationInfo() {
        // TODO Auto-generated constructor stub
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getAccuracy() {
        if (accuracy > 2000)
            return 2000;
        return accuracy;
    }

    public Point getLocation() {
        return location;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setSenserBearing(float bearing) {
        this.bearing = bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getBearing() {
        return bearing;
    }

    public void setOriLocationInfo(LocationInfo locationInfo) {
        this.oriLocationInfo = locationInfo;
    }

    public LocationInfo getOriLocationInfo() {
        return oriLocationInfo;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }


    public void setConfidence(int c) {
        this.confidence = c;
    }

    public int getConfidence() {
        return confidence;
    }

    public String toString() {
        String str = "";

        if (accuracy != -1)
            str += "accuracy:" + accuracy + " ";

        if (speed != -1)
            str += "speed:" + speed + " ";

        if (time != -1)
            str += "time:" + time + " ";

        if (bearing != -1)
            str += "bearing:" + bearing + " ";

        return str;
    }

    /**
     * 中国四周的地理坐标，本函数仅用来监控地址是否在国内，非精确数值。
     *
     * @param loc
     * @return
     */
    public static boolean isValidLocation(LocationInfo loc) {
        Point pnt = loc.getLocation();
        float x = pnt.getX();
        float y = pnt.getY();
        return isValidLocation(x, y);
    }

    public static boolean isValidLocation(float x, float y) {
        float westLon = 8150750;
        float eastLon = 15930000;
        float northLat = 7788000;
        float southLat = 313937;
        if (x > westLon && x < eastLon && y > southLat && y < northLat) {
            return true;
        }
        return false;
    }

    public static boolean sameLocation(LocationInfo loc1, LocationInfo loc2) {
        if (loc1 == null || loc1.getLocation() == null || loc2 == null || loc2.getLocation() == null) {
            return false;
        }

        if (loc1.getLocation().getX() == loc2.getLocation().getX() && loc1.getLocation().getY() == loc2.getLocation().getY()
                && loc1.getAccuracy() == loc2.getAccuracy() && loc1.getSpeed() == loc2.getSpeed()) {
            return true;
        }

        return false;
    }

    public static boolean sameLocationStrictMode(LocationInfo loc1, LocationInfo loc2) {
        if (loc1 == null || loc1.getLocation() == null || loc2 == null || loc2.getLocation() == null) {
            return false;
        }

        if (loc1.getLocation().getX() == loc2.getLocation().getX() && loc1.getLocation().getY() == loc2.getLocation().getY()
                && loc1.getAccuracy() == loc2.getAccuracy() && loc1.getSpeed() == loc2.getSpeed() &&
                loc1.getTime() == loc2.getTime()) {
            return true;
        }

        return false;
    }

    public static boolean mostSameLocation(LocationInfo loc1, LocationInfo loc2){
        if (loc1 == null || loc1.getLocation() == null || loc2 == null || loc2.getLocation() == null) {
            return false;
        }

        if (loc1.getLocation().getX() == loc2.getLocation().getX() && loc1.getLocation().getY() == loc2.getLocation().getY()
                && loc1.getSpeed() == loc2.getSpeed()) {
            return true;
        }

        return false;
    }

    public void setLocType(int locType) {
        this.locType = locType;
    }
    public int getLocType() {
        return this.locType;
    }

    public float getHdop() {
        return hdop;
    }

    public void setHdop(float hdop) {
        this.hdop = hdop;
    }

    public int getFix() {
        return fix;
    }

    public void setFix(int fix) {
        this.fix = fix;
    }
}

