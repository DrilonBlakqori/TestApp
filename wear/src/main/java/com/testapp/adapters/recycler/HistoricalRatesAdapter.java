package com.testapp.adapters.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testapp.R;
import com.testapp.adapters.recycler.HistoricalRatesAdapter.HistoricalViewHolder;
import com.testapp.models.HistoricalRate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoricalRatesAdapter extends Adapter<HistoricalViewHolder> {

	private ArrayList<HistoricalRate> items;
	private Context context;

	public HistoricalRatesAdapter(Context context) {
		items = new ArrayList<>();
		this.context = context;
	}

	@Override
	public HistoricalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.item_historical_rate, parent, false);
		return new HistoricalViewHolder(view);
	}

	@Override
	public void onBindViewHolder(HistoricalViewHolder holder, int position) {
		HistoricalRate historicalRate = items.get(position);
		holder.dateText.setText(context.getString(R.string.historical_activity_date, historicalRate.getDateString()));
		holder.rateText.setText(context.getString(R.string.historical_activity_rate, historicalRate.getRate()));
		if (position == getItemCount() - 1) {
			holder.itemDivider.setVisibility(View.GONE);
		} else {
			holder.itemDivider.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void addAll(List<HistoricalRate> rates) {
		items.clear();
		items.addAll(rates);
		notifyDataSetChanged();
	}

	public class HistoricalViewHolder extends ViewHolder {

		@BindView(R.id.dateText)
		TextView dateText;
		@BindView(R.id.rateText)
		TextView rateText;
		@BindView(R.id.itemDivider)
		View itemDivider;

		HistoricalViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

}
