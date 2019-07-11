package android.example.com.prescriptminder.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyHttpRequest {

    private static final MediaType MEDIA_TYPE_MP3 = MediaType.parse("audio/mpeg");

    public static Response uploadAudio(File file) throws IOException {

        Log.d("tag", "In uploadAudio() method");
        OkHttpClient client = new OkHttpClient();

        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "Hi", RequestBody.create(MEDIA_TYPE_MP3, file)).build();

        okhttp3.Request request = new okhttp3.Request.Builder().url("http://192.168.43.250:8000/audio_db/" + file.getName() + "/upload/")
                .post(multipartBody).build();
        okhttp3.Response response = client.newCall(request).execute();
        Log.d("tag", response.toString());

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return response;
    }

    public static File downloadAudio(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).get().build();
        okhttp3.Response response = client.newCall(request).execute();

        InputStream inputStream = response.body().byteStream();

        File receivedFile = new File(Environment.getExternalStorageDirectory(), "audio1");//DateFormat.getDateTimeInstance().format(new Date()));
        OutputStream outputStream = new FileOutputStream(receivedFile);
        try {
            byte[] buffer = new byte[4 * 1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        } finally {
            outputStream.close();
        }

        return receivedFile;
    }
}
