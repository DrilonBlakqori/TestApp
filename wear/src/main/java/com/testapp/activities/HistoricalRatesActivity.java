package com.testapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
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
	@BindView(R.id.toolbarTitle)
	TextView toolbarTitle;

	private HistoricalRatesAdapter historicalRatesAdapter;

	public static void startActivity(Context context, String currency) {
		Intent intent = new Intent(context, HistoricalRatesActivity.class);
		intent.putExtra(EXTRA_CURRENCY, currency);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_historical_rates);
		ButterKnife.bind(this);
		setupRecycler();
		presenter.init(getIntent().getExtras().getString(EXTRA_CURRENCY), savedInstanceState);
	}

	private void setupRecycler() {
		historicalRatesAdapter = new HistoricalRatesAdapter(this);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(historicalRatesAdapter);
	}

	@Override
	public void setupSwipeRefresh(OnRefreshListener onRefreshListener) {
		swipeRefresh.setOnRefreshListener(onRefreshListener);
		swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
	}

	@Override
	public void setToolbarTitle(String title) {
		toolbarTitle.setText(title);
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
		return new HistoricalRatesPresenter(this);
	}
}
