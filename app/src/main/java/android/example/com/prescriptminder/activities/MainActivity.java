package android.example.com.prescriptminder.activities;

import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.fragments.BluetoothConnectFragment;
import android.example.com.prescriptminder.fragments.ProfileFragment;
import android.example.com.prescriptminder.fragments.RecentScanFragment;
import android.example.com.prescriptminder.fragments.RecordFragment;
import android.example.com.prescriptminder.fragments.ScanFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    public static BottomNavigationView navigation;
    public static Fragment fragment;
    private final BluetoothConnectFragment bluetoothConnectFragment = BluetoothConnectFragment.getBluetoothConnectFragment();
    private final ProfileFragment profileFragment = ProfileFragment.getProfileFragment();
    private final RecentScanFragment recentScanFragment = RecentScanFragment.getRecentScanFragment();
    private final RecordFragment recordFragment = RecordFragment.getRecordFragment();
    private final ScanFragment scanFragment = ScanFragment.getScanFragment();

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
                    if (fragment != profileFragment)
                        replaceFragment(profileFragment);
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
        navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
        fragment = recentScanFragment;
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment)
    {
        fragmentManager.beginTransaction()
                .replace(R.id.navigation_fragment_container,fragment)
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
