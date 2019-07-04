package com.example.wekid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeacherHomeActivity extends AppCompatActivity {
    TextView kinderName;
    TextView className;
    TextView teacherName;
    Button busBtn;
    Button messengerBtn;
    Button workStatusBtn;
    TextView nowWorkStatusTxt;

    TeacherDTO teacherDTO;   // 담임 객체
    String status;           // 서버에서 담당 원아 명수 받아와서 저장. 없으면 0
    List<KidsDTO> kidsArray; // 담당 원아 정보 담을 리스트

    String changeVar;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        context = this;

        teacherDTO = new TeacherDTO();  // 담임 객체 초기화
        kidsArray = new ArrayList<KidsDTO>(); // 담당 원아 정보 담을 리스트 초기화

        kinderName = (TextView) findViewById(R.id.kinderName);      // 유치원 이름 띄워주는 텍스트뷰
        className = (TextView) findViewById(R.id.className);        // 반 이름 띄워주는 텍스트뷰
        teacherName = (TextView) findViewById(R.id.teacherName);    // 선생님 이름 띄워주는 텍스트뷰
        messengerBtn = (Button) findViewById(R.id.messengerBtn);    // 메신저 들어가는 버튼
        workStatusBtn = (Button) findViewById(R.id.workStatusBtn);
        nowWorkStatusTxt = (TextView) findViewById(R.id.nowWorkStatusTxt);
        busBtn = (Button) findViewById(R.id.busBtn);

        // MainActivity에서 보낸 데이터 받아옴 ---
        Intent intent = getIntent();

        teacherDTO.setId(intent.getExtras().getString("id"));
        teacherDTO.setName(intent.getExtras().getString("name"));
        teacherDTO.setKinderName(intent.getExtras().getString("kinderName"));
        teacherDTO.setClassName(intent.getExtras().getString("className"));
        teacherDTO.setPhoneNum(intent.getExtras().getString("phoneNum"));
        teacherDTO.setWorkStatus(intent.getExtras().getString("workStatus"));
        teacherDTO.setOffWorkTime(intent.getExtras().getString("offWorkTime"));

        kinderName.setText(teacherDTO.getKinderName());
        className.setText(teacherDTO.getClassName());
        teacherName.setText(teacherDTO.getName());
        // 여기까지 -----------------------------

        // 출퇴근 상태에 따라 workStatus 버튼 텍스트 설정
        if(teacherDTO.getWorkStatus().equals("0")) {
            workStatusBtn.setText("퇴근");
            nowWorkStatusTxt.setText("출근");
        } else {
            workStatusBtn.setText("출근");
            nowWorkStatusTxt.setText("퇴근");
        }
        //

        new TeacherHomeActivity.JSONTask().execute("http://10.0.2.2:3000/getKidsListFromTeacher"); //AsyncTask 시작시킴

        //Toast.makeText(getApplicationContext(), teacherDTO.getOffWorkTime(), Toast.LENGTH_LONG).show();

        // 메신저 버튼 클릭 이벤트
        messengerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅 목록을 띄워주기 위해 ChatList 클래스로 넘어감
                Intent intent = new Intent(TeacherHomeActivity.this, ChatListActivity.class);
                intent.putExtra("teacherDTO", teacherDTO);                  // ChatListActivity로 교사 객체 전달
                intent.putExtra("kidsArray", (Serializable) kidsArray);     // ChatListActivity로 원아 객체 전달
                startActivity(intent);
            }
        });
        // 메신저 버튼 클릭 이벤트 끝

        // 버스 버튼 클릭 이벤트
        busBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅 목록을 띄워주기 위해 ChatList 클래스로 넘어감
                Intent intent = new Intent(TeacherHomeActivity.this, BusActivity.class);
                startActivity(intent);
            }
        });
        //

        // 출퇴근 버튼 클릭 이벤트
        workStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teacherDTO.getWorkStatus().equals("0")) {
                    changeVar = "1";    // db에 변경할 값
                    teacherDTO.setWorkStatus("1");
                    workStatusBtn.setText("출근");
                    nowWorkStatusTxt.setText("퇴근");

                    // 퇴근 시간 설정
                    Date nowDate = new Date();    // 오늘 날짜 생성
                    SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd a hh:mm");
                    teacherDTO.setOffWorkTime(formatter.format(nowDate));
                    //

                    new TeacherHomeActivity.ChangeWorkStatusTask().execute("http://10.0.2.2:3000/changeWorkStatus"); //AsyncTask 시작시킴
                } else {
                    changeVar = "0";    // db에 변경할 값
                    teacherDTO.setWorkStatus("0");
                    workStatusBtn.setText("퇴근");
                    nowWorkStatusTxt.setText("출근");

                    teacherDTO.setOffWorkTime("");   // 현재 교사가 출근중이면 퇴근 시간을 공백으로 설정함

                    new TeacherHomeActivity.ChangeWorkStatusTask().execute("http://10.0.2.2:3000/changeWorkStatus"); //AsyncTask 시작시킴
                }
            }
        });
    }

    public TeacherDTO getTeacherDTO() {
        return this.teacherDTO;
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                org.json.JSONObject jsonObject = new org.json.JSONObject();
                jsonObject.accumulate("id", teacherDTO.getId());     // 담당하고 있는 원아를 찾기위해 교사의 id를 서버로 보냄

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

                // 담당 원아가 없는 경우
                if(status.equals("0")) {
                    Toast.makeText(getApplicationContext(), "담당 원아가 없습니다.", Toast.LENGTH_LONG).show();
                }
                // 담당 원아가 1명 이상 있는 경우
                else {
                    JSONArray jsonArray = (JSONArray)jsonObject.get("rows");
                    for(int i = 0; i < Integer.parseInt(status); i++) {
                        JSONObject returnObject = (JSONObject) jsonArray.get(i);
                        KidsDTO kidsDTO = new KidsDTO();
                        kidsDTO.setIdentifier(returnObject.get("identifier").toString());
                        kidsDTO.setName(returnObject.get("name").toString());
                        kidsDTO.setBirth(returnObject.get("birth").toString());
                        kidsDTO.setAddress(returnObject.get("address").toString());
                        kidsDTO.setKinderName(returnObject.get("kinderName").toString());
                        kidsDTO.setClassName(returnObject.get("className").toString());
                        kidsDTO.setTeacherId(returnObject.get("teacherId").toString());
                        kidsDTO.setParentsId(returnObject.get("parentsId").toString());
                        kidsArray.add(kidsDTO);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 여기까지 ------------------------------
        }
    }

    // 교사 출퇴근 상태 변경
    public class ChangeWorkStatusTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                org.json.JSONObject jsonObject = new org.json.JSONObject();
                jsonObject.accumulate("id", teacherDTO.getId());
                jsonObject.accumulate("changeVar", changeVar);
                jsonObject.accumulate("offWorkTime", teacherDTO.getOffWorkTime());

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
        }
    }
}
