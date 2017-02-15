package com.testapp.models;

public class HistoricalRate {
	private String dateString;
	private double rate;

	public HistoricalRate(String dateString, double rate) {
		this.dateString = dateString;
		this.rate = rate;
	}

	public String getDateString() {
		return dateString;
	}

	public double getRate() {
		return rate;
	}
}
