package com.hellokiki.rrorequest.down;

/**
 * Created by 黄麒羽 on 2017/12/20.
 */

public interface HttpDownListener {

    void onStart();

    void onPause(long read);

    void onStop(long read);

    void onFinish(DownInfo info);

    void onError(DownInfo info,String s);

    void onProgress(long currentRead,long addLength);

}
