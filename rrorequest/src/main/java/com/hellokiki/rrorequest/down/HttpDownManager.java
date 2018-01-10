package com.hellokiki.rrorequest.down;

import com.hellokiki.rrorequest.HttpManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 黄麒羽 on 2017/12/20.
 * 下载管理者
 */
public class HttpDownManager {

    private String mBaseUrl;
    private Set<DownInfo> mInfoSets;
    private Map<String,HttpDownCallBack> mCallBackMap;
    private int mDownThreadCount=3;         //多线程下载开启的线程数

    private static HttpDownManager mHttpDownManager;
    private int CONNECT_TIME_OUT = 6;     //全局连接超时时间(秒)
    private int READ_TIME_OUT = 20;       //全局读取超时时间(秒)
    private int WRITE_TIME_OUT = 20;      //全局写入超时时间(秒)

    private boolean mIsDeleteFileForShop=false;      //调用shop方法是否删除为完成的文件


    private HttpDownManager() {
        mBaseUrl=HttpManager.BaseUrl;   //默认用HttpManager的BaseUrl
        mCallBackMap=new HashMap<>();
        mInfoSets=new HashSet<>();
    }

    public static HttpDownManager getInstance(){
        if(mHttpDownManager==null){
            synchronized (HttpDownManager.class){
                mHttpDownManager=new HttpDownManager();
            }
        }
        return mHttpDownManager;
    }

    public void setBaseUrl(String baseUrl){
        this.mBaseUrl=baseUrl;
    }

    /**
     * 开始下载
     * @param info  下载信息
     * @param listener  监听
     */
    public void start(final DownInfo info,HttpDownListener listener){
        start(info,listener,false);
    }

    /**
     * 开始下载
     * @param info  下载信息
     * @param listener  监听
     *  @param isMoreThread 是否多线程下载
     */
    public void start(final DownInfo info,HttpDownListener listener,boolean isMoreThread){

        if(info==null||info.getUrl()==null){
            return;
        }
        if(mCallBackMap.get(info.getUrl())!=null){
            mCallBackMap.get(info.getUrl()).setDownInfo(info);
            return;
        }

        if(info.getReadLength()==info.getCountLength()){
            info.setReadLength(0);
        }

        info.setListener(listener);
        HttpDownCallBack callBack=new HttpDownCallBack(info);

        callBack.setOnResultListener(new HttpDownCallBack.onResultListener() {
            @Override
            public void onFinish(DownInfo info) {
                mCallBackMap.remove(info.getUrl());
            }

            @Override
            public void onError(DownInfo info) {
                mCallBackMap.remove(info.getUrl());
            }
        });

        DownloadInterceptor interceptor=new DownloadInterceptor(callBack);

        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true);

        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build();

        if(listener!=null){
            listener.onStart();
        }

        downLoad(retrofit,info.getReadLength(),info).subscribe(callBack);


        callBack.onStart();

