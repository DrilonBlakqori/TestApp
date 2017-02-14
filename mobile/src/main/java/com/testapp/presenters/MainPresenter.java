package com.testapp.presenters;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.testapp.R;
import com.testapp.models.BitcoinRate;
import com.testapp.retrofitServices.RetrofitApi;
import com.testapp.utils.SharedPrefsManager;
import com.testapp.utils.SharedPrefsManager.Keys;
import com.testapp.views.MainView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.testapp.utils.SharedPrefsManager.getSharedPrefs;

public class MainPresenter extends MvpBasePresenter<MainView> implements OnRefreshListener {

	private static final String TAG = "MainPresenter";
	private Context context;

	private SimpleDateFormat simpleDateFormat;

	public void init(Context context) {
		this.context = context;
		if (isViewAttached()) {
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			getView().setupSwipeRefresh(this);
			showCachedData();
			queryRates();
		}
	}

	@Override
	public void onRefresh() {
		queryRates();
	}

	private void showCachedData() {
		String ratesBody = SharedPrefsManager.getSharedPrefs(context).getString(Keys.ratesBody.name(), null);
		if (ratesBody != null) {
			List<BitcoinRate> bitcoinRates = parseBitcoinRates(new Gson().fromJson(ratesBody, LinkedTreeMap.class));
			getView().presentBitcoinRates(bitcoinRates);
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
		Call<LinkedTreeMap> call = RetrofitApi.getCoinDeskService().getHistoricalData(
				simpleDateFormat.format(start),
				simpleDateFormat.format(end));
		call.enqueue(new Callback<LinkedTreeMap>() {
			@Override
			public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
				if (isViewAttached()) {
					if (response.isSuccessful()) {
						storeInPrefs(response.body());
						List<BitcoinRate> bitcoinRates = parseBitcoinRates(response.body());
						getView().presentBitcoinRates(bitcoinRates);
					} else {
						getView().showMessage(R.string.main_activity_request_error);
						Log.e(TAG, response.message());
					}
					getView().setRefreshing(false);
				}
			}

			@Override
			public void onFailure(Call<LinkedTreeMap> call, Throwable t) {
				if (isViewAttached()) {
					getView().showMessage(R.string.main_activity_request_error);
					Log.e(TAG, t.getMessage());
					getView().setRefreshing(false);
				}
			}
		});
	}

	private void storeInPrefs(LinkedTreeMap body) {
		getSharedPrefs(context).edit().putString(Keys.ratesBody.name(),
				new Gson().toJson(body)).apply();
	}

	private ArrayList<BitcoinRate> parseBitcoinRates(LinkedTreeMap body) {
		ArrayList<BitcoinRate> bitcoinRates = new ArrayList<>();
		@SuppressWarnings("unchecked")
		LinkedTreeMap<String, Double> data = (LinkedTreeMap<String, Double>) body.get("bpi");
		for (LinkedTreeMap.Entry<String, Double> entry : data.entrySet()) {
			bitcoinRates.add(new BitcoinRate(entry.getKey(), entry.getValue()));
		}
		return bitcoinRates;
	}
}
