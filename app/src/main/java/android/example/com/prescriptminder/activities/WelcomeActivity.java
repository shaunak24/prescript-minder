package android.example.com.prescriptminder.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.com.prescriptminder.R;
import android.example.com.prescriptminder.fragments.ViewPagerFragment1;
import android.example.com.prescriptminder.fragments.ViewPagerFragment2;
import android.example.com.prescriptminder.fragments.ViewPagerFragment3;
import android.example.com.prescriptminder.fragments.ViewPagerFragment4;
import android.example.com.prescriptminder.fragments.ViewPagerFragment5;
import android.example.com.prescriptminder.fragments.ViewPagerFragment6;
import android.example.com.prescriptminder.helperclasses.WelcomePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final String userType = getIntent().getStringExtra("userType");

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isFirstTime", false);
        editor.apply();

        final ViewPager viewPager = findViewById(R.id.viewPager);
        WelcomePagerAdapter welcomePagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager());
        welcomePagerAdapter.addFragment(ViewPagerFragment1.getViewPagerFragment1());
        welcomePagerAdapter.addFragment(ViewPagerFragment2.getViewPagerFragment2());
        welcomePagerAdapter.addFragment(ViewPagerFragment3.getViewPagerFragment3());
        welcomePagerAdapter.addFragment(ViewPagerFragment4.getViewPagerFragment4());
        welcomePagerAdapter.addFragment(ViewPagerFragment5.getViewPagerFragment5());
        welcomePagerAdapter.addFragment(ViewPagerFragment6.getViewPagerFragment6());

        final int[] dotsArray = {R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4, R.id.dot5, R.id.dot6};

        final Button skipButton = findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == dotsArray.length - 1)
                {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("userType", userType);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int currentItem) {
                for (int i = 0; i < dotsArray.length; i++)
                {
                    if (i == currentItem)
                        ((ImageView)findViewById(dotsArray[i])).setImageResource(R.drawable.active_dot);
                    else
                        ((ImageView)findViewById(dotsArray[i])).setImageResource(R.drawable.inactive_dot);
                }
                if (currentItem == dotsArray.length - 1)
                    skipButton.setText("done");
                else
                    skipButton.setText("skip");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setAdapter(welcomePagerAdapter);
    }
}
