package com.hellokiki.rrorequest;

import android.text.TextUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by 黄麒羽 on 2017/12/15.
 */

public abstract class SimpleCallBack<T> implements Observer<T> {

    private Disposable mDisposable;
    private String mTag;

    public SimpleCallBack() {
    }

    /**
     * @param tag Disposable保存的key ,当key为空时不保存
     */
    public SimpleCallBack(String tag) {
        mTag = tag;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        if (!TextUtils.isEmpty(mTag)) {
            HttpManager.getInstance().addDisposable(mTag, mDisposable);
        }
    }

    @Override
    public void onNext(T o) {
        onSuccess(o);
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            if (!TextUtils.isEmpty(mTag)) {
                HttpManager.getInstance().cancel(mTag);
                mTag = null;
            }
        }

    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            if (!TextUtils.isEmpty(mTag)) {
                HttpManager.getInstance().cancel(mTag);
                mTag = null;
            }
        }
    }

    @Override
    public void onComplete() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            if (!TextUtils.isEmpty(mTag)) {
                HttpManager.getInstance().cancel(mTag);
                mTag = null;
            }
        }
    }

    public abstract void onSuccess(T t);

    public abstract void onFailure(Throwable e);

}
