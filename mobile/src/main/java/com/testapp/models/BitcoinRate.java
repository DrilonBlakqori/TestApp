package com.testapp.models;

public class BitcoinRate {
	private String dateString;
	private double rate;

	public BitcoinRate(String dateString, double rate) {
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
