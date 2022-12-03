package com.example.musicplayer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.format.Time;
import android.widget.Toast;
import com.example.musicplayer.factory.SingletonFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyMPlayer {
    private final MediaPlayer mediaPlayer;

    public MyMPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void start(String url, Context context){
        if (url == null){
            Toast.makeText(context, "请重试", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }


        mediaPlayer.prepareAsync();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }

    public void play(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
        else {
            mediaPlayer.pause();
        }
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void setProgress(int time){
        mediaPlayer.seekTo(time);
    }

    public int getTotalTime(){
        return mediaPlayer.getDuration();
    }

    public int getCurTime(){
        return mediaPlayer.getCurrentPosition();
    }

    public String totalTimeToString(){
        return timeToString(mediaPlayer.getDuration());
    }

    public String curTimeToString(){
        return timeToString(mediaPlayer.getCurrentPosition());
    }

    private String timeToString(long time){
        Date date = new Date(time);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");

        return format.format(date);
    }

    public MediaPlayer getInstance(){
        return mediaPlayer;
    }
}
