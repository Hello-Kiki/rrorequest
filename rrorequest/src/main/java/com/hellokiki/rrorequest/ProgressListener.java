package com.hellokiki.rrorequest;

/**
 * Created by 黄麒羽 on 2017/12/20.
 */

public interface ProgressListener {

    void onProgress(long read,long length,boolean done);
}
