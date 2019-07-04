package com.example.wekid;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;


public class ChatAdapter extends BaseAdapter {
    private static final int ITEM_VIEW_TYPE_MY_TALK = 0;
    private static final int ITEM_VIEW_TYPE_YOUR_TALK = 1;
    private static final int ITEM_VIEW_TYPE_MAX = 2;

    // 아이템을 세트로 담기 위한 array
    private ArrayList<ChatDTO> items = new ArrayList<ChatDTO>();
    private String userName;
    private LayoutInflater inflater = null;

    private TeacherDTO teacherDTO;

    public ChatAdapter() {

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if (items.get(position).getUserName().equals(userName)) {
            type = ITEM_VIEW_TYPE_MY_TALK;
        } else if (!items.get(position).getUserName().equals(userName)) {
            type = ITEM_VIEW_TYPE_YOUR_TALK;
        }

        return type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        int viewType = getItemViewType(position);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ChatDTO chatDTO = items.get(position);

        switch (viewType) {
            case ITEM_VIEW_TYPE_MY_TALK:
                convertView = inflater.inflate(R.layout.chat_msg_my_talk, parent, false);

                TextView my_talk = (TextView) convertView.findViewById(R.id.my_talk);
                TextView my_talk_time = (TextView) convertView.findViewById(R.id.my_talk_time);
                ImageView my_image = (ImageView) convertView.findViewById(R.id.my_image);

                if (chatDTO.getFileName().equals("null")) {
                    my_talk.setText(chatDTO.getMessage());
                    my_talk_time.setText(chatDTO.getDate());
                    my_image.setVisibility(View.GONE);
                } else {
                    Glide.with(context).load(chatDTO.getFileUrl()).override(400,400).into(my_image);
                    my_talk_time.setText(chatDTO.getDate());
                    my_talk.setVisibility(View.GONE);
                }

                break;

            case ITEM_VIEW_TYPE_YOUR_TALK:
                convertView = inflater.inflate(R.layout.chat_msg_your_talk, parent, false);

                TextView your_talk = (TextView) convertView.findViewById(R.id.your_talk);
                TextView your_talk_time = (TextView) convertView.findViewById(R.id.your_talk_time);
                ImageView your_image = (ImageView) convertView.findViewById(R.id.your_image);

                if (chatDTO.getFileName().equals("null")) {
                    your_talk.setText(chatDTO.getMessage());
                    your_talk_time.setText(chatDTO.getDate());
                    your_image.setVisibility(View.GONE);
                } else {
                    Glide.with(context).load(chatDTO.getFileUrl()).override(400,400).into(your_image);
                    your_talk_time.setText(chatDTO.getDate());
                    your_talk.setVisibility(View.GONE);
                }
                break;
        }

        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addItem(String user_name, String message, String date, String fileName, String fileUrl) {
        //Log.i("status : ", ((TeacherHomeActivity)TeacherHomeActivity.context).getTeacherDTO().getWorkStatus());
        //Log.i("퇴근시간 : ", ((TeacherHomeActivity)TeacherHomeActivity.context).getTeacherDTO().getOffWorkTime());

        ChatDTO chatDTO = new ChatDTO();

        chatDTO.setUserName(user_name);
        chatDTO.setMessage(message);
        chatDTO.setDate(date);
        chatDTO.setFileName(fileName);
        chatDTO.setFileUrl(fileUrl);

        if(userName.equals("담임 선생님")) {
            TeacherDTO teacherDTO = ((TeacherHomeActivity)TeacherHomeActivity.context).getTeacherDTO();

            if (teacherDTO.getWorkStatus().equals("0")) {
                items.add(chatDTO); // items에 새로운 KidsItem을 추가한다.
            } else if (teacherDTO.getWorkStatus().equals("1")) {
                // 비교를 위해 메시지를 보낸 시간과 퇴근 버튼을 누른 시간이 필요
                String getChatDate = chatDTO.getDate();
                String getOffWorkTime = teacherDTO.getOffWorkTime();

                // Date 형식으로 변환
                SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd a hh:mm");
                Date chatDate = null;
                Date offWorkTime = null;

                try {
                    chatDate = formatter.parse(getChatDate);
                    offWorkTime = formatter.parse(getOffWorkTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (chatDTO.getUserName().equals(userName)) {
                    items.add(chatDTO);
                } else {
                    // 비교
                    int compare = offWorkTime.compareTo(chatDate);
                    if (compare >= 0) {   // 퇴근하기 전 & 퇴근버튼 누른 시간에 온 메시지들만 출력
                        items.add(chatDTO);
                    }
                }
            }
        } else {
            String teacherStatus = ((ParentsHomeActivity)ParentsHomeActivity.context).selected_teacher_status;
            String teacherOffWorkTime = ((ParentsHomeActivity)ParentsHomeActivity.context).selected_teacher_off_work_time;

            if(teacherStatus.equals("0")) {
                items.add(chatDTO);
            } else {
                // 비교를 위해 메시지를 보낸 시간과 퇴근 버튼을 누른 시간이 필요
                String getChatDate = chatDTO.getDate();
                String getOffWorkTime = teacherOffWorkTime;

                // Date 형식으로 변환
                SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd a hh:mm");
                Date chatDate = null;
                Date offWorkTime = null;

                try {
                    chatDate = formatter.parse(getChatDate);
                    offWorkTime = formatter.parse(getOffWorkTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (chatDTO.getUserName().equals(userName)) {
                    items.add(chatDTO);
                } else {
                    // 비교
                    int compare = offWorkTime.compareTo(chatDate);
                    if (compare >= 0) {   // 퇴근하기 전 & 퇴근버튼 누른 시간에 온 메시지들만 출력
                        items.add(chatDTO);
                    }
                }
            }
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
