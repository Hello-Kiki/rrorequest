package com.hellokiki.rrorequest.down;

import android.util.Log;

import com.hellokiki.rrorequest.ProgressListener;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by 黄麒羽 on 2017/12/20.
 */

public class HttpDownCallBack<T> implements Observer<T> ,ProgressListener{
    private HttpDownListener mListener;
    private Disposable mDisposable;
    private long mCurrentRead;
    private DownInfo mInfo;


    public HttpDownCallBack(DownInfo info) {
        this.mInfo=info;
        this.mListener = info.getListener();
    }

    public void setDownInfo(DownInfo info){
        this.mInfo=info;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable=d;
    }

    @Override
    public void onNext(T t) {

    }

    public void onStart(){
        if(mListener!=null){
            mListener.onStart();
        }
    }

    public void onPause(){
        if(mListener!=null){
            mListener.onPause(mInfo.getReadLength());
        }
    }

    public void onShop(){
        if(mListener!=null){
            mListener.onStop(mInfo.getReadLength());
        }
    }


    @Override
    public void onError(Throwable e) {
        if(mListener!=null){
            mListener.onError(e.toString());
        }
    }

    @Override
    public void onComplete() {
        if(mListener!=null){
            mListener.onFinish();
        }
    }

    @Override
    public void onProgress(long read, long length, final boolean done) {

        if(mInfo.getCountLength()>length){
            read=mInfo.getCountLength()-length+read;
        }else{
            mInfo.setCountLength(length);
        }
        mInfo.setReadLength(read);

        if(mListener!=null){
            Observable.just(read).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                @Override
                public void accept(@NonNull Long aLong) throws Exception {
                    mListener.onProgress(aLong,mInfo.getCountLength());
                }
            });

        }
    }

    public Disposable getDisposable() {
        return mDisposable;
    }
}
