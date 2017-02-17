package com.testapp.retrofitServices;

import com.google.gson.internal.LinkedTreeMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@SuppressWarnings("WeakerAccess")
public interface CoinDeskService {

	@GET("v1/bpi/historical/close.json")
	Call<LinkedTreeMap> getHistoricalRates(@Query("currency") String currency, @Query("start") String start, @Query("end") String end);

	@GET("v1/bpi/currentprice.json")
	Call<LinkedTreeMap> getLiveRates();
}
