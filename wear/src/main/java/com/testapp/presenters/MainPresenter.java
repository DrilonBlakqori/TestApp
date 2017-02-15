package com.testapp.presenters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
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

	private Context context;

	public void init(Context context, Bundle savedInstanceState) {
		this.context = context;
		if (isViewAttached()) {
			getView().setupSwipeRefresh(this);
			showCachedData();
			if (savedInstanceState == null) {
				queryRates();
			}
		}
	}

	private void showCachedData() {
		String ratesBody = SharedPrefsManager.getSharedPrefs(context).getString(Keys.todayRatesBody.name(), null);
		if (ratesBody != null) {
			List<Rate> historicalRates = parseBitcoinRates(new Gson().fromJson(ratesBody, LinkedTreeMap.class));
			getView().presentRates(historicalRates);
		}
	}

	private void queryRates() {
		if (!isViewAttached()) {
			return;
		}
		getView().setRefreshing(true);
		Call<LinkedTreeMap> call = RetrofitApi.getCoinDeskService().getTodayRates();
		call.enqueue(new Callback<LinkedTreeMap>() {
			@Override
			public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
				if (!isViewAttached()) {
					return;
				}
				getView().setRefreshing(false);
				storeInPrefs(response.body());
				List<Rate> rates = parseBitcoinRates(response.body());
				getView().presentRates(rates);
			}

			@Override
			public void onFailure(Call<LinkedTreeMap> call, Throwable t) {

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
