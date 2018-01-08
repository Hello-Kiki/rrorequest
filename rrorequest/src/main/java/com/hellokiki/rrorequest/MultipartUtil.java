package com.hellokiki.rrorequest;

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

    public List<MultipartBody.Part> makeMultpart(String key, List<File> files){

        MultipartBody.Builder  builder=new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        if(files==null){
            //TODO 抛异常
            return null;
        }
        for (int i=0;i<files.size();i++){
            if(files.get(i)==null){
                //TODO 抛异常
                return null;
            }
            RequestBody body=RequestBody.create(MediaType.parse("multipart/form-data"),files.get(i));
            builder.addFormDataPart(key,files.get(i).getName(),body);

        }
        return builder.build().parts();
    }


}
