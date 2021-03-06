package com.testapp.views;

import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.testapp.models.Rate;

import java.util.List;

public interface MainView extends MvpView {
	void setupSwipeRefresh(OnRefreshListener onRefreshListener);

	void showMessage(@StringRes int resId);

	void setRefreshing(boolean refreshing);

	void presentRates(List<Rate> rates);
}
