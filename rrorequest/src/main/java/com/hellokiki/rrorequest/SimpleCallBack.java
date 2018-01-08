package com.hellokiki.rrorequest;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by 黄麒羽 on 2017/12/15.
 */

public abstract class SimpleCallBack<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T o) {
        onSuccess(o);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(T t);
    public abstract void onFailure(Throwable e);

}
