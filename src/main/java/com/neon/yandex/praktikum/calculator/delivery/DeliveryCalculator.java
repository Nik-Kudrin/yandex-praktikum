package com.neon.yandex.praktikum.calculator.delivery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

/**
 * Calculator cost of delivery
 */
public class DeliveryCalculator {
	
	private static final Logger log = LogManager.getLogger(DeliveryCalculator.class.getName());
	
	public final MonetaryAmount minimumDeliveryCost;
	public final CurrencyUnit baseCurrency;
	
	public DeliveryCalculator(final MonetaryAmount minimumDeliveryCost) {
		if (minimumDeliveryCost == null) {
			throw new IllegalArgumentException("Minimum delivery cost cannot be null");
		}
		
		this.minimumDeliveryCost = minimumDeliveryCost;
		this.baseCurrency = minimumDeliveryCost.getCurrency();
	}
	
	private MonetaryAmount addMileageCost(final double distance) {
		if (distance <= 0.0) {
			throw new IllegalArgumentException("Distance cannot be 0 Km or negative");
		}
		
		// TODO: comparision problem in number with floating point
		if (distance <= 2) {
			return Money.of(50, baseCurrency);
		} else if (distance <= 10) {
			return Money.of(100, baseCurrency);
		} else if (distance <= 30) {
			return Money.of(200, baseCurrency);
		} else {
			return Money.of(300, baseCurrency);
		}
	}
	
	private MonetaryAmount addSizeCost(final boolean isCargoOversize) {
		return isCargoOversize ? Money.of(200, baseCurrency) : Money.of(100, baseCurrency);
	}
	
	private MonetaryAmount addFragilityCost(final double distance, final boolean isCargoFragile)
		throws IllegalArgumentException {
		
		if (isCargoFragile) {
			if (distance > 30) {
				throw new IllegalArgumentException("Transportation for fragile cargo is limited to 30 Km");
			}
			return Money.of(300, baseCurrency);
		} else {
			return Money.of(0, baseCurrency);
		}
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
		
		if (deliveryServiceLoad == null) {
			throw new IllegalArgumentException("Delivery service load must not be null");
		}
		
		var totalCost = Monetary.getDefaultAmountFactory().setCurrency(baseCurrency).setNumber(0).create();
		
		totalCost = totalCost.add(addMileageCost(distance));
		totalCost = totalCost.add(addSizeCost(isCargoOversize));
		totalCost = totalCost.add(addFragilityCost(distance, isCargoFragile));
		totalCost = totalCost.multiply(deliveryServiceLoad.getCoefficient());
		
		if (totalCost.isLessThan(minimumDeliveryCost)) {
			totalCost = minimumDeliveryCost;
		}
		
		log.info("Delivery cost calculation has been finished. Amount is " + totalCost);
		
		return totalCost;
	}
}
