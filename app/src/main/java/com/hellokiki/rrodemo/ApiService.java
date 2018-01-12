package com.hellokiki.rrodemo;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;

/**
 * Created by 黄麒羽 on 2017/12/14.
 */

public interface ApiService {

    @GET("/api/goods/join_member_goodslist")
    Observable<JsonObject>  getData();

    @GET("/test/test.zip")
    Observable<JsonObject> downFile();

    @Multipart
    @POST("")
    Observable<JsonObject> uploadFile(@PartMap Map<String, RequestBody> map, @Part List<MultipartBody.Part> part);

}
