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
public class ViewPagerFragment3 extends Fragment {

    private static ViewPagerFragment3 viewPagerFragment3;

    public ViewPagerFragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager_fragment3, container, false);
    }

    public static ViewPagerFragment3 getViewPagerFragment3() {
        if (viewPagerFragment3 == null)
            viewPagerFragment3 = new ViewPagerFragment3();
        return viewPagerFragment3;
    }
}
