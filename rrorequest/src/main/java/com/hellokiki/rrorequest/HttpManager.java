package com.hellokiki.rrorequest;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 黄麒羽 on 2017/12/14.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class HttpManager {

    private static HttpManager mHttpManager;
    public static String BaseUrl;
    public static Converter.Factory mFactory;

    private int CONNENTTIMEOUT = 6;
    private int READTIMEOUT = 20;
    private int WRITETIMEOUT = 20;

    private List<Interceptor> mOkHttp3Interceptor;

    private Retrofit mRetrofit;
    private HttpRequest mHttpRequest;

    private ArrayMap<String, Disposable> mDisposableList = new ArrayMap<>();

    private HttpManager() {
        mOkHttp3Interceptor = new ArrayList<>();
        mHttpRequest = HttpRequest.getInstance();
        initRetrofit();
    }

    public static HttpManager getInstance() {
        if (mHttpManager == null) {
            synchronized (HttpManager.class) {
                mHttpManager = new HttpManager();
            }
        }
        return mHttpManager;
    }

    public static void baseUrl(String url) {
        BaseUrl = url;
    }

    public static void setFactory(Converter.Factory factory) {
        mFactory = factory;
    }

    /**
     * 统一的调度转换器
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }


    private void initRetrofit() {
        mHttpRequest.initRetrofit(BaseUrl, getOkHttpClient(), mFactory);
        mRetrofit = mHttpRequest.getBaseRetrofit();
    }

    /**
     * 获取请求类
     *
     * @return
     */
    public HttpRequest getHttpRequest() {
        if (mHttpRequest == null) {
            mHttpRequest = HttpRequest.getInstance();
        }
        return mHttpRequest;
    }


    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNENTTIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READTIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITETIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        if (mOkHttp3Interceptor != null && mOkHttp3Interceptor.size() > 0) {
            for (int i = 0; i < mOkHttp3Interceptor.size(); i++) {
                builder.addInterceptor(mOkHttp3Interceptor.get(i));
            }
        }
        return builder.build();
    }

    /**
     * 保存订阅信息
     *
     * @param disposable
     */
    void addDisposable(String tag, Disposable disposable) {
        mDisposableList.put(tag, disposable);
    }

    /**
     * 取消全部订阅
     */
    public void cancelAllRequest() {
        for (String key : mDisposableList.keySet()) {
            Disposable disposable = mDisposableList.get(key);
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        mDisposableList.clear();
    }

    /**
     * 取消订阅
     */
    public void cancel(String tag) {
        Disposable disposable=mDisposableList.get(tag);
        if(disposable!=null&&!disposable.isDisposed()){
            disposable.dispose();
        }
        mDisposableList.remove(tag);
    }

}
