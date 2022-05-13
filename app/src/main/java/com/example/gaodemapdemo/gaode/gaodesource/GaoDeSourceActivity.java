package com.example.gaodemapdemo.gaode.gaodesource;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.example.gaodemapdemo.R;
import com.example.gaodemapdemo.gaode.ThreadUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GaoDeSourceActivity extends AppCompatActivity {

    private MapView mMapview;
    private AMap aMap;
    private Polygon polygon;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaode_source);
        MapsInitializer.updatePrivacyShow(GaoDeSourceActivity.this, true, true);
        MapsInitializer.updatePrivacyAgree(GaoDeSourceActivity.this, true);

        mMapview = ((MapView) findViewById(R.id.activity_gao_de_src_tdt_mv_map));
        mTextView = (TextView) findViewById(R.id.activity_gao_de_src_tv_zoom_level);

        mMapview.onCreate(savedInstanceState);
        initAMap();

        setAMap();

        // initPolygon();
        //加载自定义wms
        GaodeSourceTileProvider tileProvider = new GaodeSourceTileProvider();
        tileProvider.setContext(GaoDeSourceActivity.this);
        aMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider));
    }

    private void initPolygon() {
        List<LatLng> latLngs = new ArrayList();
        //绘制一个全世界的多边形
        latLngs.add(new LatLng(84.9, -179.9));
        latLngs.add(new LatLng(84.9, 179.9));
        latLngs.add(new LatLng(-84.9, 179.9));
        latLngs.add(new LatLng(-84.9, -179.9));

        polygon = aMap.addPolygon(new PolygonOptions().addAll(latLngs).fillColor(Color.argb(127,
                245, 245, 245)).zIndex(10));
        searchDistrict();
    }

    private void searchDistrict() {
        // String province = "庐山";
        String province = "四川省雅安市";

        DistrictSearch districtSearch = null;
        try {
            districtSearch = new DistrictSearch(getApplicationContext());
            DistrictSearchQuery districtSearchQuery = new DistrictSearchQuery();
            // districtSearchQuery.setKeywords(province);
            // districtSearchQuery.setKeywords("武汉市");
            // districtSearchQuery.setKeywords("四川省雅安市");
            districtSearchQuery.setKeywords("511826");//芦山县
            // districtSearchQuery.setKeywords("510723");//盐亭县
            districtSearchQuery.setShowBoundary(true);
            // districtSearchQuery.setShowChild(true);
            districtSearchQuery.setSubDistrict(0);
            districtSearch.setQuery(districtSearchQuery);

            districtSearch.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {
                @Override
                public void onDistrictSearched(DistrictResult districtResult) {
                    if (districtResult == null || districtResult.getDistrict() == null) {
                        return;
                    }
                    showDistrictBounds(districtResult);
                }
            });

            //请求边界数据 开始展示
            districtSearch.searchDistrictAsyn();
        } catch (AMapException e) {
            e.printStackTrace();
        }

    }

    protected void showDistrictBounds(DistrictResult districtResult) {
        //通过ErrorCode判断是否成功
        if (districtResult.getAMapException() != null && districtResult.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {
            final DistrictItem item = districtResult.getDistrict().get(0);

            if (item == null) {
                return;
            }

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    drawBound(item);
                }
            });
        } else {
            if (districtResult.getAMapException() != null) {
                Log.e("amap", "error " + districtResult.getAMapException().getErrorCode());
            }
        }
    }

    private void drawBound(DistrictItem item) {
        String[] polyStr = item.districtBoundary();
        if (polyStr == null || polyStr.length == 0) {
            return;
        }

        List<com.amap.api.maps.model.BaseHoleOptions> holeOptionsList =
                new ArrayList<>();
        for (String str : polyStr) {
            String[] lat = str.split(";");
            com.amap.api.maps.model.PolygonHoleOptions polygonHoleOptions =
                    new com.amap.api.maps.model.PolygonHoleOptions();
            List<LatLng> holeLatLngs = new ArrayList<>();
            boolean isFirst = true;
            LatLng firstLatLng = null;
            int index = 0;

            // 不跳过任何点，以最高精度显示边界
            // 可以过滤掉一些点，提升绘制效率
            /*int offset = 50;
            if (lat.length < 400) {
                offset = 20;
            }*/

            for (String latstr : lat) {
                index++;
                // 不跳过任何点，以最高精度显示边界
                /*if (index % offset != 0) {
                    continue;
                }*/
                String[] lats = latstr.split(",");
                if (isFirst) {
                    isFirst = false;
                    firstLatLng = new LatLng(Double
                            .parseDouble(lats[1]), Double
                            .parseDouble(lats[0]));
                }
                holeLatLngs.add(new LatLng(Double
                        .parseDouble(lats[1]), Double
                        .parseDouble(lats[0])));
            }
            if (firstLatLng != null) {
                holeLatLngs.add(firstLatLng);
            }

            polygonHoleOptions.addAll(holeLatLngs);

            holeOptionsList.add(polygonHoleOptions);
        }

        if (holeOptionsList.size() > 0) {
            GaoDeSourceActivity.ListComparator c = new GaoDeSourceActivity.ListComparator();
            // 将洞的内容排序，优先显示量大的洞，避免出现洞相互叠加，导致无法展示
            Collections.sort(holeOptionsList, c);
            for (com.amap.api.maps.model.BaseHoleOptions holeOptions :
                    holeOptionsList) {
                com.amap.api.maps.model.PolygonHoleOptions polygonHoleOption =
                        (com.amap.api.maps.model.PolygonHoleOptions) holeOptions;
                android.util.Log.e("amap",
                        "polygonHoleOption size " + polygonHoleOption.getPoints().size());
            }

            polygon.setHoleOptions(holeOptionsList);
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapview.onSaveInstanceState(outState);
    }

    /**
     * 地图方法
     */
    private void setAMap() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(3);
        //设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);

        aMap.getUiSettings().setScaleControlsEnabled(true);// 设置比例尺
    }


    /**
     * 初始化AMap对象
     */
    private void initAMap() {
        if (aMap == null) {
            aMap = mMapview.getMap();
        }
        setUpMap();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapview.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mMapview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapview.onDestroy();
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        // LatLng latLng = new LatLng(31.247, 105.565);// 庐山县经纬度
        // aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
        //         latLng, 12, 30, 30)));
        final LatLng LUSHANXIAN = new LatLng(30.145077, 102.933618);// 庐山县经纬度
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                LUSHANXIAN, 9, 30, 30)));
        // 设置可视范围变化时的回调的接口方法
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                //屏幕中心的Marker跳动
                Log.d("onCameraChangeFinish",
                        "onCameraChangeFinish: " + Thread.currentThread().getName());
                String text = "缩放等级" + cameraPosition.zoom;
                mTextView.setText(text);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    static class ListComparator implements Comparator<Object>,
            Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Object lhs, Object rhs) {
            com.amap.api.maps.model.PolygonHoleOptions fir =
                    (com.amap.api.maps.model.PolygonHoleOptions) lhs;
            com.amap.api.maps.model.PolygonHoleOptions sec =
                    (com.amap.api.maps.model.PolygonHoleOptions) rhs;
            try {
                if (fir != null && sec != null && fir.getPoints() != null) {
                    if (fir.getPoints().size() < sec.getPoints().size()) {
                        return 1;
                    } else if (fir.getPoints().size() > sec.getPoints().size()) {
                        return -1;
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            return 0;
        }
    }

}
