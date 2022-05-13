package com.example.gaodemapdemo.gaode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.gaodemapdemo.R;
import com.example.gaodemapdemo.gaode.gaodesource.GaoDeSourceActivity;
import com.example.gaodemapdemo.gaode.tianditudatasource.MainTianDiTuSrcActivity;
import com.example.gaodemapdemo.gaode.wgs84Merctorsource.Wgs84MercatorActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonDiffActivity = (Button) findViewById(R.id.btn_start_diff_map_activity);
        buttonDiffActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DifferentMapSDKActivity.class);
                startActivity(intent);
            }
        });

        Button buttonAMapTianDiTuSrcActivity =
                (Button) findViewById(R.id.btn_start_AMAP_TianDiTu_src_activity);
        buttonAMapTianDiTuSrcActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainTianDiTuSrcActivity.class);
                startActivity(intent);
            }
        });

        Button buttonWGS84MercatorSrcActivity =
                (Button) findViewById(R.id.btn_start_wgs84_mercator_src_activity);
        buttonWGS84MercatorSrcActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Wgs84MercatorActivity.class);
                startActivity(intent);
            }
        });

        Button buttonGaoDeSrcActivity =
                (Button) findViewById(R.id.btn_start_gao_de_src_activity);
        buttonGaoDeSrcActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GaoDeSourceActivity.class);
                startActivity(intent);
            }
        });

    }
}