package com.testapp.models;

public class Rate {

	private String code;
	private String description;
	private String rateFloat;

	public enum ResponseKeys {
		code,
		description,
		rate
	}

	public Rate(String code, String description, String rateFloat) {
		this.code = code;
		this.description = description;
		this.rateFloat = rateFloat;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getRate() {
		return rateFloat;
	}
}
