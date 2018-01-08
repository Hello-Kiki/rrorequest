package com.hellokiki.rrorequest.down;

import com.hellokiki.rrorequest.ProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by 黄麒羽 on 2017/12/20.
 */

public class ProgressResponseBody extends ResponseBody {

    private ResponseBody mBody;
    private ProgressListener mListener;
    private BufferedSource mBufferedSource;

    public ProgressResponseBody(ResponseBody mBody, ProgressListener mListener) {
        this.mBody = mBody;
        this.mListener = mListener;
    }

    @Override
    public MediaType contentType() {
        return mBody.contentType();
    }

    @Override
    public long contentLength() {
        return mBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if(mBufferedSource==null){
            mBufferedSource= Okio.buffer(source(mBody.source()));
        }
        return mBufferedSource;
    }


    public Source source(Source source) {

        return new ForwardingSource(source) {

            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead == -1 ? 0 : bytesRead;
                if(mListener!=null){
                    mListener.onProgress(totalBytesRead,mBody.contentLength(),bytesRead==-1);
                }
                return bytesRead;
            }
        };

    }

}
