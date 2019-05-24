package android.example.com.prescriptminder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity {

    private Button sendButton;
    private TextView responseText;
    private EditText urlEditText;
    private ImageView imageView;
    private static final MediaType MEDIA_TYPE_MP3 = MediaType.parse("audio/mpeg");
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sendButton = findViewById(R.id.url_send_button);
        responseText = findViewById(R.id.response);
        urlEditText = findViewById(R.id.url);
        imageView = findViewById(R.id.imageView);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendJSON();
                String path = Environment.getExternalStorageDirectory() + "/nusta";
                file = new File(path);
                responseText.setText(file.getName());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            uploadAudio(file, "nusta");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }

    public void uploadAudio(File file, String fileName) throws IOException {

        OkHttpClient client = new OkHttpClient();

        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MEDIA_TYPE_MP3, file)).build();

        okhttp3.Request request = new okhttp3.Request.Builder().url("http://192.168.43.250:8000/audio_db/upload_audio/")
                .post(multipartBody).build();
        okhttp3.Response response = client.newCall(request).execute();
        //responseText.setText(response.toString());

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void sendJSON() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlEditText.getText().toString().trim(),
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        responseText.setText(response);
                        Log.d("Response", response);
                        showToast("Hua");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        responseText.setText(error.toString());
                        Log.d("Error.Response", error.toString());
                        showToast("Nahi hua");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("username", "shaunak24");
                params.put("password", "nusta123");
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addRequestToQueue(postRequest);
        Bitmap bmp = QRCodeUtil.encodeAsBitmap("Shaunak", 800, 800);
        imageView.setImageBitmap(bmp);
    }
}
