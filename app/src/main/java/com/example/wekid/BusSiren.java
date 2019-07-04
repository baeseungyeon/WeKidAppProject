package com.example.wekid;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class BusSiren extends AppCompatActivity {

    Button virationBtt;
    Button beepBtt;
    Button bttsafe;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_siren);


        bttsafe=(Button)findViewById(R.id.btt_safe);
        final Vibrator vib=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        vib.vibrate(
                new long[]{100,1000,100,500,100,500,100,1000}
                , 0);
        mediaPlayer = MediaPlayer.create(BusSiren.this, R.raw.siren);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        bttsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 정지
                mediaPlayer.stop();
                // 초기화
                mediaPlayer.reset();
                vib.cancel(); //
                onBackPressed();
            }
        });
    }
}