        info.setState(DownState.START);
        mInfoSets.add(info);
        mCallBackMap.put(info.getUrl(),callBack);
    }

    /**
     * 单线程下载
     * @param retrofit Retrofit
     * @param start 开始位置
     * @param info 下载信息
     * @return Observable
     */
    private Observable downLoad(Retrofit retrofit, long start, final DownInfo info){
        return  retrofit.create(DownLoadApi.class).downFile("bytes=" + start + "-", info.getUrl())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, DownInfo>() {

                    @Override
                    public DownInfo apply(@NonNull ResponseBody responseBody) throws Exception {
                        writeToCaches(responseBody,info);
                        return info;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 多线程下载，默认3个线程
     * @param retrofit Retrofit
     * @param start 开始位置
     * @param end   结束位置
     * @param info 下载信息
     * @return Observable
     */
//    private Observable downLoad(final Retrofit retrofit, long start, final long end, final DownInfo info, final HttpDownCallBack callBack){
//        String str = "";
//        if (end == -1) {
//            str = "";
//        } else {
//            str = end + "";
//        }
//        return  retrofit.create(DownLoadApi.class).downFile("bytes=" + start + "-"+str, info.getUrl())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .map(new Function<ResponseBody, ResponseBody>() {
//                    @Override
//                    public ResponseBody apply(@NonNull ResponseBody responseBody) throws Exception {
//                        return responseBody;
//                    }
//                })
//                .observeOn(Schedulers.computation()).doOnNext(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(@NonNull ResponseBody responseBody) throws Exception {
//
//                        Log.e("2017","responseBody--length=="+responseBody.contentLength());
//                        if (end == -1) {
//                            long interval=responseBody.contentLength()/info.getDownThreadCount();
//                            Log.e("2017","平均-》 "+interval);
//
//                            downLoad(retrofit,0,interval,info,callBack).mergeWith(downLoad(retrofit,interval,interval*2,info,callBack))
//                                    .mergeWith(downLoad(retrofit,interval*2,responseBody.contentLength(),info,callBack)).subscribe(callBack);
//
//                        }else{
//                            writeToCaches(responseBody,info);
//                        }
//
//                    }
//                }).observeOn(AndroidSchedulers.mainThread());
//    }


    /**
     * 暂停
     * @param info  下载信息
     * @return  暂停状态的下载信息
     */
    public DownInfo pause(DownInfo info){
        if(info!=null){
            HttpDownCallBack callBack=mCallBackMap.get(info.getUrl());
            if(callBack!=null){
                callBack.getDisposable().dispose();
                callBack.onPause();
                info.setState(DownState.PAUSE);
                mCallBackMap.remove(info.getUrl());
            }
        }
        return info;
    }

    /**
     * 停止
     * @param info 下载信息
     * @return 停止状态的下载信息
     */
    public DownInfo stop(DownInfo info){
        if(info!=null){
            HttpDownCallBack callBack=mCallBackMap.get(info.getUrl());
            if(callBack!=null){
                callBack.getDisposable().dispose();
                callBack.onShop();
                info.setState(DownState.STOP);
                mCallBackMap.remove(info.getUrl());

                if(mIsDeleteFileForShop){
                    File file=new File(info.getSavePath());
                    if(file.exists()){
                        file.delete();
                    }
                }
            }
        }
        return info;
    }

    /**
     * 暂停全部
     * @return DownInfo
     */
    public Set<DownInfo> pauseAll(){
        Set<DownInfo> infos=mInfoSets;
        for (DownInfo info:infos){
            pause(info);
        }
        mInfoSets.clear();
        mCallBackMap.clear();
        return infos;
    }

    /**
     * 停止全部
     * @return DownInfo
     */
    public Set<DownInfo> shopAll(){
        Set<DownInfo> infos=mInfoSets;
        for (DownInfo info:infos){
            stop(info);
        }
        mInfoSets.clear();
        mCallBackMap.clear();
        return infos;
    }



    private void writeToCaches(ResponseBody body,DownInfo info){

        RandomAccessFile randomAccessFile=null;
        FileChannel fileCahnnel=null;
        InputStream inputStream=null;

        File file=new File(info.getSavePath());
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try {
            long allLength = 0 == info.getCountLength() ? body.contentLength() : info.getReadLength() + body
                    .contentLength();

            randomAccessFile=new RandomAccessFile(file,"rwd");
            fileCahnnel=randomAccessFile.getChannel();
            inputStream=body.byteStream();
            MappedByteBuffer byteBuffer=fileCahnnel.map(FileChannel.MapMode.READ_WRITE,info.getReadLength(), allLength - info.getReadLength());

            byte[] buffer=new byte[1024*4];
            int len;
            while((len=inputStream.read(buffer))!=-1){
                byteBuffer.put(buffer,0,len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if(inputStream!=null) {
                        inputStream.close();
                    }
                    if(fileCahnnel!=null){
                        fileCahnnel.close();
                    }
                    if(randomAccessFile!=null){
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
            }
        }

    }

    /**
     * 设置是否调用shop删除未完成文件
     * @param isDeleteFile
     */
    public void setUseShopIsDeleteFile(boolean isDeleteFile){
        this.mIsDeleteFileForShop=isDeleteFile;
    }

    /**
     * 设置全局连接超时时间(秒)
     */
    public void setConnectTimeOut(int timeOut){
        this.CONNECT_TIME_OUT=timeOut;
    }

    /**
     * 设置全局读取超时时间(秒)
     */
    public void setReadTimeOut(int timeOut){
        this.READ_TIME_OUT=timeOut;
    }

    /**
     * 设置全局写入超时时间(秒)
     */
    public void setWriteTimeOut(int timeOut){
        this.WRITE_TIME_OUT=timeOut;
    }



}
