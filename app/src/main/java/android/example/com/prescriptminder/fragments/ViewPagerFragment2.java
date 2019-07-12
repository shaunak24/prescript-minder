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
public class ViewPagerFragment2 extends Fragment {

    private static ViewPagerFragment2 viewPagerFragment2;

    public ViewPagerFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager_fragment2, container, false);
    }

    public static ViewPagerFragment2 getViewPagerFragment2() {
        if (viewPagerFragment2 == null)
            viewPagerFragment2 = new ViewPagerFragment2();
        return  viewPagerFragment2;
    }
}
