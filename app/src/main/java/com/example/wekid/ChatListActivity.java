package com.example.wekid;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private TeacherDTO teacherDTO;
    private List<KidsDTO> kidsArray;
    private ListView chatListView;

    private String chat_name = "";
    private String user_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // TeacherHomeActivity에서 전달한 값 받기
        teacherDTO = (TeacherDTO) getIntent().getParcelableExtra("teacherDTO");
        Serializable serial = getIntent().getSerializableExtra("kidsArray");
        kidsArray = (ArrayList<KidsDTO>) serial;
        //

        // 위젯과 멤버변수 참조 획득
        chatListView = (ListView) findViewById(R.id.chatListView);

        // 아이템 추가 및 어댑터 등록
        dataSetting();

        // 리스트뷰 클릭 이벤트 - ChatRoomActivity로 이동함
        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chat_name = teacherDTO.getId() + "|" + kidsArray.get(position).getIdentifier();  // 채팅방 이름은 교사아이디|아이식별자
                user_name = "담임 선생님";

                Intent intent = new Intent(ChatListActivity.this, ChatRoomActivity.class);
                intent.putExtra("chatName", chat_name);
                intent.putExtra("userName", user_name);
                startActivity(intent);
            }
        });
        // 리스트뷰 클릭 이벤트 끝
    }

    private void dataSetting() {
        ChatListAdapter adapter = new ChatListAdapter();

        for(int i = 0; i < kidsArray.size(); i++) {
            adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.profile2), kidsArray.get(i).getName() + " 학부모", "contents_" + i);
        }

        // 리스트뷰에 어댑터 등록
        chatListView.setAdapter(adapter);
    }
}
