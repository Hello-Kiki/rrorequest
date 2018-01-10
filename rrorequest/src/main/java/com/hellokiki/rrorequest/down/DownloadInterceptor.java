package com.hellokiki.rrorequest.down;

import com.hellokiki.rrorequest.ProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by 黄麒羽 on 2017/12/20.
 *
 */

public class DownloadInterceptor implements Interceptor {

    private ProgressListener mListener;

    public DownloadInterceptor() {
    }

    public DownloadInterceptor(ProgressListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response=chain.proceed(chain.request());
        return response.newBuilder().body(new DownProgressResponseBody(response.body(),mListener)).build();
    }
}
