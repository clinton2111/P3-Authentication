package com.p3authentication.tutorial;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.p3authentication.R;
import com.p3authentication.adapters.ViewPagerAdapter;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class TutorialActivity extends FragmentActivity {
	ViewPager tutviewpager = null;
	PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.tutorial_main);
		tutviewpager = (ViewPager) findViewById(R.id.tutPager);
		FragmentManager fm = getSupportFragmentManager();
		tutviewpager.setAdapter(new ViewPagerAdapter(fm));
		mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(tutviewpager);
	}

}
