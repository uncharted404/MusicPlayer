package com.example.musicplayer.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.musicplayer.R;
import com.example.musicplayer.factory.SingletonFactory;
import com.example.musicplayer.pojo.Music;
import com.example.musicplayer.utils.GlobalConstant;
import com.example.musicplayer.utils.MyMPlayer;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {
    private final MyMPlayer mPlayer = SingletonFactory.getMPlayer();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //更新播放器ui
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == GlobalConstant.REFRESH){

                Music music = SingletonFactory.getMusic();
                if (music.getTitle() != null){
                    if(!GlobalConstant.nowImageUrl.equals(music.getImageUrl())){
                        ImageView image = getActivity().findViewById(R.id.image_album);
                        //设置图片
                        Picasso.with(getContext()).load(music.getImageUrl()).into(image);
                        GlobalConstant.nowImageUrl = music.getImageUrl();
                    }

                    TextView title = getActivity().findViewById(R.id.title);
                    //设置歌曲名、歌手
                    title.setText(new StringBuilder()
                            .append(music.getTitle())
                            .append("——")
                            .append(music.getAuthor()).toString());
                }

                SeekBar thumb = getActivity().findViewById(R.id.sb_thumb);
                TextView curTimeBtn = getActivity().findViewById(R.id.tv_cur_time);
                TextView totalTimeBtn = getActivity().findViewById(R.id.tv_total_time);

                int curTime = mPlayer.getCurTime();
                int totalTime = mPlayer.getTotalTime();

                //设置总时长
                totalTimeBtn.setText(mPlayer.totalTimeToString());
                //设置音符位置
                thumb.setProgress((curTime*100)/totalTime);
                //设置现在时长
                curTimeBtn.setText(mPlayer.curTimeToString());
            }
        }
    };

    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
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

        //handler刷新播放器
        SingletonFactory.getExecutor().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    handler.sendEmptyMessage(GlobalConstant.REFRESH);
                }
            }
        }, 1000, 100, TimeUnit.MILLISECONDS);

        //异步，手动拖动进度
        SingletonFactory.getExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                SeekBar thumb = getActivity().findViewById(R.id.sb_thumb);
                thumb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if(mPlayer.isPlaying()){
                            int curProgress = seekBar.getProgress();
                            int totalTime = mPlayer.getTotalTime();
                            mPlayer.setProgress((curProgress*totalTime)/100);
                        }
                    }
                });
            }
        }, 1000, TimeUnit.MILLISECONDS);

        return inflater.inflate(R.layout.fragment_player, container, false);
    }
}