package com.example.wekid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class parentsHomeActivity extends AppCompatActivity {
    TextView kinderName;
    TextView className;
    TextView parentsName;

    String name = null;
    String kinder = null;       // 유치원 명
    String myClass = null;   // 변수명 'class'로 못 만들게 돼있어서 myClass로 만듦
    String phoneNum = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_home);

        kinderName = (TextView) findViewById(R.id.kinderName);
        className = (TextView) findViewById(R.id.className);
        parentsName = (TextView) findViewById(R.id.parentsName);

        // MainActivity에서 보낸 데이터 받아옴 ---
        Intent intent = getIntent();

        name = intent.getExtras().getString("name");
        kinder = intent.getExtras().getString("kinder");
        myClass = intent.getExtras().getString("myClass");
        phoneNum = intent.getExtras().getString("phoneNum");
        // 여기까지 -----------------------------

        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();

        // 화면에 띄워주는 코드
        kinderName.setText(kinder);
        className.setText(myClass);
        parentsName.setText(name);
        // 여기까지 -----------------------------
    }
}
