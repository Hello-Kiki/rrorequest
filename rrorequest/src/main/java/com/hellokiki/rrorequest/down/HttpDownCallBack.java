package com.hellokiki.rrorequest.down;

import android.util.Log;

import com.hellokiki.rrorequest.ProgressListener;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.HashMap;

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
    private onResultListener mResultListener;


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
        mInfo.setState(DownState.START);
        if(mListener!=null){
            mListener.onStart();
        }
    }

    public void onPause(){
        mInfo.setState(DownState.PAUSE);
        if(mListener!=null){
            mListener.onPause(mInfo.getReadLength());
        }
    }

    public void onShop(){
        mInfo.setState(DownState.STOP);
        if(mListener!=null){
            mListener.onStop(mInfo.getReadLength());
        }
    }


    @Override
    public void onError(Throwable e) {
        mInfo.setState(DownState.ERROR);
        if(mListener!=null){
            mListener.onError(mInfo,e.toString());
        }
        if(mResultListener!=null){
            mResultListener.onError(mInfo);
        }
    }

    @Override
    public void onComplete() {
        mInfo.setState(DownState.FINISH);
        if(mListener!=null){
            mListener.onFinish(mInfo);
        }
        if(mResultListener!=null){
            mResultListener.onFinish(mInfo);
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
            if(mInfo.getState()==DownState.PAUSE||mInfo.getState()==DownState.STOP){
                return;
            }else{
                Observable.just(read).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Log.e("2018","progress = "+aLong);
                        mListener.onProgress(aLong,mInfo.getCountLength());
                    }
                });
            }

        }
    }

    public Disposable getDisposable() {
        return mDisposable;
    }

    public void setOnResultListener(onResultListener listener){
        mResultListener=listener;
    }

    public interface onResultListener{
        void onFinish(DownInfo info);
        void onError(DownInfo info);
    }

}
