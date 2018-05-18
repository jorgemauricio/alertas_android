package com.example.jorge.audios;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView soundPool, mediaPlayer;
    SoundPool sp;
    MediaPlayer mp;
    int Sonido_de_Reproduccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPool = (ImageView) findViewById(R.id.SoundPool);
        soundPool.setOnClickListener(this);
        mediaPlayer = (ImageView) findViewById(R.id.MediaPlayer);
        mediaPlayer.setOnClickListener(this);
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC,1);
        //mp = MediaPlayer.create(this, R. ??? );
        //Sonido_de_Reproduccion = sp.load(this, ??? , 1);
    }

    @Override
    public void onClick(View v) {
    if (v == mediaPlayer){
        AudioMediaPlayer();
    }if (v == soundPool){

        }
    }

    public void AudioMediaPlayer(){
        mp.start();
    }

    public void AudioSoundPool(){
        sp.play(Sonido_de_Reproduccion, 1,1,1,0,0);
    }
}
