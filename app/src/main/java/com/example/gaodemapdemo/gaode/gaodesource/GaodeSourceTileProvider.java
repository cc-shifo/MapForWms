package com.example.gaodemapdemo.gaode.gaodesource;


import android.content.Context;
import android.util.Log;

import com.amap.api.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zkj on 2017/08/02
 */

public class GaodeSourceTileProvider extends UrlTileProvider {

    private String mRootUrl;
    //默认瓦片大小
    private static final int TILE_SIZE = 512;//a=6378137±2（m）
    //基本参数
    private final double initialResolution = 156543.03392804062;//2*Math.PI*6378137/titleSize;
    private final double originShift = 20037508.342789244;//2*Math.PI*6378137/2.0; 周长的一半

    private final double HALF_PI = Math.PI / 2.0;
    private final double RAD_PER_DEGREE = Math.PI / 180.0;
    private final double HALF_RAD_PER_DEGREE = Math.PI / 360.0;
    private final double METER_PER_DEGREE = originShift / 180.0;//一度多少米
    private final double DEGREE_PER_METER = 180.0 / originShift;//一米多少度

    private Context mContext;


    public GaodeSourceTileProvider() {
        super(TILE_SIZE, TILE_SIZE);
        //地址写你自己的wms地址
        // mRootUrl = "http://xxxxxx自己的/wms?LAYERS=cwh:protect_region_38_20160830&
        // FORMAT=image%2Fpng&TRANSPARENT=TRUE&SERVICE=WMS&VERSION=1.1.1&
        // REQUEST=GetMap&STYLES=&SRS=EPSG%3A900913&BBOX=";
        // mRootUrl = "http://192.168.43.249:8080/geoserver/gwc/demo/yanting%3APLBDOM?gridSet=EPSG%3A900913&format=image/png&BBOX=";
        // mRootUrl = "http://192.168.43.249:8080/geoserver/yanting/wms?service=WMS&version=1.1.0&request=GetMap&layers=yanting%3APLBDOM&srs=EPSG%3A4490&styles=&format=image%2Fpng&TRANSPARENT=TRUE&bbox=";
        // mRootUrl = "http://192.168.43.249:8080/geoserver/tmp/wms?service=WMS&version=1.1.0&request=GetMap&layers=tmp%3Alushanxian&srs=EPSG%3A4490&styles=&format=image%2Fpng&TRANSPARENT=TRUE&bbox=";
        mRootUrl = "http://192.168.43.249:8080/geoserver/gaodelushanxian/wms?LAYERS" +
                "=gaodelushanxian:lushanxian&FORMAT" +
                "=image%2Fpng&TRANSPARENT=TRUE&SERVICE=WMS&VERSION=1.1" +
                ".0&REQUEST=GetMap&STYLES=&SRS=EPSG:3857&BBOX=";
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public GaodeSourceTileProvider(int i, int i1) {
        super(i, i1);
    }

    @Override
    public URL getTileUrl(int x, int y, int level) {

        try {
            String url = mRootUrl + TitleBounds(x, y, level);
            Log.e("getTileUrl", "getTileUrl: " + url);
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 根据像素、等级算出坐标
     *
     * @param p
     * @param zoom
     * @return
     */
    private double Pixels2Meters(int p, int zoom) {
        return p * Resolution(zoom) - originShift;
    }

    /**
     * 根据瓦片的x/y等级返回瓦片范围
     *
     * @param tx
     * @param ty
     * @param zoom
     * @return
     */
    private String TitleBounds(int tx, int ty, int zoom) {


        double minX = Pixels2Meters(tx * TILE_SIZE, zoom);
        double maxY = -Pixels2Meters(ty * TILE_SIZE, zoom);
        double maxX = Pixels2Meters((tx + 1) * TILE_SIZE, zoom);
        double minY = -Pixels2Meters((ty + 1) * TILE_SIZE, zoom);
        return minX + "," + minY + "," + maxX + "," + maxY + "&WIDTH=" + TILE_SIZE + "&HEIGHT=" + TILE_SIZE;
    }

    /**
     * 计算分辨率
     *
     * @param zoom
     * @return
     */
    private double Resolution(int zoom) {
        return initialResolution / (Math.pow(2, zoom));
    }

    /**
     * X米转经纬度
     */
    private double Meters2Lon(double mx) {
        double lon = mx * DEGREE_PER_METER;
        return lon;
    }

    /**
     * Y米转经纬度
     */
    private double Meters2Lat(double my) {
        double lat = my * DEGREE_PER_METER;
        lat = 180.0 / Math.PI * (2 * Math.atan(Math.exp(lat * RAD_PER_DEGREE)) - HALF_PI);
        return lat;
    }

    /**
     * X经纬度转米
     */
    private double Lon2Meter(double lon) {
        double mx = lon * METER_PER_DEGREE;
        return mx;
    }

    /**
     * Y经纬度转米
     */
    private double Lat2Meter(double lat) {
        double my = Math.log(Math.tan((90 + lat) * HALF_RAD_PER_DEGREE)) / (RAD_PER_DEGREE);
        my = my * METER_PER_DEGREE;
        return my;
    }

}
