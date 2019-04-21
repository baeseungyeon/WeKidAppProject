package com.example.wekid;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TeacherHomeActivity extends AppCompatActivity {
    TextView kinderName;
    TextView className;
    TextView teacherName;

    String id = null;
    String name = null;
    String kinder = null;       // 유치원 명
    String myClass = null;   // 변수명 'class'로 못 만들게 돼있어서 myClass로 만듦
    String phoneNum = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        kinderName = (TextView) findViewById(R.id.kinderName);
        className = (TextView) findViewById(R.id.className);
        teacherName = (TextView) findViewById(R.id.teacherName);

        // MainActivity에서 보낸 데이터 받아옴 ---
        Intent intent = getIntent();

        id = intent.getExtras().getString("id");
        name = intent.getExtras().getString("name");
        kinder = intent.getExtras().getString("kinder");
        myClass = intent.getExtras().getString("myClass");
        phoneNum = intent.getExtras().getString("phoneNum");
        // 여기까지 -----------------------------

        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();

        // 화면에 띄워주는 코드
        kinderName.setText(kinder);
        className.setText(myClass);
        teacherName.setText(name);
        // 여기까지 -----------------------------
    }
}
