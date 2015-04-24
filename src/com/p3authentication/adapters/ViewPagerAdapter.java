package com.p3authentication.adapters;

import com.p3authentication.tutorial.TutorialFragmentA;
import com.p3authentication.tutorial.TutorialFragmentB;
import com.p3authentication.tutorial.TutorialFragmentC;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int i) {
		// TODO Auto-generated method stub
		Fragment fragment = null;
		if (i == 0) {
			fragment = new TutorialFragmentA();
		}
		if (i == 1) {
			fragment = new TutorialFragmentB();
		}
		if (i == 2) {
			fragment = new TutorialFragmentC();
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

}
