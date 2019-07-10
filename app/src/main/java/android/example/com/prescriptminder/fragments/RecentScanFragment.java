package android.example.com.prescriptminder.fragments;


import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.utils.Medicines;
import android.example.com.prescriptminder.utils.MedicinesAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentScanFragment extends Fragment {

    private static RecentScanFragment recentScanFragment;
    private String JSON;
    private RecyclerView recyclerView;
    private MedicinesAdapter adapter;
    private ArrayList<Medicines> medicinesArrayList;

    public RecentScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        MainActivity.navigation.setSelectedItemId(R.id.navigation_recent_scan);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        medicinesArrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        medicinesArrayList.add(new Medicines("Shaunak", "No note", "101010"));
        medicinesArrayList.add(new Medicines("Sahil", "No note", "111010"));
        medicinesArrayList.add(new Medicines("Divyesh", "No note", "111110"));
        medicinesArrayList.add(new Medicines("Mitesh", "No note", "111111"));
        medicinesArrayList.add(new Medicines("Aashay", "No note", "101000"));
        medicinesArrayList.add(new Medicines("Upasana", "No note", "100000"));

        adapter = new MedicinesAdapter(getContext(), medicinesArrayList);
        recyclerView.setAdapter(adapter);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getMedicineInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void getMedicineInfo() throws IOException {
        String url = "https://earthquake.usgs.gov/fdsnws/event/1/application.json";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        Response response = client.newCall(request).execute();
        JSON = response.body().string();

        if(!response.isSuccessful())
            throw new IOException("Response code : " + response);
        Log.e("Response", JSON);
    }

    public static RecentScanFragment getRecentScanFragment() {
        if (recentScanFragment == null)
            recentScanFragment = new RecentScanFragment();
        return recentScanFragment;
    }
}
