package com.hellokiki.rrodemo;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by HelloKiki on 2018/4/17.
 */

public class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody,T> {
    @Override
    public T convert(ResponseBody value) throws IOException {
        return null;
    }
}
