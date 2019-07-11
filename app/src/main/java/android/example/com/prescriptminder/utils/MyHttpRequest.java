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

    public static final MediaType WAV = MediaType.parse("audio/x-wav");

    public static Response uploadAudio(File file, String patient_mail, String doctor_mail) throws IOException {

        Log.e("Upload audio method", "In Upload audio method");
        OkHttpClient client = new OkHttpClient();

        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("patient_mail", patient_mail)
                .addFormDataPart("medicine", "")
                .addFormDataPart("doctor_mail", doctor_mail)
                .addFormDataPart("file", "hi", RequestBody.create(WAV, file))
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder().url(Constants.BASE_URL + "prescript/store/")
                .post(multipartBody).build();
        okhttp3.Response response = client.newCall(request).execute();
        Log.e("Upload audio method", "In Upload audio method");
        Log.d("tag", response.body().string());
        Log.e("Upload audio method", "In Upload audio method");

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
