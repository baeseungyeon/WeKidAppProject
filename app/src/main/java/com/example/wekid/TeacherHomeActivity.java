package com.example.wekid;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TeacherHomeActivity extends AppCompatActivity {
    TextView kinderName;
    TextView className;
    TextView teacherName;
    Button messengerBtn;

    TeacherDTO teacherDTO;   // 담임 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        teacherDTO = new TeacherDTO();  // 담임 객체

        kinderName = (TextView) findViewById(R.id.kinderName);
        className = (TextView) findViewById(R.id.className);
        teacherName = (TextView) findViewById(R.id.teacherName);
        messengerBtn = (Button) findViewById(R.id.messengerBtn);

        // MainActivity에서 보낸 데이터 받아옴 ---
        Intent intent = getIntent();

        teacherDTO.setId(intent.getExtras().getString("id"));
        teacherDTO.setName(intent.getExtras().getString("name"));
        teacherDTO.setKinderName(intent.getExtras().getString("kinderName"));
        teacherDTO.setClassName(intent.getExtras().getString("className"));
        teacherDTO.setPhoneNum(intent.getExtras().getString("phoneNum"));

        kinderName.setText(teacherDTO.getKinderName());
        className.setText(teacherDTO.getClassName());
        teacherName.setText(teacherDTO.getName());
        // 여기까지 -----------------------------

        new TeacherHomeActivity.JSONTask().execute("http://10.0.2.2:3000/getKidsListFromTeacher"); //AsyncTask 시작시킴

        // 메신저 버튼 클릭 이벤트
        messengerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat_name = "";
                String user_name = "";

                //String kids_name = "송민국";

                chat_name = teacherDTO.getId() + "|0";  // 채팅방 이름은 교사아이디|아이식별자
                user_name = "담임 선생님";

                /*for(int i = 0; i < kidsArray.size(); i++) {
                    if(kidsArray.get(i).getName().equals(kids_name)) {  // kidsArray에 있는 애 이름이랑 스피너에서 선택한 애 이름이랑 같으면
                        String teacher_id = kidsArray.get(i).getTeacherId();
                        String kids_identifier = kidsArray.get(i).getIdentifier();

                        chat_name = teacher_id + "|" + kids_identifier;  // 채팅방 이름은 교사아이디|아이식별자
                        user_name = kids_name + " 학부모님";
                    }
                }*/

                Intent intent = new Intent(TeacherHomeActivity.this, ChatActivity.class);
                intent.putExtra("chatName", chat_name);
                intent.putExtra("userName", user_name);
                startActivity(intent);
            }
        });
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

        }
    }
}
