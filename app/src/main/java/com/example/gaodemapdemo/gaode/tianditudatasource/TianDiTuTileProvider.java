package com.example.gaodemapdemo.gaode.tianditudatasource;


import android.content.Context;
import android.util.Log;

import com.amap.api.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zkj on 2017/08/02
 */

public class TianDiTuTileProvider extends UrlTileProvider {
    private static final String URL_KEY = "0d53470343f82423bd71a6d10ca5a107";
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

    private String mRootUrl;
    private Context mContext;


    public TianDiTuTileProvider() {
        super(TILE_SIZE, TILE_SIZE);
        //地址写你自己的wms地址
        // mRootUrl = "http://xxxxxx自己的/wms?LAYERS=cwh:protect_region_38_20160830&
        // FORMAT=image%2Fpng&TRANSPARENT=TRUE&SERVICE=WMS&VERSION=1.1.1&
        // REQUEST=GetMap&STYLES=&SRS=EPSG%3A900913&BBOX=";
        // mRootUrl = "http://192.168.43.249:8080/geoserver/gwc/demo/yanting%3APLBDOM?gridSet=EPSG%3A900913&format=image/png&BBOX=";
        // mRootUrl = "http://192.168.43.249:8080/geoserver/yanting/wms?service=WMS&version=1.1.0&request=GetMap&layers=yanting%3APLBDOM&srs=EPSG%3A4490&styles=&format=image%2Fpng&TRANSPARENT=TRUE&bbox=";
        // mRootUrl = "http://192.168.43.249:8080/geoserver/tmp/wms?service=WMS&version=1.1.0&request=GetMap&layers=tmp%3Alushanxian&srs=EPSG%3A4490&styles=&format=image%2Fpng&TRANSPARENT=TRUE&bbox=";
        // mRootUrl = "http://192.168.43.249:8080/geoserver/tmp/wms?LAYERS=tmp:lushanxian&FORMAT" +
        //         "=image%2Fpng&TRANSPARENT=TRUE&SERVICE=WMS&VERSION=1.1" +
        //         ".0&REQUEST=GetMap&STYLES=&SRS=EPSG:4326&BBOX=";

        mRootUrl = "http://t7.tianditu.gov.cn/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0" +
                ".0&LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles";
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public TianDiTuTileProvider(int i, int i1) {
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
        return "&TILEMATRIX=" + zoom + "&TILEROW=" + tx + "&TILECOL=" + ty + "&tk=" + URL_KEY;
        // double minX = Pixels2Meters(tx * titleSize, zoom);
        // double maxY = -Pixels2Meters(ty * titleSize, zoom);
        // double maxX = Pixels2Meters((tx + 1) * titleSize, zoom);
        // double minY = -Pixels2Meters((ty + 1) * titleSize, zoom);
        //
        // //转换成经纬度
        // minX = Meters2Lon(minX);
        // minY = Meters2Lat(minY);
        // maxX = Meters2Lon(maxX);
        // maxY = Meters2Lat(maxY);
        // PositionModel position1 = PositionUtil.gcj_To_Gps84(minY, minX);
        // minX = position1.getWgLon();
        // minY = position1.getWgLat();
        // PositionModel position2 = PositionUtil.gcj_To_Gps84(maxY, maxX);
        // maxX = position2.getWgLon();
        // maxY = position2.getWgLat();
        //
        // // minX = Lon2Meter(minX);
        // // minY = Lat2Meter(minY);
        // // maxX = Lon2Meter(maxX);
        // // maxY = Lat2Meter(maxY);
        //
        // String sMinX = new BigDecimal(minX).toString();
        // String sMinY = new BigDecimal(minY).toString();
        // String sMaxX = new BigDecimal(maxX).toString();
        // String sMaxY = new BigDecimal(maxY).toString();
        //
        // return sMinX + "%2C" + sMinY + "%2C" + sMaxX + "%2C" + sMaxY + "&WIDTH=256&HEIGHT=256";

        // double minX = Pixels2Meters(tx * TILE_SIZE, zoom);
        // double maxY = -Pixels2Meters(ty * TILE_SIZE, zoom);
        // double maxX = Pixels2Meters((tx + 1) * TILE_SIZE, zoom);
        // double minY = -Pixels2Meters((ty + 1) * TILE_SIZE, zoom);
        //
        // //转换成经纬度
        // minX = Meters2Lon(minX);
        // minY = Meters2Lat(minY);
        // maxX = Meters2Lon(maxX);
        // maxY = Meters2Lat(maxY);
        // // //坐标转换工具类构造方法 Gps( WGS-84) 转 为高德地图需要的坐标
        // // PositionModel position1 = PositionUtil.gcj_To_Gps84(minY, minX);
        // // minX = position1.getWgLon();
        // // minY = position1.getWgLat();
        // // PositionModel position2 = PositionUtil.gcj_To_Gps84(maxY, maxX);
        // // maxX = position2.getWgLon();
        // // maxY = position2.getWgLat();
        //
        // // 图源是高德的图源，不需要再进行经纬度转换。
        // // 图源不是高德的情况下，即便转换了也有误差。（3857坐标系的图源数据没有验证是否有偏差）
        // return minX + "," + minY + "," + maxX + "," + maxY + "&WIDTH=" + TILE_SIZE +
        // "&HEIGHT=" + TILE_SIZE;

        // CoordinateConverter coordinateConverter = new CoordinateConverter(mContext);
        // coordinateConverter.from(CoordinateConverter.CoordType.GPS);
        // coordinateConverter.coord(new LatLng(minY, minX));
        // LatLng min = coordinateConverter.convert();
        // coordinateConverter.coord(new LatLng(maxY, maxX));
        // LatLng max = coordinateConverter.convert();
        // return min.longitude + "," + min.latitude + "," + max.longitude + "," + max.latitude +
        // "&WIDTH" +
        //         "=256&HEIGHT=256";
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
