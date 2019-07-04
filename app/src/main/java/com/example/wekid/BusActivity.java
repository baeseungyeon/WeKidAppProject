package com.example.wekid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BusActivity extends AppCompatActivity {
    private Button busStartBtn;
    private Button busNowBtn;
    private Button busStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        busStartBtn = (Button) findViewById(R.id.busStartBtn);
        busNowBtn = (Button) findViewById(R.id.busNowBtn);
        busStopBtn = (Button) findViewById(R.id.busStopBtn);

        // 버스 운행 버튼 클릭 이벤트
        busStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusActivity.this, BusStartActivity.class);
                startActivity(intent);
            }
        });

        // 탑승 현황 버튼 클릭 이벤트
        busNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusActivity.this, BusBoardingNowActivity.class);
                startActivity(intent);
            }
        });


        //운행 종료 버튼 클릭 이벤트
        busStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusActivity.this, BusExitActivity.class);
                startActivity(intent);
            }
        });
    }
}
