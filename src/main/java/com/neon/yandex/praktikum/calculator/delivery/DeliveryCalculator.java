package com.neon.yandex.praktikum.calculator.delivery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.FastMoney;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

/**
 * Calculator cost of delivery
 */
public class DeliveryCalculator {
	
	private static final Logger log = LogManager.getLogger(DeliveryCalculator.class.getName());
	public final CurrencyUnit baseCurrency;
	
	public DeliveryCalculator(final String currency) {
		this.baseCurrency = Monetary.getCurrency(currency);
	}
	
	public MonetaryAmount calculateDeliveryCost(
		final double distance,
		final boolean isCargoOversize,
		final boolean isCargoFragile,
		final DeliveryServiceLoad deliveryServiceLoad
	) {
		log.info(String.format(
			"Delivery cost calculation has been started with parameters: " +
				"distance=%s, isCargoOversize=%s, isCargoFragile=%s, deliveryServiceLoad=%s",
			distance,
			isCargoOversize,
			isCargoFragile,
			deliveryServiceLoad));
		
		var totalCost = Monetary.getDefaultAmountFactory().setCurrency(baseCurrency).setNumber(0).create();
		
		// - более 30 км: +300 рублей к доставке;
		//- до 30 км: +200 рублей к доставке;
		//- до 10 км: +100 рублей к доставке;
		//- до 2 км: +50 рублей к доставке;
		if (distance < 2) {
			totalCost = totalCost.add(FastMoney.of(50, baseCurrency));
		} else if (distance < 10) {
			totalCost = totalCost.add(FastMoney.of(100, baseCurrency));
		} else if (distance < 30) {
			totalCost = totalCost.add(FastMoney.of(200, baseCurrency));
		} else {
			totalCost = totalCost.add(FastMoney.of(300, baseCurrency));
		}
		
		// - большие габариты: +200 рублей к доставке;
		//- маленькие габариты: +100 рублей к доставке;
		totalCost = isCargoOversize ?
			totalCost.add(FastMoney.of(200, baseCurrency)) :
			totalCost.add(FastMoney.of(100, baseCurrency));
		
		// Если груз хрупкий — +300 рублей к доставке. Хрупкие грузы нельзя возить на расстояние более 30 км;
		if (isCargoFragile) {
			if (distance > 30) {
				throw new IllegalArgumentException("Transportation for fragile cargo is limited to 30 km");
			}
			totalCost = totalCost.add(FastMoney.of(300, baseCurrency));
		}
		
		totalCost = totalCost.multiply(deliveryServiceLoad.getCoefficient());
		
		return totalCost;
	}
}
