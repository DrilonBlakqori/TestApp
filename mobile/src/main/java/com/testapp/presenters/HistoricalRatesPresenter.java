package com.testapp.presenters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.testapp.R;
import com.testapp.models.HistoricalRate;
import com.testapp.retrofitServices.RetrofitApi;
import com.testapp.utils.SharedPrefsManager;
import com.testapp.utils.SharedPrefsManager.Keys;
import com.testapp.views.HistoricalRatesView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.testapp.utils.SharedPrefsManager.getSharedPrefs;

public class HistoricalRatesPresenter extends MvpBasePresenter<HistoricalRatesView> implements OnRefreshListener {

	private static final String TAG = "HistoricalPresenter";
	private String currency;
	private Context context;

	private SimpleDateFormat simpleDateFormat;

	public void init(Context context, String currency, Bundle savedInstanceState) {
		this.context = context;
		this.currency = currency;
		if (isViewAttached()) {
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			getView().setupSwipeRefresh(this);
			showCachedData();
			if (savedInstanceState == null) {
				queryRates();
			}
		}
	}

	@Override
	public void onRefresh() {
		queryRates();
	}

	private void showCachedData() {
		String ratesBody = SharedPrefsManager.getSharedPrefs(context).getString(Keys.historicalRatesBody.name() + currency, null);
		if (ratesBody != null) {
			List<HistoricalRate> historicalRates = parseHistoricalRates(new Gson().fromJson(ratesBody, LinkedTreeMap.class));
			getView().presentBitcoinRates(historicalRates);
		}
	}

	private void queryRates() {
		if (!isViewAttached()) {
			return;
		}
		getView().setRefreshing(true);
		Date end = new Date(); // now
		Date start = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 14); // 2 weeks ago
		executeRatesRequest(start, end);
	}

	private void executeRatesRequest(Date start, Date end) {
		Call<LinkedTreeMap> call = RetrofitApi.getCoinDeskService().getHistoricalRates(
				currency,
				simpleDateFormat.format(start),
				simpleDateFormat.format(end));
		call.enqueue(new Callback<LinkedTreeMap>() {
			@Override
			public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
				if (isViewAttached()) {
					if (response.isSuccessful()) {
						storeInPrefs(response.body());
						List<HistoricalRate> historicalRates = parseHistoricalRates(response.body());
						getView().presentBitcoinRates(historicalRates);
					} else {
						getView().showMessage(R.string.historical_activity_request_error);
						Log.e(TAG, response.message());
					}
					getView().setRefreshing(false);
				}
			}

			@Override
			public void onFailure(Call<LinkedTreeMap> call, Throwable t) {
				if (isViewAttached()) {
					getView().showMessage(R.string.historical_activity_request_error);
					Log.e(TAG, t.getMessage());
					getView().setRefreshing(false);
				}
			}
		});
	}

	private void storeInPrefs(LinkedTreeMap body) {
		getSharedPrefs(context).edit().putString(Keys.historicalRatesBody.name() + currency,
				new Gson().toJson(body)).apply();
	}

	private ArrayList<HistoricalRate> parseHistoricalRates(LinkedTreeMap body) {
		ArrayList<HistoricalRate> historicalRates = new ArrayList<>();
		@SuppressWarnings("unchecked")
		LinkedTreeMap<String, Double> data = (LinkedTreeMap<String, Double>) body.get("bpi");
		for (LinkedTreeMap.Entry<String, Double> entry : data.entrySet()) {
			historicalRates.add(new HistoricalRate(entry.getKey(), entry.getValue()));
		}
		sortRates(historicalRates);
		return historicalRates;
	}

	private void sortRates(ArrayList<HistoricalRate> historicalRates) {
		Collections.sort(historicalRates, new Comparator<HistoricalRate>() {
			@Override
			public int compare(HistoricalRate o1, HistoricalRate o2) {
				return o2.getDateString().compareTo(o1.getDateString());
			}
		});
	}
}
