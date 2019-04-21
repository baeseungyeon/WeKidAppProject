package com.example.wekid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

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

public class ParentsHomeActivity extends AppCompatActivity {
    TextView kinderName;
    TextView className;
    Spinner kidsNameList;
    Button messengerBtn;

    String id = null;
    String name = null;
    String phoneNum = null;

    String kidsId = null;

    //
    String status;
    List<KidsDTO> kidsArray; // 자기 자식 정보 담을 리스트
    ArrayAdapter<String> adapter;
    List<String> kidsNameArray;  // 자기 자식 이름만 담을 리스트
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_home);

        kinderName = (TextView) findViewById(R.id.kinderName);
        className = (TextView) findViewById(R.id.className);
        kidsNameList = (Spinner) findViewById(R.id.kidsNameList);
        messengerBtn = (Button) findViewById(R.id.messengerBtn);

        // MainActivity에서 보낸 데이터 받아옴 ---
        Intent intent = getIntent();

        id = intent.getExtras().getString("id");
        name = intent.getExtras().getString("name");
        phoneNum = intent.getExtras().getString("phoneNum");
        // 여기까지 -----------------------------

        kidsArray = new ArrayList<KidsDTO>();
        kidsNameArray = new ArrayList<String>();

        new ParentsHomeActivity.JSONTask().execute("http://10.0.2.2:3000/getKidsList"); //AsyncTask 시작시킴

        // 메신저 버튼 클릭 이벤트
        messengerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat_name = "";
                String user_name = "";

                String kids_name = kidsNameList.getSelectedItem().toString();

                for(int i = 0; i < kidsArray.size(); i++) {
                    if(kidsArray.get(i).getName().equals(kids_name)) {  // kidsArray에 있는 애 이름이랑 스피너에서 선택한 애 이름이랑 같으면
                        String teacher_id = kidsArray.get(i).getTeacherId();
                        String kids_identifier = kidsArray.get(i).getIdentifier();

                        chat_name = teacher_id + "|" + kids_identifier;  // 채팅방 이름은 교사아이디|아이식별자
                        user_name = kids_name + " 학부모님";
                    }
                }

                Intent intent = new Intent(ParentsHomeActivity.this, ChatActivity.class);
                intent.putExtra("chatName", chat_name);
                intent.putExtra("userName", user_name);
                startActivity(intent);
            }
        });

        // 스피너 변경 이벤트 (자식 변경 이벤트)
        //
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                org.json.JSONObject jsonObject = new org.json.JSONObject();
                jsonObject.accumulate("id", id);     // 자기 자식을 찾기 위해 id를 서버로 보냄

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

                // 자식이 없는 경우
                if(status.equals("0")) {
                    Toast.makeText(getApplicationContext(), "없음", Toast.LENGTH_LONG).show();
                }
                // 자식이 있는 경우
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
                        kidsNameArray.add(returnObject.get("name").toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 여기까지 ------------------------------

            // 스피너에 자식 목록 띄우는거
            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, kidsNameArray);
            kidsNameList.setAdapter(adapter);
            // 여기까지 ------------------------------

            // 텍스트박스에 첫번째 자식 기준으로 유치원 이름, 반 이름
            kinderName.setText(kidsArray.get(0).getKinderName());
            className.setText(kidsArray.get(0).getClassName());
            // 여기까지 ------------------------------
        }
    }
}
