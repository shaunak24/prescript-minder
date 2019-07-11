package android.example.com.prescriptminder.fragments;


import android.content.DialogInterface;
import android.example.com.prescriptminder.activities.MainActivity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
    private static String result_url;

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
        result_url = result.getText();
        Log.e("ScanResult", result_url);
        showResultDialogue(result_url);
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

    public static String getUrl() {
        return result_url;
    }

    public void showResultDialogue(final String url) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle("Scanned URL")
                .setMessage(url)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.replaceFragment(new RecentScanFragment());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
