package com.iloveplan.android.asis.util;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class HttpUtil {

    public static <T> T request(String uri, Type typeOfT) throws Exception {
        return request(uri, typeOfT, new ArrayList<NameValuePair>());
    }

    public static <T> T request(String uri, Type typeOfT, List<NameValuePair> fields) throws Exception {
        return new Gson().fromJson(request(uri, fields), typeOfT);
    }

    public static String request(String uri) throws Exception {
        return request(uri, new ArrayList<NameValuePair>());
    }

    public static String request(String uri, List<NameValuePair> fields) throws Exception {

//        // 요청정보를 설정합니다.
//        HttpPost httpPost = new HttpPost(Settings.HTTP_URL + uri);
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(fields, Settings.ENCODING);
//        httpPost.setEntity(entity);
//
//        // 타임아웃(3초)을 설정합니다.
//        int timeout = 3000;
//        HttpParams params = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(params, timeout);
//        HttpConnectionParams.setSoTimeout(params, timeout);
//
//        // 요청페이지를 호출합니다.
//        return EntityUtils.toString(new DefaultHttpClient(params).execute(httpPost).getEntity());

        return null;
    }
}
