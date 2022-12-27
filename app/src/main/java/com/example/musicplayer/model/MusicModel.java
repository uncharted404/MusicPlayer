package com.example.musicplayer.model;

import android.app.Application;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import com.example.musicplayer.Dao.MusicDao;
import com.example.musicplayer.database.MusicDatabase;
import com.example.musicplayer.factory.SingletonFactory;
import com.example.musicplayer.pojo.Music;

import java.util.List;

public class MusicModel extends AndroidViewModel {
    private static MusicDatabase musicDatabase;
    private final MusicDao musicDao;
    private final LiveData<List<Music>> listLiveData;

    private Application app;


    public MusicModel(@NonNull Application application) {
        super(application);

        app = application;
        if (musicDatabase == null){
            musicDatabase = Room.databaseBuilder(
                            application.getApplicationContext(),
                            MusicDatabase.class,
                            "music_database")
                    .build();
        }
        musicDao = musicDatabase.getMusicDao();
        listLiveData = musicDao.liveSelectAll();
    }

    public LiveData<List<Music>> liveFindAllMusic(){
        return listLiveData;
    }

    public void addMusic(Music music){
        SingletonFactory.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Music> musicList = musicDao.selectBySondId(music.getSongId());
                if (!musicList.isEmpty()){
                    Looper.prepare();
                    Toast.makeText(app, "已收藏", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                musicDao.insertMusic(music);
                Looper.prepare();
                Toast.makeText(app, "收藏成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }

    public void deleteMusic(int id){
        SingletonFactory.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                musicDao.deleteById(id);
                Looper.prepare();
                Toast.makeText(app, "删除成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }
}
