package android.example.com.prescriptminder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class ProfileActivity extends AppCompatActivity {

    private TextView responseText;
    private RequestQueue requestQueue;
    private Button sendButton;
    private EditText urlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        responseText = findViewById(R.id.response);
        sendButton = findViewById(R.id.url_send_button);
        urlEditText = findViewById(R.id.url);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, urlEditText.getText().toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseText.setText(response);
                        showToast("Response received");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseText.setText(error.toString());
                        showToast("Kuch to gadbad hai daya");
                    }
                });
                MySingleton.getInstance(getApplicationContext()).addRequestToQueue(stringRequest);
            }

        });

    }

    public void showToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
