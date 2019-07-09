package android.example.com.prescriptminder.fragments;


import android.example.com.prescriptminder.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentScanFragment extends Fragment {

    private static RecentScanFragment recentScanFragment;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static RecentScanFragment getRecentScanFragment() {
        if (recentScanFragment == null)
            recentScanFragment = new RecentScanFragment();
        return recentScanFragment;
    }
}
