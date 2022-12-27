package com.example.musicplayer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MyFSPAdapter;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.factory.SingletonFactory;
import com.example.musicplayer.fragment.MyLikeFragment;
import com.example.musicplayer.fragment.PlayerFragment;
import com.example.musicplayer.pojo.Music;
import com.example.musicplayer.pojo.MusicInfo;
import com.example.musicplayer.utils.GlobalConstant;
import com.example.musicplayer.model.MusicModel;
import com.example.musicplayer.utils.MyMPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;

    private MusicModel musicModel;
    private final MyMPlayer mPlayer = SingletonFactory.getMPlayer();
    private final Retrofit retrofit = SingletonFactory.getRetrofit();

    //初始化
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == GlobalConstant.INIT){
                initData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicModel = SingletonFactory.getMusicModel(MainActivity.this);

        viewPager = findViewById(R.id.vp);
        bottomNavigationView = findViewById(R.id.bottom_menu);

        //异步初始化
        SingletonFactory.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(GlobalConstant.INIT);
            }
        });

        mPlayer.getInstance().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playByOrder(GlobalConstant.NEXT);

            }
        });
    }

    public void like(View view) {
        Music music = SingletonFactory.getMusic();
        if (music.getImageUrl() == null){
            Toast.makeText(this, "请重试", Toast.LENGTH_SHORT).show();
            return;
        }
        musicModel.addMusic(music);
    }

    public void changeOrder(View view) {
        GlobalConstant.order = (GlobalConstant.order+1) % 3;
        Button order = findViewById(R.id.btn_order);
        switch (GlobalConstant.order){
            case GlobalConstant.ORDER_SINGLE:
                order.setBackgroundResource(R.drawable.order_single);
                break;
            case GlobalConstant.ORDER_CIRCULATE:
                order.setBackgroundResource(R.drawable.order_circulate);
                break;
            case GlobalConstant.ORDER_RANDOM:
                order.setBackgroundResource(R.drawable.order_random);
                break;
        }
    }

    public void prior(View view) {
        playByOrder(GlobalConstant.PRIOR);
    }

    public void play(View view) {
        if (SingletonFactory.getMusic().getTitle() == null){
            playByOrder(GlobalConstant.NEXT);
        }
        else{
            SingletonFactory.getMPlayer().play();
        }

    }

    public void next(View view) {
        playByOrder(GlobalConstant.NEXT);
    }



    private void playByOrder(int status){
        List<Music> musicList = GlobalConstant.musicList;
        int size = musicList.size();

        if (size == 0){
            Toast.makeText(this, "你还未收藏歌曲", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (GlobalConstant.order){
            case GlobalConstant.ORDER_SINGLE:
                break;
            case GlobalConstant.ORDER_CIRCULATE:
                GlobalConstant.position = (GlobalConstant.position+size+status) % size;
                break;
            case GlobalConstant.ORDER_RANDOM:
                Random random = new Random();
                GlobalConstant.position = random.nextInt(size);
                break;
        }
        Music music = musicList.get(GlobalConstant.position);

        playMusicBySongId(music.getSongId());
    }

    private void initData() {
        List<Fragment> fragmentList = new ArrayList<>();

        PlayerFragment playerFragment = PlayerFragment.newInstance("播放器", "");
        MyLikeFragment myLikeFragment = MyLikeFragment.newInstance("我的喜欢", "");

        fragmentList.add(playerFragment);
        fragmentList.add(myLikeFragment);

        MyFSPAdapter myFSPAdapter = new MyFSPAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(myFSPAdapter);

        SingletonFactory.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if(position == 0){
                            bottomNavigationView.setSelectedItemId(R.id.menu_player);
                        }
                        else{
                            bottomNavigationView.setSelectedItemId(R.id.menu_like);
                        }

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });

        SingletonFactory.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(item.getItemId() == R.id.menu_player){
                            viewPager.setCurrentItem(0);
                        }
                        else{
                            viewPager.setCurrentItem(1);
                        }

                        return true;
                    }
                });
            }
        });
    }

    private void playMusicBySongId(int songId) {
        MusicService musicClient = retrofit.create(MusicService.class);
        Call<MusicInfo> call = musicClient.getMusicInfoById(songId, "netease", "name");
        call.enqueue(new Callback<MusicInfo>() {

            @Override
            public void onResponse(Call<MusicInfo> call, Response<MusicInfo> response) {
                if (response.isSuccessful()){
                    GlobalConstant.flag = true;
                    MusicInfo musicInfo = response.body();
                    MusicInfo.DataBean data = musicInfo.getData().get(0);

                    Music music = new Music();
                    music.setSongId(data.getSongid());
                    music.setTitle(data.getTitle());
                    music.setAuthor(data.getAuthor());
                    music.setMusicUrl(data.getUrl());
                    music.setImageUrl(data.getPic());

                    SingletonFactory.insertMusicInfo(music);
                    mPlayer.start(music.getMusicUrl(), MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<MusicInfo> call, Throwable t) {
                Toast.makeText(MainActivity.this, "播放失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //搜索
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SearchActivity.class);
                intent.putExtra("search", query);
                startActivity(intent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}