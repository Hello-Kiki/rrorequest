package com.hellokiki.rrorequest;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by 黄麒羽 on 2017/12/15.
 */

public class MultipartUtil {

    public MultipartUtil() {

    }

    public List<MultipartBody.Part> makeMultpart(String key,@NonNull List<File> files){

        MultipartBody.Builder  builder=new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (int i=0;i<files.size();i++){
            if(files.get(i)==null){
                try {
                    throw new RequestException("文件为null");
                } catch (RequestException e) {
                    e.printStackTrace();
                }
                return null;
            }

            RequestBody body=RequestBody.create(MediaType.parse("multipart/form-data"),files.get(i));
            UpLoadProgressRequestBody upLoadBody=new UpLoadProgressRequestBody(body);
            builder.addFormDataPart(key,files.get(i).getName(),upLoadBody);

        }
        return builder.build().parts();
    }


}
