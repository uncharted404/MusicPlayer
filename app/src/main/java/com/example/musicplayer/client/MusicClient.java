package com.example.musicplayer.client;

import com.example.musicplayer.pojo.MusicInfo;
import retrofit2.Call;
import retrofit2.http.*;

public interface MusicClient {
    @POST("/")
    @FormUrlEncoded
    @Headers({
            "Connection: keep-alive",
            "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<MusicInfo> getMusicInfo(
            @Field("input") String input,
            @Field("type") String type,
            @Field("filter") String filter
    );

    @POST("/")
    @FormUrlEncoded
    @Headers({
            "Connection: keep-alive",
            "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<MusicInfo> getMusicInfoById(
            @Field("input") int input,
            @Field("type") String type,
            @Field("filter") String filter
    );
}
