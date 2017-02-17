package com.testapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.testapp.R;
import com.testapp.adapters.recycler.MainRateAdapter;
import com.testapp.models.Rate;
import com.testapp.presenters.MainPresenter;
import com.testapp.views.MainView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

	@BindView(R.id.recyclerView)
	RecyclerView recyclerView;
	@BindView(R.id.swipeRefresh)
	SwipeRefreshLayout swipeRefresh;

	private MainRateAdapter rateAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		setupRecycler();
		presenter.init(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		presenter.deInit();
		super.onDestroy();
	}

	private void setupRecycler() {
		rateAdapter = new MainRateAdapter(this);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(rateAdapter);
		recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
	}

	@Override
	public void setupSwipeRefresh(OnRefreshListener onRefreshListener) {
		swipeRefresh.setOnRefreshListener(onRefreshListener);
		swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
	}

	@Override
	public void showMessage(@StringRes int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setRefreshing(boolean refreshing) {
		swipeRefresh.setRefreshing(refreshing);
	}

	@Override
	public void presentRates(List<Rate> rates) {
		rateAdapter.addAll(rates);
	}

	@NonNull
	@Override
	public MainPresenter createPresenter() {
		return new MainPresenter(this);
	}
}
