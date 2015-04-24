package com.p3authentication;

import java.util.ArrayList;

import android.content.Context;

public class Constants {
	Context context;

	public Constants(Context context) {
		this.context = context;

	}

	public String[] getImagePath(String Path) {
		ArrayList<String> IMAGES = new ArrayList<String>();
		String[] TEMP = context.getResources().getStringArray(R.array.pic_name);
		for (int index = 0; index < TEMP.length; index++) {
			IMAGES.add(Path + "/" + TEMP[index]);
		}
		String[] StringIMAGES = new String[IMAGES.size()];
		StringIMAGES = IMAGES.toArray(StringIMAGES);
		return StringIMAGES;
	}

}
