package com.example.wekid;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {
    private String CHAT_NAME;
    private String USER_NAME;
    private String USER_ID;
    //private String teacherStatus;   // 교사 출퇴근 현황

    private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;
    private Button file_send;
    private TextView notice_txt;

    private ChatAdapter chatAdapter;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private Uri filePath;
    private String fileName;
    private StorageReference storageRef;
    private String fileUrl;

    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatAdapter = new ChatAdapter();

        // 위젯 ID 참조
        chat_view = (ListView) findViewById(R.id.chat_view);
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_send);
        file_send = (Button) findViewById(R.id.file_send);
        notice_txt = (TextView) findViewById(R.id.notice_txt);

        // 로그인 화면에서 받아온 채팅방 이름, 유저 이름, 유저 id 저장
        Intent intent = getIntent();
        CHAT_NAME = intent.getStringExtra("chatName");
        USER_NAME = intent.getStringExtra("userName");
        USER_ID = intent.getStringExtra("userId");
        //teacherStatus = intent.getStringExtra("teacherStatus");
        //Toast.makeText(getApplicationContext(), teacherStatus, Toast.LENGTH_SHORT).show();

        chatAdapter.setUserName(USER_NAME);

        // 채팅 방 입장
        openChat(CHAT_NAME);

        notice_txt.setVisibility(View.GONE);

        /*
        // 교사 퇴근시 알림창 띄우기
        TextView notice_txt = (TextView) findViewById(R.id.notice_txt);

        if(USER_NAME.equals("담임 선생님")) {
            TeacherDTO teacherDTO = ((TeacherHomeActivity)TeacherHomeActivity.context).getTeacherDTO();

            if(teacherDTO.getWorkStatus().equals("0")) {
                notice_txt.setVisibility(View.GONE);
            }
        } else {
            //String teacherStatus = ((ParentsHomeActivity)ParentsHomeActivity.context).selected_teacher_status;
            //Log.i("first : ", teacherStatus);
            if(!teacherStatus.equals("-100")) {
                notice_txt.setVisibility(View.GONE);
            }
        }
        //*/

        // 메시지 전송 버튼에 대한 클릭 리스너 지정
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_edit.getText().toString().equals(""))
                    return;

                // 시간 가져오기
                Date nowDate = new Date();    // 오늘 날짜 생성
                SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd a hh:mm");
                String date = formatter.format(nowDate);
                //

                ChatDTO chat = new ChatDTO(USER_NAME, chat_edit.getText().toString(), date, "null", "null"); //ChatDTO를 이용하여 데이터를 묶는다.
                databaseReference.child("chat").child(CHAT_NAME).push().setValue(chat); // 데이터 푸쉬
                chat_edit.setText(""); //입력창 초기화

            }
        });

        // 파일 전송 버튼에 대한 클릭 리스너 지정
        file_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();

            uploadFile();
        }
    }

    //upload the file
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드 중");
            progressDialog.show();

            ContentResolver cR = getApplicationContext().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String fileType = mime.getExtensionFromMimeType(cR.getType(filePath));

            //storage
            storage = FirebaseStorage.getInstance();

            //파일명 지정
            SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd a hh:mm");
            Date now = new Date();

            String fileSendDate = formatter.format(now);

            fileName = CHAT_NAME + "_" + USER_NAME + "_" + fileSendDate + "." + fileType;
            //storage 주소와 폴더 파일명을 지정해 준다.
            storageRef = storage.getReferenceFromUrl("gs://wekidchat.appspot.com").child("images/" + fileName);

            //올리기
            StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // url 가져오기 ------------------------------------------------------------
                            final StorageReference ref = storageRef;
                            UploadTask uploadTask = ref.putFile(filePath);

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return ref.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        fileUrl = downloadUri.toString();
                                        Log.i("gilegile : ", fileUrl);

                                        // 시간 가져오기
                                        Date nowDate = new Date();    // 오늘 날짜 생성
                                        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd a hh:mm");
                                        String date = formatter.format(nowDate);
                                        //

                                        ChatDTO chat = new ChatDTO(USER_NAME, "null", date, fileName, fileUrl); //ChatDTO를 이용하여 데이터를 묶는다.
                                        databaseReference.child("chat").child(CHAT_NAME).push().setValue(chat); // 데이터 푸쉬
                                        chat_edit.setText(""); //입력창 초기화
                                    } else {
                                        // Handle failures
                                        // ...
                                    }
                                }
                            });
                            // 가이드 : https://firebase.google.com/docs/storage/android/upload-files?hl=ko
                            // -----------------------------------------------------------------------------

                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기

                            //Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다.
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void addMessage(DataSnapshot dataSnapshot, ChatAdapter adapter) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        adapter.addItem(chatDTO.getUserName(), chatDTO.getMessage(), chatDTO.getDate(), chatDTO.getFileName(), chatDTO.getFileUrl());
        //Log.i("test1 : " , USER_NAME + " " + chatDTO.getUserName() + " " + chatDTO.getMessage() + " " + chatDTO.getDate() + " " + chatDTO.getFileName());
        adapter.notifyDataSetChanged();
        chat_view.setSelection(chatAdapter.getCount() - 1);
    }

    private void removeMessage(DataSnapshot dataSnapshot, ChatAdapter adapter) {
        //ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        //adapter.remove(chatDTO.getUserName() + " : " + chatDTO.getMessage());
    }

    private void openChat(String chatName) {
        // 리스트 어댑터 생성 및 세팅
        /*
        final ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);a*/
        chat_view.setAdapter(chatAdapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("chat").child(chatName).addChildEventListener(new ChildEventListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessage(dataSnapshot, chatAdapter);
                Log.e("LOG", "s:"+s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeMessage(dataSnapshot, chatAdapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}