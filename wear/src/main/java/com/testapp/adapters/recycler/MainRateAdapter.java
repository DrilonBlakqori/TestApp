package com.testapp.adapters.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testapp.R;
import com.testapp.activities.HistoricalRatesActivity;
import com.testapp.adapters.recycler.MainRateAdapter.RateViewHolder;
import com.testapp.models.Rate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainRateAdapter extends Adapter<RateViewHolder> {

	private ArrayList<Rate> rates;
	private Context context;

	public MainRateAdapter(Context context) {
		this.context = context;
		rates = new ArrayList<>();
	}

	@Override
	public RateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_rate,
				parent, false);
		final RateViewHolder rateViewHolder = new RateViewHolder(view);
		rateViewHolder.itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (rateViewHolder.getAdapterPosition() != -1) {
					HistoricalRatesActivity.startActivity(context,
							rates.get(rateViewHolder.getAdapterPosition()).getCode());
				}
			}
		});
		return rateViewHolder;
	}

	public void addAll(List<Rate> rates) {
		this.rates.clear();
		this.rates.addAll(rates);
		notifyDataSetChanged();
	}

	@Override
	public void onBindViewHolder(RateViewHolder holder, int position) {
		Rate rate = rates.get(position);
		holder.currencyText.setText(rate.getDescription());
		holder.rateText.setText(rate.getRate());
		if (position == getItemCount() - 1) {
			holder.itemDivider.setVisibility(View.GONE);
		} else {
			holder.itemDivider.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public int getItemCount() {
		return rates.size();
	}

	public class RateViewHolder extends ViewHolder {

		@BindView(R.id.currencyText)
		TextView currencyText;
		@BindView(R.id.rateText)
		TextView rateText;
		@BindView(R.id.itemDivider)
		View itemDivider;

		RateViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
