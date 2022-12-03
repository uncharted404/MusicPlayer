package com.example.musicplayer.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.musicplayer.pojo.Music;

import java.util.List;

@Dao
public interface MusicDao {
    @Insert
    void insertMusic(Music music);

    @Query("delete from Music where id = :id")
    void deleteById(int id);

    @Query("select * from Music Order by id desc")
    LiveData<List<Music>> liveSelectAll();

    @Query("select * from Music where songId = :songId")
    List<Music> selectBySondId(int songId);
}
