package com.testapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {

	private static final String PREFS_NAME = "com.testapp.prefs";

	public enum Keys {
		todayRatesBody,
		historicalRatesBody
	}

	public static SharedPreferences getSharedPrefs(Context context) {
		return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
}
