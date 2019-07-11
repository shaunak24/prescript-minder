package android.example.com.prescriptminder.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.fragments.BluetoothConnectFragment;
import android.example.com.prescriptminder.fragments.ProfileFragment;
import android.example.com.prescriptminder.fragments.RecentScanFragment;
import android.example.com.prescriptminder.fragments.RecordFragment;
import android.example.com.prescriptminder.fragments.ScanFragment;
import android.example.com.prescriptminder.utils.Constants;
import android.example.com.prescriptminder.utils.MyHttpRequest;
import android.example.com.prescriptminder.utils.OkHttpUtils;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static FragmentManager fragmentManager;
    private BottomNavigationView navigation;
    public static Fragment fragment;
    private final BluetoothConnectFragment bluetoothConnectFragment = BluetoothConnectFragment.getBluetoothConnectFragment();
    private final ProfileFragment profileFragment = ProfileFragment.getProfileFragment();
    private final RecentScanFragment recentScanFragment = RecentScanFragment.getRecentScanFragment();
    private final RecordFragment recordFragment = RecordFragment.getRecordFragment();
    private final ScanFragment scanFragment = ScanFragment.getScanFragment();
    private ProgressDialog progressDialog;
    private final int REQUEST_PERMISSIONS = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_connect_device:
                    if (fragment != bluetoothConnectFragment)
                        replaceFragment(bluetoothConnectFragment);
                    return true;
                case R.id.navigation_profile:
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Loading");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                    final String email = Objects.requireNonNull(googleSignInAccount).getEmail();
                    final String personName = googleSignInAccount.getGivenName() + " " + googleSignInAccount.getFamilyName();
                    final String profilePicture = Objects.requireNonNull(googleSignInAccount.getPhotoUrl()).toString();

//                    OkHttpClient okHttpClient = new OkHttpClient();
//                    Request request = new Request.Builder().url(url).get().build();
                    String url = Constants.BASE_URL + "user/get/" + email + "/";
                    Call call = OkHttpUtils.getOkHttpUtils().sendHttpGetRequest(url);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            try {
                                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("email", email);
                                editor.putString("personName", personName);
                                editor.putString("profilePicture", profilePicture);
                                editor.putString("gender", jsonObject.getString("gender"));
                                editor.putString("city", jsonObject.getString("city"));
                                editor.putString("birthday", jsonObject.getString("birthday"));
                                editor.apply();

                                dismissProgressDialog();

                                if (fragment != profileFragment)
                                    replaceFragment(profileFragment);
                            } catch (Exception e) {
                                Log.e("onResponse", e.toString());
                            }
                        }
                    });
//                    if (fragment != profileFragment)
//                        replaceFragment(profileFragment);
                    return true;
                case R.id.navigation_recent_scan:
                    if (fragment != recentScanFragment)
                        replaceFragment(recentScanFragment);
                    return true;
                case R.id.navigation_record_audio:
                    if (fragment != recordFragment)
                        replaceFragment(recordFragment);
                    return true;
                case R.id.navigation_scan_qr_code:
                    if (fragment != scanFragment)
                        replaceFragment(scanFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(Objects.requireNonNull(MainActivity.this), new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSIONS);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = MyHttpRequest.downloadAudio(Constants.BASE_URL + "prescript/getaudio/audio1/");
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(file.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        String userType = getIntent().getStringExtra("userType");

        navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu navigationMenu = navigation.getMenu();
        MenuItem recordMenuItem = navigationMenu.findItem(R.id.navigation_record_audio);
        MenuItem connectMenuItem = navigationMenu.findItem(R.id.navigation_connect_device);
        if (userType.equalsIgnoreCase("doctor")) {
            recordMenuItem.setVisible(true);
            connectMenuItem.setVisible(true);
        } else {
            recordMenuItem.setVisible(false);
            connectMenuItem.setVisible(false);
        }

        fragmentManager = getSupportFragmentManager();
        fragment = recentScanFragment;
        replaceFragment(fragment);
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    public static void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.navigation_fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();
        MainActivity.fragment = fragment;
    }

    @Override
    public void onBackPressed() {
        if (fragment instanceof BluetoothConnectFragment || fragment instanceof ProfileFragment || fragment instanceof RecordFragment || fragment instanceof ScanFragment)
            navigation.setSelectedItemId(R.id.navigation_recent_scan);
        else
            super.onBackPressed();
    }
}
