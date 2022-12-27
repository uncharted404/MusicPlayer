package com.example.musicplayer.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MyLikeListAdapter;
import com.example.musicplayer.service.MusicService;
import com.example.musicplayer.factory.SingletonFactory;
import com.example.musicplayer.model.MusicModel;
import com.example.musicplayer.pojo.Music;
import com.example.musicplayer.pojo.MusicInfo;
import com.example.musicplayer.utils.GlobalConstant;
import com.example.musicplayer.utils.MyMPlayer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyLikeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyLikeFragment extends Fragment {

    private final MyMPlayer mPlayer = SingletonFactory.getMPlayer();
    private final Retrofit retrofit = SingletonFactory.getRetrofit();

    private MusicModel musicModel;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public MyLikeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyLikeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyLikeFragment newInstance(String param1, String param2) {
        MyLikeFragment fragment = new MyLikeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        musicModel = SingletonFactory.getMusicModel(this);

        return inflater.inflate(R.layout.fragment_my_like, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView rvList = getActivity().findViewById(R.id.rv_list);
        //设置样式：布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(layoutManager);

        //自动刷新RecyclerView上的数据
        musicModel.liveFindAllMusic().observe(getActivity(), new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> musicList) {
                GlobalConstant.musicList = musicList;
                MyLikeListAdapter myLikeListAdapter = new MyLikeListAdapter(musicList);
                rvList.setAdapter(myLikeListAdapter);

                //RecyclerView点击事件，播放所选音乐
                myLikeListAdapter.setOnItemClickListener(new MyLikeListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Music music) {
                        GlobalConstant.position = position;
                        SingletonFactory.insertMusicInfo(music);
                        playMusicBySongId(music.getSongId());
                    }
                });

                //RecyclerView上的button的点击事件，删除所选音乐的数据
                myLikeListAdapter.setOnItemClickDeleteListener(new MyLikeListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Music music) {
                        deleteMusic(music.getId());
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
                    MusicInfo musicInfo = response.body();
                    MusicInfo.DataBean data = musicInfo.getData().get(0);

                    Music music = new Music();
                    music.setSongId(data.getSongid());
                    music.setTitle(data.getTitle());
                    music.setAuthor(data.getAuthor());
                    music.setMusicUrl(data.getUrl());
                    music.setImageUrl(data.getPic());

                    SingletonFactory.insertMusicInfo(music);
                    mPlayer.start(music.getMusicUrl(), getContext());

                }
            }

            @Override
            public void onFailure(Call<MusicInfo> call, Throwable t) {
                Toast.makeText(getContext(), "播放失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMusic(int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("你确定要删除吗");
        builder.setCancelable(true);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                musicModel.deleteMusic(id);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}