package com.hellokiki.rrodemo;

import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * Created by 黄麒羽 on 2017/12/14.
 */

public interface ApiService {

    @GET("/api/goods/join_member_goodslist")
    Observable<JsonObject>  getData();

    @GET("/test/test.zip")
    Observable<JsonObject> downFile();


}
