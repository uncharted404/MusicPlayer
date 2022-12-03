package com.example.musicplayer.utils;

import com.example.musicplayer.pojo.Music;

import java.util.ArrayList;
import java.util.List;

public class GlobalConstant {
    public static final String BASE_URL = "http://music.eggvod.cn/";

    public static List<Music> musicList = new ArrayList<>();
    public static int position = 0;

    //播放是否成功
    public static boolean flag = true;

    //handler消息
    public static final int REFRESH = 0;
    public static final int INIT = 1;

    //播放顺序
    public static final int ORDER_SINGLE = 0;
    public static final int ORDER_CIRCULATE = 1;
    public static final int ORDER_RANDOM = 2;

    public static int order = ORDER_SINGLE;

    //播放下一个、上一个
    public static final int NEXT = 1;
    public static final int PRIOR = -1;

    public static String nowImageUrl = "url";
}
