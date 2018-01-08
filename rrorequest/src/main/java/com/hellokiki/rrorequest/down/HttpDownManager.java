package com.hellokiki.rrorequest.down;

import android.util.Log;

import com.google.gson.JsonObject;
import com.hellokiki.rrorequest.HttpManager;
import com.hellokiki.rrorequest.ProgressListener;

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

    private static HttpDownManager mHttpDownManager;
    private int CONNENTTIMEOUT = 6;
    private int READTIMEOUT = 20;
    private int WRITETIMEOUT = 20;

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

        Log.e("2017","info = "+info.toString());

        if(info==null||info.getUrl()==null){
            return;
        }
        if(mCallBackMap.get(info.getUrl())!=null){
            mCallBackMap.get(info.getUrl()).setDownInfo(info);
            return;
        }
        Log.e("2017","info22 = "+info.toString());

        File file=new File(info.getSavePath());
        Log.e("2017",file.exists()+"");
        Log.e("2017",file.getAbsolutePath());
        Log.e("2017",file.getPath());

        info.setListener(listener);
        HttpDownCallBack callBack=new HttpDownCallBack(info);

        DownloadInterceptor interceptor=new DownloadInterceptor(callBack);

        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(CONNENTTIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READTIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITETIMEOUT, TimeUnit.SECONDS)
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
        Log.e("2017","断点=="+info.getReadLength());

        retrofit.create(DownLoadApi.class).downFile("bytes=" + info.getReadLength() + "-", info.getUrl())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, DownInfo>() {

                    @Override
                    public DownInfo apply(@NonNull ResponseBody responseBody) throws Exception {
                        writeToCaches(responseBody,info);
                        return info;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack);

        callBack.onStart();
        info.setState(DownState.START);
        mInfoSets.add(info);
        mCallBackMap.put(info.getUrl(),callBack);
    }


    /**
     * 暂停
     * @param info  下载信息
     * @return  暂停状态的下载信息
     */
    public DownInfo pause(DownInfo info){
        if(info!=null){
            HttpDownCallBack callBack=mCallBackMap.get(info.getUrl());
            callBack.getDisposable().dispose();
            callBack.onPause();
            info.setState(DownState.PAUSE);
            mCallBackMap.remove(info.getUrl());
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
        return info;
    }

    /**
     * 暂停全部
     * @return
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
     * @return
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





}
