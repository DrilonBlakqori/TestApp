package com.testapp.adapters.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testapp.R;
import com.testapp.adapters.recycler.BitcoinRateAdapter.BitcoinViewHolder;
import com.testapp.models.BitcoinRate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BitcoinRateAdapter extends Adapter<BitcoinViewHolder> {

	private ArrayList<BitcoinRate> items;
	private Context context;

	public BitcoinRateAdapter(Context context) {
		items = new ArrayList<>();
		this.context = context;
	}

	@Override
	public BitcoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.item_bitcoin_rate, parent, false);
		return new BitcoinViewHolder(view);
	}

	@Override
	public void onBindViewHolder(BitcoinViewHolder holder, int position) {
		BitcoinRate bitcoinRate = items.get(position);
		holder.dateText.setText(context.getString(R.string.main_activity_date, bitcoinRate.getDateString()));
		holder.rateText.setText(context.getString(R.string.main_activity_rate, bitcoinRate.getRate()));
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void addAll(List<BitcoinRate> rates) {
		items.clear();
		items.addAll(rates);
		notifyDataSetChanged();
	}

	class BitcoinViewHolder extends ViewHolder {

		@BindView(R.id.dateText)
		TextView dateText;
		@BindView(R.id.rateText)
		TextView rateText;

		BitcoinViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

}
