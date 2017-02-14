package com.testapp.retrofitServices;

import com.google.gson.internal.LinkedTreeMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CoinDeskService {

	@GET("v1/bpi/historical/close.json")
	Call<LinkedTreeMap> getHistoricalData(@Query("start") String start, @Query("end") String end);
}
