package com.example.wekid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
public class JoinActivity extends AppCompatActivity {
    public Spinner userTypeSpinner;
    public String[] userTypeArr = {"교사", "학부모"};
    public String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // 스피너 초기화 ------------------------------
        Spinner spinner = (Spinner) findViewById(R.id.userTypeSpinner);

        //어댑터 정의 <String 배열> - api에 미리 정의되어있는 adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, userTypeArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = userTypeArr[position].toString();
                Toast.makeText(getApplicationContext(), userType, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 여기까지 ------------------------------
    }
}
