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
public class ViewPagerFragment4 extends Fragment {

    private static ViewPagerFragment4 viewPagerFragment4;

    public ViewPagerFragment4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager_fragment4, container, false);
    }

    public static ViewPagerFragment4 getViewPagerFragment4() {
        if (viewPagerFragment4 == null)
            viewPagerFragment4 = new ViewPagerFragment4();
        return viewPagerFragment4;
    }
}
