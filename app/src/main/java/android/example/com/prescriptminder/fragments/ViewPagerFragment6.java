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
public class ViewPagerFragment6 extends Fragment {

    private static ViewPagerFragment6 viewPagerFragment6;

    public ViewPagerFragment6() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager_fragment6, container, false);
    }

    public static ViewPagerFragment6 getViewPagerFragment6() {
        if (viewPagerFragment6 == null)
            viewPagerFragment6 = new ViewPagerFragment6();
        return viewPagerFragment6;
    }
}
