package android.example.com.prescriptminder.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.fragments.ProfileDialogFragment;
import android.example.com.prescriptminder.utils.Constants;
import android.example.com.prescriptminder.utils.OkHttpUtils;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 100;

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInButton googleSignInButton;
    private JSONObject loginJson;
    private String userType;
    private boolean isFilled;
    private String status;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        googleSignInButton = findViewById(R.id.signInButton);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                googleSignInButton.setVisibility(View.VISIBLE);
            }
        }, 3000);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                googleSignInButton.setEnabled(false);
            }
        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInClient.silentSignIn()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                handleSignInResult(task);
                            }
                        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(GoogleSignInAccount account)
    {
        if (account != null)
        {
            dismissProgressDialog();
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            if (sharedPref.getBoolean("isFirstTime", true))
            {
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.putExtra("userType", userType);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("userType", userType);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
        else
            googleSignInButton.setEnabled(true);
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            sendIdToken(account);

        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);
            updateUI(null);
        }
    }

    private void sendIdToken(final GoogleSignInAccount account)
    {
        try
        {
            final String url = Constants.BASE_URL + "user/verification/";
            FormBody formBody = new FormBody.Builder().add("idToken", Objects.requireNonNull(account.getIdToken())).build();
            Call call = OkHttpUtils.getOkHttpUtils().sendHttpPostRequest(url, formBody);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        loginJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                        status = loginJson.getString("status");
                        userType = loginJson.getString("user_type");
                        isFilled = loginJson.getBoolean("is_filled");
                        if (status.equalsIgnoreCase("ok"))
                        {
                            if (isFilled)
                                updateUI(account);
                            else
                            {
                                dismissProgressDialog();
                                ProfileDialogFragment profileDialogFragment = ProfileDialogFragment.getProfileDialogFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("userType", userType);
                                profileDialogFragment.setArguments(bundle);
                                profileDialogFragment.setCancelable(false);
                                profileDialogFragment.show(getSupportFragmentManager(), "User");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.e("sendIdToken", e.toString());
        }
    }

    private void dismissProgressDialog()
    {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
