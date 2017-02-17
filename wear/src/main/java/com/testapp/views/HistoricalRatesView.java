package com.testapp.views;

import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.testapp.models.HistoricalRate;

import java.util.List;

public interface HistoricalRatesView extends MvpView {

	void setupSwipeRefresh(OnRefreshListener onRefreshListener);

	void setToolbarTitle(String title);

	void setRefreshing(boolean refreshing);

	void presentBitcoinRates(List<HistoricalRate> historicalRates);

	void showMessage(@StringRes int messageId);
}
