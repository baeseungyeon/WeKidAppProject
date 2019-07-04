package com.example.wekid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ChatSendImageActivity extends AppCompatActivity {
    ImageView send_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_send_image);

        send_image =  (ImageView) findViewById(R.id.send_image);


    }
}
