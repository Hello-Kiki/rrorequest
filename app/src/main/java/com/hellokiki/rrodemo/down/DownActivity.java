package com.hellokiki.rrodemo.down;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hellokiki.rrodemo.R;
import com.hellokiki.rrorequest.down.DownInfo;
import com.hellokiki.rrorequest.down.HttpDownListener;
import com.hellokiki.rrorequest.down.HttpDownManager;

import java.util.ArrayList;
import java.util.List;

public class DownActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private DownListAdapter mAdapter;
    private List<DownInfo> mInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down);
        mRecyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        initData();

    }


    private void initData(){
        mInfoList=new ArrayList<>();

        DownInfo downInfo=new DownInfo();
        downInfo.setUrl("http://192.168.137.147:8080/test/hello.mp4");
        downInfo.setSavePath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/rro/test1.mp4");
        mInfoList.add(downInfo);

        DownInfo downInfo2=new DownInfo();
        downInfo2.setUrl("http://192.168.137.147:8080/test/hello2.mp4");
        downInfo2.setSavePath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/rro/test2.mp4");
        mInfoList.add(downInfo2);

        DownInfo downInfo3=new DownInfo();
        downInfo3.setUrl("http://192.168.137.147:8080/test/hello3.mp4");
        downInfo3.setSavePath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/rro/test3.mp4");
        mInfoList.add(downInfo3);

        DownInfo downInfo4=new DownInfo();
        downInfo4.setUrl("http://192.168.137.147:8080/test/hello4.mp4");
        downInfo4.setSavePath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/rro/test4.mp4");
        mInfoList.add(downInfo4);


        Log.e("2017","info-length="+mInfoList.size());
        mAdapter=new DownListAdapter(this,mInfoList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
    }
}
