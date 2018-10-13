package com.hellokiki.rrodemo;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hellokiki.rrodemo.down.DownActivity;
import com.hellokiki.rrorequest.HttpManager;
import com.hellokiki.rrorequest.MultipartUtil;
import com.hellokiki.rrorequest.ProgressListener;
import com.hellokiki.rrorequest.SimpleCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button_goto_down).setOnClickListener(this);

        mTextView= (TextView) findViewById(R.id.text_view);

        HttpManager.baseUrl("http://zd.gzrcqf.com");
//        HttpManager.baseUrl("https://gank.io");
    }


    public void request(){
        //https://gank.io/api/xiandu/data/id/appinn/count/10/page/1
        HttpManager.getInstance().create(ApiService.class).getData()
                .compose(HttpManager.<JsonObject>applySchedulers())
                .subscribe(new SimpleCallBack<JsonObject>("123") {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        Log.e("2017","成功-->"+jsonObject.toString());
                        mTextView.setText(jsonObject.toString());
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e("2017","失败-->"+e.toString());
                    }
                });
        HttpManager.getInstance().cancel("1213");
        HttpManager.getInstance().cancelAllRequest();
    }


    public void upload(){

        Map<String,RequestBody> textBody=MultipartUtil.newInstance()
                .addParam("text1","123")
                .addParam("text2","456")
                .Build();

        List<File> files=new ArrayList<>();
        File file=new File(Environment.getExternalStorageDirectory()+"test.png");
        files.add(file);

        //文件上传进度只支持单文件上传的时候使用
        List<MultipartBody.Part> parts= MultipartUtil.makeMultpart("images", files, new ProgressListener() {
            @Override
            public void onProgress(long read, long length, boolean done) {

            }
        });
        HttpManager.getInstance().create(ApiService.class).uploadFile(textBody,parts)
                .compose(HttpManager.<JsonObject>applySchedulers())
                .subscribe(new SimpleCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        //请求成功
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        //请求失败
                    }
                });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                request();
                break;
            case R.id.button_goto_down:
                Intent intent=new Intent(this,DownActivity.class);
                startActivity(intent);
                break;
        }

    }
}
