package android.example.com.prescriptminder.utils;

import android.util.Log;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpUtils
{
    private static OkHttpClient okHttpClient;
    private static OkHttpUtils okHttpUtils;

    public static OkHttpUtils getOkHttpUtils()
    {
        if (okHttpUtils == null)
            okHttpUtils = new OkHttpUtils();
        return okHttpUtils;
    }

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null)
            okHttpClient = new OkHttpClient();
        return okHttpClient;
    }

    public Call sendHttpGetRequest(String url)
    {
        try
        {
            OkHttpClient okHttpClient = getOkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            return okHttpClient.newCall(request);
        }
        catch (Exception e)
        {
            Log.e("OkHttpUtils", e.toString());
            return null;
        }
    }

    public Call sendHttpPostRequest(String url, RequestBody requestBody)
    {
        try
        {
            OkHttpClient okHttpClient = getOkHttpClient();
            Request request = new Request.Builder().url(url).post(requestBody).build();
            return okHttpClient.newCall(request);
        }
        catch (Exception e)
        {
            Log.e("OkHttpUtils", e.toString());
            return null;
        }
    }
}
