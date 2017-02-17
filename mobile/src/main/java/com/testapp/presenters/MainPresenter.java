package com.testapp.presenters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.testapp.R;
import com.testapp.models.Rate;
import com.testapp.models.Rate.ResponseKeys;
import com.testapp.retrofitServices.RetrofitApi;
import com.testapp.utils.SharedPrefsManager;
import com.testapp.utils.SharedPrefsManager.Keys;
import com.testapp.views.MainView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.testapp.utils.SharedPrefsManager.getSharedPrefs;

public class MainPresenter extends MvpBasePresenter<MainView> implements OnRefreshListener {

	public static final String TAG = "MainPresenter";
	private static final String ACTION_REFRESH = "com.testapp.action_refresh";
	private static final int REQ_CODE_REFRESH = 8872;

	private Context context;
	private BroadcastReceiver broadcastReceiver;

	public MainPresenter(Context context) {
		this.context = context;
	}

	public void init(Bundle savedInstanceState) {
		if (getView() != null) {
			getView().setupSwipeRefresh(this);
			showCachedData();
			if (savedInstanceState == null) {
				queryRates();
			}
			registerReceiver();
			startAlarm();
		}
	}

	public void deInit() {
		context.unregisterReceiver(broadcastReceiver);
		stopAlarm();
	}

	public void stopAlarm() {
		AlarmManager alarmManager = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
		Intent refreshIntent = new Intent();
		refreshIntent.setAction(ACTION_REFRESH);
		PendingIntent createPostPendingIntent = PendingIntent.getBroadcast(context, REQ_CODE_REFRESH,
				refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(createPostPendingIntent);
	}

	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter(ACTION_REFRESH);
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				queryRates();
			}
		};
		context.registerReceiver(broadcastReceiver, intentFilter);
	}

	public void startAlarm() {
		AlarmManager alarmManager = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
		Intent refreshIntent = new Intent();
		refreshIntent.setAction(ACTION_REFRESH);
		PendingIntent createPostPendingIntent = PendingIntent.getBroadcast(context, REQ_CODE_REFRESH,
				refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60 * 1000
				, 60 * 1000, createPostPendingIntent);
	}

	private void showCachedData() {
		String ratesBody = SharedPrefsManager.getSharedPrefs(context).getString(Keys.todayRatesBody.name(), null);
		if (ratesBody != null && getView() != null) {
			List<Rate> historicalRates = parseBitcoinRates(new Gson().fromJson(ratesBody, LinkedTreeMap.class));
			getView().presentRates(historicalRates);
		}
	}

	private void queryRates() {
		if (getView() == null) {
			return;
		}
		getView().setRefreshing(true);
		Call<LinkedTreeMap> call = RetrofitApi.getCoinDeskService().getTodayRates();
		call.enqueue(new Callback<LinkedTreeMap>() {
			@Override
			public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
				if (getView() == null) {
					return;
				}
				getView().setRefreshing(false);
				if (response.isSuccessful() && response.body().size() > 0) {
					storeInPrefs(response.body());
					List<Rate> rates = parseBitcoinRates(response.body());
					getView().presentRates(rates);
				} else if (!response.isSuccessful()){
					getView().showMessage(R.string.main_activity_request_error);
				}
			}

			@Override
			public void onFailure(Call<LinkedTreeMap> call, Throwable t) {
				if (getView() != null) {
					getView().showMessage(R.string.main_activity_request_error);
					Log.e(TAG, t.getMessage());
					getView().setRefreshing(false);
				}
			}
		});
	}

	private ArrayList<Rate> parseBitcoinRates(LinkedTreeMap body) {
		ArrayList<Rate> rates = new ArrayList<>();
		@SuppressWarnings("unchecked")
		LinkedTreeMap<String, LinkedTreeMap<String, Object>> data =
				(LinkedTreeMap<String, LinkedTreeMap<String, Object>>) body.get("bpi");

		for (LinkedTreeMap.Entry<String, LinkedTreeMap<String, Object>> entry : data.entrySet()) {
			LinkedTreeMap<String, Object> rateMap = entry.getValue();
			rates.add(new Rate(
					(String) rateMap.get(ResponseKeys.code.name()),
					(String) rateMap.get(ResponseKeys.description.name()),
					(String) rateMap.get(ResponseKeys.rate.name())));
		}
		return rates;
	}

	private void storeInPrefs(LinkedTreeMap body) {
		getSharedPrefs(context).edit().putString(Keys.todayRatesBody.name(),
				new Gson().toJson(body)).apply();
	}

	@Override
	public void onRefresh() {
		queryRates();
	}
}
