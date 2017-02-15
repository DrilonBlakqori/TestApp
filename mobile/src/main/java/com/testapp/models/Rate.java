package com.testapp.models;

public class Rate {

	private String code;
	private String description;
	private String rate;

	public enum ResponseKeys {
		code,
		description,
		rate
	}

	public Rate(String code, String description, String rate) {
		this.code = code;
		this.description = description;
		this.rate = rate;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getRate() {
		return rate;
	}
}
