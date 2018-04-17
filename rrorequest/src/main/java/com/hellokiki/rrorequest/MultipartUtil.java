package com.hellokiki.rrorequest;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by 黄麒羽 on 2017/12/15.
 */

public class MultipartUtil {

    private static MultipartUtil multipartUtil;

    private Map<String, String> maps = new HashMap<>();

    public MultipartUtil() {

    }

    public static MultipartUtil newInstance() {
        multipartUtil = new MultipartUtil();
        return multipartUtil;
    }

    public MultipartUtil addParam(String key, String value) {
        maps.put(key, value);
        return multipartUtil;
    }

    public Map<String, RequestBody> Build() {
        Map<String, RequestBody> bodyMap = new HashMap<>();
        for (String key : maps.keySet()) {
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), maps.get(key));
            bodyMap.put(key, body);
        }
        return bodyMap;
    }


    public static List<MultipartBody.Part> makeMultpart(String key, @NonNull List<File> files) {
        return makeMultpart(key, files, null);
    }

    public static MultipartBody.Part makeMultpart(String key, @NonNull File file) {
        return makeMultpart(key, file, null);
    }

    public static MultipartBody.Part makeMultpart(String key, @NonNull File file, ProgressListener listener) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (file==null||!file.exists()) {
            try {
                throw new RequestException("文件为null");
            } catch (RequestException e) {
                e.printStackTrace();
            }
            return null;
        }
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        UpLoadProgressRequestBody upLoadBody = new UpLoadProgressRequestBody(body, listener);
        builder.addFormDataPart(key,file.getName(), upLoadBody);
        return builder.build().parts().get(0);
    }


    /**
     * @param key      文件key
     * @param files    文件
     * @param listener 文件上传进度监听，只支持一个文件的时候使用，多文件不生效
     * @return List<MultipartBody.Part>
     */
    public static List<MultipartBody.Part> makeMultpart(String key, @NonNull List<File> files, ProgressListener listener) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (files.size() > 1) {
            listener = null;
        }
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i) == null) {
                try {
                    throw new RequestException("文件为null");
                } catch (RequestException e) {
                    e.printStackTrace();
                }
                return null;
            }

            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), files.get(i));
            UpLoadProgressRequestBody upLoadBody = new UpLoadProgressRequestBody(body, listener);
            builder.addFormDataPart(key, files.get(i).getName(), upLoadBody);
        }
        return builder.build().parts();
    }

}
