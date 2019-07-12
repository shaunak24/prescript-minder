package android.example.com.prescriptminder.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.example.com.prescriptminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment5 extends Fragment {

    private static ViewPagerFragment5 viewPagerFragment5;

    public ViewPagerFragment5() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager_fragment5, container, false);
    }

    public static ViewPagerFragment5 getViewPagerFragment5() {
        if (viewPagerFragment5 == null)
            viewPagerFragment5 = new ViewPagerFragment5();
        return viewPagerFragment5;
    }
}
