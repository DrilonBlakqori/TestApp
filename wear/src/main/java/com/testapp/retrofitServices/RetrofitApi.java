package com.testapp.retrofitServices;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {

	private static CoinDeskService coinDeskService;

	public static CoinDeskService getCoinDeskService() {
		if (coinDeskService == null) {

			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl("https://api.coindesk.com/")
					.addConverterFactory(GsonConverterFactory.create())
					.build();
			coinDeskService = retrofit.create(CoinDeskService.class);
		}
		return coinDeskService;
	}
}
