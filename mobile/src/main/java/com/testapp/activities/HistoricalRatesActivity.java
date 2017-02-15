package com.testapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.testapp.R;
import com.testapp.adapters.recycler.HistoricalRatesAdapter;
import com.testapp.models.HistoricalRate;
import com.testapp.presenters.HistoricalRatesPresenter;
import com.testapp.views.HistoricalRatesView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoricalRatesActivity extends MvpActivity<HistoricalRatesView, HistoricalRatesPresenter> implements HistoricalRatesView {

	private static final String EXTRA_CURRENCY = "extra_currency";

	@BindView(R.id.recyclerView)
	RecyclerView recyclerView;
	@BindView(R.id.swipeRefresh)
	SwipeRefreshLayout swipeRefresh;

	private HistoricalRatesAdapter historicalRatesAdapter;

	public static void startActivity(Context context, String currency) {
		Intent intent = new Intent(context, HistoricalRatesActivity.class);
		intent.putExtra(EXTRA_CURRENCY, currency);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_historical_data);
		ButterKnife.bind(this);
		setupRecycler();
		presenter.init(this, getIntent().getStringExtra(EXTRA_CURRENCY), savedInstanceState);
	}

	private void setupRecycler() {
		historicalRatesAdapter = new HistoricalRatesAdapter(this);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
		}
		recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		recyclerView.setAdapter(historicalRatesAdapter);
	}

	@Override
	public void setupSwipeRefresh(OnRefreshListener onRefreshListener) {
		swipeRefresh.setOnRefreshListener(onRefreshListener);
		swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
	}

	@Override
	public void setRefreshing(boolean refreshing) {
		swipeRefresh.setRefreshing(refreshing);
	}

	@Override
	public void presentBitcoinRates(List<HistoricalRate> historicalRates) {
		historicalRatesAdapter.addAll(historicalRates);
	}

	@Override
	public void showMessage(@StringRes int messageId) {
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}

	@NonNull
	@Override
	public HistoricalRatesPresenter createPresenter() {
		return new HistoricalRatesPresenter();
	}
}
