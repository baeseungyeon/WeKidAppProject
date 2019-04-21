package com.example.wekid;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button joinBtn;
    private EditText inputId;
    private EditText inputPwd;

    RadioGroup userTypeGroup;
    RadioButton teacherBtn;
    RadioButton parentsBtn;
    String checkUserType;

    ////////////////////// 서버에서 받아올 정보들 담는 변수 ///////////////////
    String status;        // 로그인 실패 = 0, 로그인 성공 = 1
    String id;
    String name ;         // 이름
    String phoneNum;
    String kinderName;    // 유치원 이름, 교사만 받아옴
    String className;     // 반 이름, 교사만 받아옴
    //////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        inputId = (EditText) findViewById(R.id.inputId);
        inputPwd = (EditText) findViewById(R.id.inputPwd);
        joinBtn = (Button) findViewById(R.id.joinBtn);

        userTypeGroup = (RadioGroup)findViewById(R.id.userTypeGroup);
        teacherBtn = (RadioButton) findViewById(R.id.teacherBtn);
        parentsBtn = (RadioButton) findViewById(R.id.parentsBtn);

        // 로그인 버튼 클릭 이벤트 ----------------
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 라디오 버튼 체크
                if(teacherBtn.isChecked() == true) {
                    checkUserType = "0";
                    // 로그인 버튼을 클릭하면 AsyncTask를 실행함. 아래 url이 doInBackground의 파라미터로 들어감
                    new JSONTask().execute("http://10.0.2.2:3000/login"); //AsyncTask 시작시킴
                } else if(parentsBtn.isChecked() == true) {
                    checkUserType = "1";
                    // 로그인 버튼을 클릭하면 AsyncTask를 실행함. 아래 url이 doInBackground의 파라미터로 들어감
                    new JSONTask().execute("http://10.0.2.2:3000/login"); //AsyncTask 시작시킴
                } else {
                    Toast.makeText(getApplicationContext(), "회원구분을 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 여기까지 ------------------------------

        // 회원가입 버튼 클릭 이벤트 ---------------
        joinBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 회원가입 activity로 넘어가기 위한 코드
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });
        // 여기까지 ------------------------------
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                org.json.JSONObject jsonObject = new org.json.JSONObject();
                jsonObject.accumulate("inputId", inputId.getText().toString());     // inputId를 서버로 보냄
                jsonObject.accumulate("inputPwd", inputPwd.getText().toString());   // inputPwd를 서버로 보냄
                jsonObject.accumulate("checkUserType", checkUserType); // 라디오버튼으로 체크한 userType을 서버로 보냄

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://13.125.112.4:3000/login");  // aws url
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듦
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌
                    //----------------------------------- 데이터 보내기 끝 ----------------------------------------//

                    //------------------------------- 서버로부터 데이터를 받음 -------------------------------------//
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 서버에서 받아온 json 가공 --------------
            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.get("status").toString();

                // 로그인 성공
                if(status.equals("1")) {
                    id = jsonObject.get("id").toString();
                    name = jsonObject.get("name").toString();
                    phoneNum = jsonObject.get("phoneNum").toString();

                    if(teacherBtn.isChecked() == true) {    // 교사인 경우
                        kinderName = jsonObject.get("kinderName").toString();
                        className = jsonObject.get("className").toString();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 여기까지 ------------------------------

            // 로그인 상태 띄워주기 //
            if(status.equals("1")) {    // 로그인이 성공했다면
                //Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();

                if(teacherBtn.isChecked() == true) {  // 로그인이 성공했는데 교사라면
                    // teacherActivity로 넘김
                    Intent intent = new Intent(getApplicationContext(), TeacherHomeActivity.class);

                    Log.i("data : ", id + " " + name + " " + kinderName + " " + className + " " + phoneNum);
                    // 데이터도 같이 전달
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("kinderName", kinderName);
                    intent.putExtra("className", className);
                    intent.putExtra("phoneNum", phoneNum);

                    startActivity(intent);
                }
                else if(parentsBtn.isChecked() == true) { // 로그인이 성공했는데 학부모라면
                    // parentsActivity로 넘김
                    Intent intent = new Intent(getApplicationContext(), ParentsHomeActivity.class);

                    // 데이터도 같이 전달
                    intent.putExtra("name", name);
                    intent.putExtra("id", id);

                    startActivity(intent);
                }
            } else {    // 로그인 실패
                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
            }
        }
    }
}
