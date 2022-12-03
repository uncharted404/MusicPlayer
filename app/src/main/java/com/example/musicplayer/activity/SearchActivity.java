package com.example.musicplayer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MySearchListAdapter;
import com.example.musicplayer.factory.SingletonFactory;
import com.example.musicplayer.pojo.MusicInfo;
import com.example.musicplayer.pojo.Music;
import com.example.musicplayer.client.MusicClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private List<Music> list;
    private Retrofit retrofit = SingletonFactory.getRetrofit();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        listView = findViewById(R.id.list_search);

        //listview点击事件，播放所选音乐
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View listView, int position, long id) {
                Music music = list.get(position);

                SingletonFactory.insertMusicInfo(music);
                SingletonFactory.getMPlayer().start(music.getMusicUrl(), SearchActivity.this);
            }
        });

        String search = getIntent().getStringExtra("search");
        showMusicInfo(search);
    }


    //获得音乐列表数据、展示
    private void showMusicInfo(String search) {
        MusicClient musicClient = retrofit.create(MusicClient.class);
        Call<MusicInfo> call = musicClient.getMusicInfo(search, "netease", "name");
        call.enqueue(new Callback<MusicInfo>() {

            @Override
            public void onResponse(Call<MusicInfo> call, Response<MusicInfo> response) {
                if (response.isSuccessful()){
                    MusicInfo musicInfo = response.body();
                    list = new ArrayList<>();
                    for (MusicInfo.DataBean data : musicInfo.getData()) {

                        Music music = new Music();
                        music.setSongId(data.getSongid());
                        music.setTitle(data.getTitle());
                        music.setAuthor(data.getAuthor());
                        music.setMusicUrl(data.getUrl());
                        music.setImageUrl(data.getPic());
                        list.add(music);
                    }
                    MySearchListAdapter imageListAdapter = new MySearchListAdapter(
                            SearchActivity.this,
                            R.layout.list_item_search,
                            list
                    );
                    listView.setAdapter(imageListAdapter);
                }
            }

            @Override
            public void onFailure(Call<MusicInfo> call, Throwable t) {

            }
        });
    }
}