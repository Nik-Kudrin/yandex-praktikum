package com.neon.yandex.praktikum.calculator.delivery;

public enum DeliveryServiceLoad {
	VeryHigh(1.6),
	High(1.4),
	OverAverage(1.2),
	AVERAGE(1.0);
	
	private final double coefficient;
	
	public double getCoefficient() {
		return coefficient;
	}
	
	DeliveryServiceLoad(final double coefficent) {
		this.coefficient = coefficent;
	}
}
