package com.testapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.testapp.R;
import com.testapp.adapters.recycler.BitcoinRateAdapter;
import com.testapp.models.BitcoinRate;
import com.testapp.presenters.MainPresenter;
import com.testapp.views.MainView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

	@BindView(R.id.mainRecycler)
	RecyclerView recyclerView;
	@BindView(R.id.mainSwipeRefresh)
	SwipeRefreshLayout swipeRefresh;

	private BitcoinRateAdapter bitcoinRateAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		setupRecycler();
		presenter.init(this);
	}

	private void setupRecycler() {
		bitcoinRateAdapter = new BitcoinRateAdapter(this);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(bitcoinRateAdapter);
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
	public void presentBitcoinRates(List<BitcoinRate> bitcoinRates) {
		bitcoinRateAdapter.addAll(bitcoinRates);
	}

	@Override
	public void showMessage(@StringRes int messageId) {
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}

	@NonNull
	@Override
	public MainPresenter createPresenter() {
		return new MainPresenter();
	}
}
