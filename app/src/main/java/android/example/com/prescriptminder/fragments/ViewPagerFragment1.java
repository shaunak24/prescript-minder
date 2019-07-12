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
public class ViewPagerFragment1 extends Fragment {

    private static ViewPagerFragment1 viewPagerFragment1;

    public ViewPagerFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager_fragment1, container, false);
    }

    public static ViewPagerFragment1 getViewPagerFragment1() {
        if (viewPagerFragment1 == null)
            viewPagerFragment1 = new ViewPagerFragment1();
        return viewPagerFragment1;
    }
}
