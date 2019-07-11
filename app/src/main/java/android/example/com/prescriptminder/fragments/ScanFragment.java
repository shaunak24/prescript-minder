package android.example.com.prescriptminder.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private static ScanFragment scanFragment;
    private ZXingScannerView scannerView;
    private String result_url;

    public ScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        MainActivity.navigation.setSelectedItemId(R.id.navigation_scan_qr_code);

        // Inflate the layout for this fragment
        scannerView = new ZXingScannerView(getContext());
        return scannerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        scannerView.setResultHandler(this);
    }

    @Override
    public void handleResult(Result result) {
        String resultText = result.getText();
        Log.e("ScanResult", resultText);
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.startCamera();
    }

    public static ScanFragment getScanFragment() {
        if (scanFragment == null)
            scanFragment = new ScanFragment();
        return scanFragment;
    }
}
