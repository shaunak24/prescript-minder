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

    public static Response uploadAudio(File file, String patient_mail, String doctor_mail) throws IOException {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("patient_mail", patient_mail)
                .addFormDataPart("medicine", "Shaunak")
                .addFormDataPart("doctor_mail", doctor_mail).build();
        okhttp3.Request request_1 = new okhttp3.Request.Builder().url(Constants.BASE_URL + "prescript/store/")
                .post(requestBody).build();
        okhttp3.Response response_1 = client.newCall(request_1).execute();
        String[] splits = response_1.body().string().split("/");
        Log.e("tag", splits[splits.length - 1]);

        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", splits[splits.length - 1], RequestBody.create(MEDIA_TYPE_MP3, file)).build();
        okhttp3.Request request_2 = new okhttp3.Request.Builder().url(Constants.BASE_URL + "prescript/uploadaudio/")
                .post(multipartBody).build();
        okhttp3.Response response_2 = client.newCall(request_2).execute();
        Log.d("tag", response_1.toString());
        Log.d("tag", response_2.toString());

        if (!response_1.isSuccessful() || !response_2.isSuccessful()) {
            throw new IOException("Unexpected code " + response_1 + response_2);
        }
        return response_2;
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
