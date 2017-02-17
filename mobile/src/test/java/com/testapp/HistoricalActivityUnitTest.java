package com.testapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.testapp.presenters.HistoricalRatesPresenter;
import com.testapp.retrofitServices.CoinDeskService;
import com.testapp.retrofitServices.RetrofitApi;
import com.testapp.utils.SharedPrefsManager;
import com.testapp.views.HistoricalRatesView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, RetrofitApi.class})
public class HistoricalActivityUnitTest {

	@Mock
	public HistoricalRatesView historicalRatesView;
	@Mock
	public Context context;
	@Mock
	public SharedPreferences sharedPreferences;
	@Mock
	public HistoricalRatesPresenter historicalRatesPresenter;
	@Mock
	Call<LinkedTreeMap> call;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(Log.class);
		PowerMockito.mockStatic(RetrofitApi.class);
		historicalRatesPresenter = new HistoricalRatesPresenter(context);
		historicalRatesPresenter = PowerMockito.spy(historicalRatesPresenter);
		when(SharedPrefsManager.getSharedPrefs(context)).thenReturn(sharedPreferences);
		CoinDeskService coinDeskService = new CoinDeskService() {
			@Override
			public Call<LinkedTreeMap> getHistoricalRates(@Query("currency") String currency, @Query("start") String start, @Query("end") String end) {
				return call;
			}

			@Override
			public Call<LinkedTreeMap> getLiveRates() {
				return null;
			}
		};
		when(RetrofitApi.getCoinDeskService()).thenReturn(coinDeskService);
		historicalRatesPresenter.attachView(historicalRatesView);
	}

	@Test
	public void checkIfHistoricalRatesAreQueried() throws Exception {
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				((Callback) (invocation).getArguments()[0]).onResponse(call, Response.<LinkedTreeMap>success(
						new LinkedTreeMap<>()));
				return null;
			}

		}).when(call).enqueue(any(Callback.class));
		historicalRatesPresenter.init("USD", null);
		verify(historicalRatesView).setupSwipeRefresh(any(OnRefreshListener.class));
		verify(historicalRatesView).setRefreshing(true);
		verify(historicalRatesView).setRefreshing(false);
	}

	@Test
	public void checkIfErrorOnUnsuccessfulRateQueryShown() throws Exception {
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				((Callback) (invocation).getArguments()[0]).onResponse(call, Response.error(400,
						ResponseBody.create(null, "")));
				return null;
			}

		}).when(call).enqueue(any(Callback.class));
		historicalRatesPresenter.init("USD", null);
		verify(historicalRatesView).setupSwipeRefresh(any(OnRefreshListener.class));
		verify(historicalRatesView).setRefreshing(true);
		verify(historicalRatesView).setRefreshing(false);
		verify(historicalRatesView).showMessage(R.string.historical_activity_request_error);
	}

	@Test
	public void checkIfErrorOnFailureRateQueryShown() throws Exception {
		final String failureMessage = "Request failed";
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				((Callback) (invocation).getArguments()[0]).onFailure(call, new Throwable(failureMessage));
				return null;
			}

		}).when(call).enqueue(any(Callback.class));
		historicalRatesPresenter.init("USD", null);
		verify(historicalRatesView).setupSwipeRefresh(any(OnRefreshListener.class));
		verify(historicalRatesView).setRefreshing(true);
		verify(historicalRatesView).setRefreshing(false);
		verify(historicalRatesView).showMessage(R.string.historical_activity_request_error);
		PowerMockito.verifyStatic();
		Log.e(HistoricalRatesPresenter.TAG, failureMessage);
	}
}
