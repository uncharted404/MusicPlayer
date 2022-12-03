package com.example.musicplayer.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.musicplayer.Dao.MusicDao;
import com.example.musicplayer.pojo.Music;

@Database(entities = {Music.class}, version = 1, exportSchema = false)
public abstract class MusicDatabase extends RoomDatabase {
    public abstract MusicDao getMusicDao();
}
