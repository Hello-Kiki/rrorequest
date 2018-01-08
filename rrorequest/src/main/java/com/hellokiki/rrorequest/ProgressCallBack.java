package com.hellokiki.rrorequest;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by 黄麒羽 on 2017/12/18.
 */

public abstract class ProgressCallBack<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    public abstract void onProgress(long currentLength, long allLength);
    public abstract void onSuccess(T t);
    public abstract void onFailure(Throwable e);

}
