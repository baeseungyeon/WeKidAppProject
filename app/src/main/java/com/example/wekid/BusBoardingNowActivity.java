package com.example.wekid;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.Timer;

public class BusBoardingNowActivity extends AppCompatActivity {
    private int sum = 0;    // 현재 인식되는 비콘의 갯수

    private String status;  // 탑승 원아의 수
    private TextView numOfKidsTxt;
    private ListView boardingListView;
    private Button restartBtn;

    private Spinner busNowSpinner;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList arrayList;

    JSONArray jsonArray = null;
    BeaconDTO beaconDTO = null;
    List<BeaconDTO> beaconArray = null; // 인식되는 비콘 정보 담을 리스트
    List<String> boardingList = null;
    List<String> kidsNameArray = null;
    List<String> kidsClassNameArray = null;
    ArrayAdapter<String> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_boarding_now);

        numOfKidsTxt = (TextView) findViewById(R.id.numOfKidsTxt);
        boardingListView = (ListView) findViewById(R.id.boardingListView);
        restartBtn = (Button) findViewById(R.id.restartBtn);

        beaconArray = new ArrayList<BeaconDTO>();
        kidsNameArray = new ArrayList<String>();
        kidsClassNameArray = new ArrayList<String>();
        beaconDTO = new BeaconDTO();

        busNowSpinner = (Spinner) findViewById(R.id.busNowSpinner);

        // 스피너에 버스 목록 띄우는거
        arrayList = new ArrayList<>();
        arrayList.add("1호차");
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList);
        busNowSpinner.setAdapter(arrayAdapter);
        // 여기까지 ------------------------------


        //데이터를 저장하게 되는 리스트
        boardingList = new ArrayList<String>();

        //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, boardingList);

        //리스트뷰의 어댑터를 지정해준다.
        boardingListView.setAdapter(adapter);

        new BusBoardingNowActivity.JSONTask().execute("http://10.0.2.2:3000/getBusBoardingNow"); //AsyncTask 시작시킴

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardingList.clear();

                new BusBoardingNowActivity.JSONTask().execute("http://10.0.2.2:3000/getBusBoardingNow");
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                //org.json.JSONObject jsonObject = new org.json.JSONObject();
                //jsonObject.accumulate("search", "1");     // 탑승중(isBoarding = 1)인 원아를 찾기 위함

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
                    //writer.write(jsonObject.toString());
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
                status = jsonObject.get("status").toString();   // 탑승중인 원아의 수

                // 탑승원아가 없는 경우
                if(status.equals("0")) {
                    //Toast.makeText(getApplicationContext(), "탑승중인 원아가 없습니다.", Toast.LENGTH_LONG).show();
                    boardingList.add("탑승중인 원아가 없습니다.");

                    adapter.notifyDataSetChanged();
                } else {
                    jsonArray = (JSONArray)jsonObject.get("rows");

                    beaconDTO = new BeaconDTO();
                    JSONObject returnObject;

                    for(int i = 0; i < Integer.parseInt(status); i++) {
                        returnObject = (JSONObject) jsonArray.get(i);

                        //beaconDTO.setKidsName(returnObject.get("name").toString());
                        //beaconDTO.setKidsClassName(returnObject.get("className").toString());

                        //kidsNameArray.add(beaconDTO.getKidsName());
                        //kidsClassNameArray.add(beaconDTO.getKidsClassName());

                        //beaconArray.add(beaconDTO);
                        boardingList.add(returnObject.get("className").toString()+"반 " + returnObject.get("name").toString());
                    }

                    for(int j = 0; j < beaconArray.size(); j++) {
                        boardingList.add(beaconArray.get(j).getKidsClassName()+"반 " + beaconArray.get(j).getKidsName());
                    }

                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 여기까지 ------------------------------

            numOfKidsTxt.setText(status);
        }
    }
}
