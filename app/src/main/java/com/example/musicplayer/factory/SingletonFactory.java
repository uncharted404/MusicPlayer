package com.example.musicplayer.factory;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.example.musicplayer.pojo.Music;
import com.example.musicplayer.model.MusicModel;
import com.example.musicplayer.utils.GlobalConstant;
import com.example.musicplayer.utils.MyMPlayer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;

public class SingletonFactory {
    private static final MyMPlayer mPlayer = new MyMPlayer();
    private static final Music music = new Music();
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    private static Retrofit retrofit;
    private static MusicModel musicModel;

    public static synchronized Retrofit getRetrofit() {
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(GlobalConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static MyMPlayer getMPlayer(){
        return mPlayer;
    }

    public static Music getMusic(){
        return music;
    }

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }

    public static synchronized MusicModel getMusicModel(ViewModelStoreOwner owner) {
        if (musicModel == null){
            musicModel = new ViewModelProvider(owner).get(MusicModel.class);
        }
        return musicModel;
    }

    public static void insertMusicInfo(Music music) {
        SingletonFactory.music.setId(music.getId());
        SingletonFactory.music.setSongId(music.getSongId());
        SingletonFactory.music.setTitle(music.getTitle());
        SingletonFactory.music.setAuthor(music.getAuthor());
        SingletonFactory.music.setImageUrl(music.getImageUrl());
    }
}
