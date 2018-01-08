package com.hellokiki.rrorequest.down;

import com.google.gson.JsonObject;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by 黄麒羽 on 2017/12/21.
 */

public interface DownLoadApi {

    @Streaming
    @GET
    Observable<ResponseBody> downFile(@Header("RANGE")String start, @Url String url);

}
