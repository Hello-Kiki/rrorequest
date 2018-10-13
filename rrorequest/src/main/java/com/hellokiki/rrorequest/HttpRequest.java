package com.hellokiki.rrorequest;

import android.text.TextUtils;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/10/13 0013.
 */

public class HttpRequest {

    private Retrofit mBaseRetrofit;

    private String baseUrl;
    private OkHttpClient client;
    private Converter.Factory factory;


    public HttpRequest() {
    }

    public static HttpRequest getInstance() {
        return Helper.httpRequest;
    }

    private static class Helper {
        private static HttpRequest httpRequest = new HttpRequest();
    }

    public void initRetrofit(String baseUrl, OkHttpClient client, Converter.Factory factory) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.factory = factory;
        mBaseRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(factory == null ? GsonConverterFactory.create() : factory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    public Retrofit getBaseRetrofit() {
        return mBaseRetrofit;
    }


    /**
     * 创建新的请求对象
     *
     * @param isUseOldOption 是否使用原来的配置
     * @return
     */
    public Builder newRetrofit(boolean isUseOldOption) {
        return new Builder(isUseOldOption);
    }

    public class Builder {
        boolean isUseOldOption;
        String newBaseUrl;
        OkHttpClient newClient;
        Converter.Factory newFactory;
        Retrofit.Builder newBuilder;

        public Builder(boolean isUseOldOption) {
            this.isUseOldOption = isUseOldOption;
            newBuilder = mBaseRetrofit.newBuilder();
        }

        public Builder baseUrl(String url) {
            this.newBaseUrl = url;
            newBuilder.baseUrl(url);
            return this;
        }

        public Builder addConverterFactory(Converter.Factory factory) {
            this.newFactory = factory;
            newBuilder.addConverterFactory(factory);
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.newClient = client;
            newBuilder.client(client);
            return this;
        }

        public Retrofit build() {
            if (!isUseOldOption) {
                return newBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
            }
            Retrofit.Builder builder = mBaseRetrofit.newBuilder()
                    .baseUrl(TextUtils.isEmpty(newBaseUrl) ? baseUrl : newBaseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            if (newFactory != null) {
                builder.addConverterFactory(newFactory);
            } else if (factory != null) {
                builder.addConverterFactory(factory);
            } else {
                builder.addConverterFactory(GsonConverterFactory.create());
            }
            if (newClient != null) {
                builder.client(newClient);
            } else if (client != null) {
                builder.client(client);
            }
            return builder.build();
        }

    }


}
