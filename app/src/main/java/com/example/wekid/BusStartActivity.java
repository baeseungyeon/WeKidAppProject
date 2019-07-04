package com.example.wekid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class BusStartActivity extends AppCompatActivity {
    private Button startYesBtn;
    private Button startNoBtn;
    private Spinner busStartSpinner;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_start);

        startYesBtn = (Button) findViewById(R.id.startYesBtn);
        startNoBtn = (Button) findViewById(R.id.startNoBtn);

        busStartSpinner = (Spinner) findViewById(R.id.busStartSpinner);

        // 스피너에 버스 목록 띄우는거
        arrayList = new ArrayList<>();
        arrayList.add("1호차");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList);

        busStartSpinner.setAdapter(arrayAdapter);
        // 여기까지 ------------------------------

        // yes 버튼 클릭 이벤트
        startYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusStartActivity.this, BusBoardingNowActivity.class);
                startActivity(intent);
            }
        });

        // no 버튼 클릭 이벤트
        startNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
