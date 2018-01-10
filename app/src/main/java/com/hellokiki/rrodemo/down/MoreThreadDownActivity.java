package com.hellokiki.rrodemo.down;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hellokiki.rrodemo.R;
import com.hellokiki.rrorequest.down.DownInfo;
import com.hellokiki.rrorequest.down.HttpDownListener;
import com.hellokiki.rrorequest.down.HttpDownManager;

//多线程下载
public class MoreThreadDownActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar mProgressBar;
    private TextView mTextViewProgress;
    private Button mButtonDown;

    private DownInfo mDownInfo;
    private HttpDownManager manager;

    String url="http://p.gdown.baidu.com/1d8c55276f085552a1724cdf2ed7002eb7944c74875d2eb637c123c1f9470e6e21529437bf46f9c5c8edc345211a5824c58f29b5df2eeb115aff3e91dd529997d800fd1e7600cf21706a5c2fea70f826b436959b6402346bb4648e53a499c6445236415e70da089ae93a66950937e7a869f7ea5f0cbc60af0de15fa81430ada2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_thread_down);

        mProgressBar= (ProgressBar) findViewById(R.id.progress);
        mTextViewProgress= (TextView) findViewById(R.id.text_view_progress);
        mButtonDown= (Button) findViewById(R.id.button_down);
        mButtonDown.setOnClickListener(this);
        findViewById(R.id.button_shop).setOnClickListener(this);

        manager=HttpDownManager.getInstance();
        mDownInfo=new DownInfo();
        mDownInfo.setUrl(url);
        mDownInfo.setSavePath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/rro/qq.apk");

    }

    public void startDown(){
        if(mDownInfo==null||"".equals(mDownInfo.getUrl())){
            return;
        }

        manager.start(mDownInfo, new HttpDownListener() {
            @Override
            public void onStart() {
                mButtonDown.setText("暂停");
            }

            @Override
            public void onPause(long read) {
                mDownInfo.setReadLength(read);
                mButtonDown.setText("继续");
                mTextViewProgress.setText("下载暂停");
            }

            @Override
            public void onStop(long read) {
                mDownInfo.setReadLength(read);
                mButtonDown.setText("下载");
                mTextViewProgress.setText("下载停止");
            }

            @Override
            public void onFinish(DownInfo info) {
                mDownInfo=info;
                mButtonDown.setText("下载");
                mTextViewProgress.setText("下载成功");
            }

            @Override
            public void onError(DownInfo info,String s) {
                mDownInfo=info;
                mButtonDown.setText("下载");
                mTextViewProgress.setText("下载失败");
            }

            @Override
            public void onProgress(long currentRead, long addLength) {
                int pro=(int)(currentRead*100/addLength);
                mProgressBar.setProgress(pro);
                mTextViewProgress.setText("下载中："+pro+"%");
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_down:
                startDown();
                break;
            case R.id.button_shop:
                manager.pause(mDownInfo);
                break;
        }
    }
}
