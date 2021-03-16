package com.neon.yandex.praktikum.calculator.delivery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import javax.money.MonetaryAmount;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryCalculatorTest {
	
	private static final Logger log = LogManager.getLogger(DeliveryCalculator.class.getName());
	private static final String CURRENCY_CODE = "RUB";
	private static final DeliveryCalculator calculator = new DeliveryCalculator(Money.of(0, CURRENCY_CODE));
	
	private static final MonetaryAmount BASIC_SIZE_COST = Money.of(100, CURRENCY_CODE);
	private static final MonetaryAmount OVERSIZE_COST = Money.of(200, CURRENCY_CODE);
	private static final MonetaryAmount BASIC_DISTANCE_COST = Money.of(50, CURRENCY_CODE);
	private static final MonetaryAmount FRAGILITY_COST = Money.of(300, CURRENCY_CODE);
	
	private static Stream<Arguments> coreLogicTestDataProvider() {
		return Stream.of(
			// tests for distance parameter
			Arguments.of(0.1, false, false, DeliveryServiceLoad.AVERAGE,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST)),
			Arguments.of(2, false, false, DeliveryServiceLoad.AVERAGE,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST)),
			Arguments.of(2.0001, false, false, DeliveryServiceLoad.AVERAGE,
				Money.of(100, CURRENCY_CODE).add(BASIC_SIZE_COST)),
			Arguments.of(5, false, false, DeliveryServiceLoad.AVERAGE,
				Money.of(100, CURRENCY_CODE).add(BASIC_SIZE_COST)),
			Arguments.of(10, false, false, DeliveryServiceLoad.AVERAGE,
				Money.of(100, CURRENCY_CODE).add(BASIC_SIZE_COST)),
			Arguments.of(10.0001, false, false, DeliveryServiceLoad.AVERAGE,
				Money.of(200, CURRENCY_CODE).add(BASIC_SIZE_COST)),
			Arguments.of(15.6, false, false, DeliveryServiceLoad.AVERAGE,
				Money.of(200, CURRENCY_CODE).add(BASIC_SIZE_COST)),
			Arguments.of(30, false, false, DeliveryServiceLoad.AVERAGE,
				Money.of(200, CURRENCY_CODE).add(BASIC_SIZE_COST)),
			Arguments.of(30.0001, false, false, DeliveryServiceLoad.AVERAGE,
				Money.of(300, CURRENCY_CODE).add(BASIC_SIZE_COST)),
			Arguments.of(100_000, false, false, DeliveryServiceLoad.AVERAGE,
				Money.of(300, CURRENCY_CODE).add(BASIC_SIZE_COST)),
			Arguments.of(Double.MAX_VALUE, false, false, DeliveryServiceLoad.AVERAGE,
				Money.of(300, CURRENCY_CODE).add(BASIC_SIZE_COST)),
			
			// tests for fragile parameter
			Arguments.of(1, false, false, DeliveryServiceLoad.AVERAGE,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST)),
			Arguments.of(1, false, true, DeliveryServiceLoad.AVERAGE,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST).add(Money.of(300, CURRENCY_CODE))),
			
			// tests for oversize parameter
			Arguments.of(1, true, false, DeliveryServiceLoad.AVERAGE,
				BASIC_DISTANCE_COST.add(OVERSIZE_COST)),
			Arguments.of(1, false, false, DeliveryServiceLoad.AVERAGE,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST)),
			
			// test for delivery service load parameter
			Arguments.of(1, false, false, DeliveryServiceLoad.AVERAGE,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST)),
			Arguments.of(1, false, false, DeliveryServiceLoad.OverAverage,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST)
					.multiply(DeliveryServiceLoad.OverAverage.getCoefficient())),
			Arguments.of(1, false, false, DeliveryServiceLoad.High,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST).multiply(DeliveryServiceLoad.High.getCoefficient())),
			Arguments.of(1, false, false, DeliveryServiceLoad.VeryHigh,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST).multiply(DeliveryServiceLoad.VeryHigh.getCoefficient()))
		);
	}
	
	// Pairwise test provider -> https://pairwise.teremokgames.com/15k7w/
	private static Stream<Arguments> pairwiseDataProvider() {
		return Stream.of(
			Arguments.of(2, true, true, DeliveryServiceLoad.AVERAGE,
				BASIC_DISTANCE_COST.add(OVERSIZE_COST).add(FRAGILITY_COST)),
			
			Arguments.of(2, false, false, DeliveryServiceLoad.OverAverage,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST)
					.multiply(DeliveryServiceLoad.OverAverage.getCoefficient())),
			
			Arguments.of(2, true, true, DeliveryServiceLoad.High,
				BASIC_DISTANCE_COST
					.add(OVERSIZE_COST)
					.add(FRAGILITY_COST)
					.multiply(DeliveryServiceLoad.High.getCoefficient())),
			
			Arguments.of(2, false, false, DeliveryServiceLoad.VeryHigh,
				BASIC_DISTANCE_COST.add(BASIC_SIZE_COST)
					.multiply(DeliveryServiceLoad.VeryHigh.getCoefficient())),
			
			Arguments.of(10, false, true, DeliveryServiceLoad.VeryHigh,
				Money.of(100, CURRENCY_CODE)
					.add(BASIC_SIZE_COST)
					.add(FRAGILITY_COST)
					.multiply(DeliveryServiceLoad.VeryHigh.getCoefficient())),
			
			Arguments.of(10, true, false, DeliveryServiceLoad.AVERAGE,
				Money.of(100, CURRENCY_CODE).add(OVERSIZE_COST)),
			
			Arguments.of(10, false, true, DeliveryServiceLoad.OverAverage,
				Money.of(100, CURRENCY_CODE)
					.add(BASIC_SIZE_COST)
					.add(FRAGILITY_COST)
					.multiply(DeliveryServiceLoad.OverAverage.getCoefficient())),
			
			Arguments.of(10, true, false, DeliveryServiceLoad.High,
				Money.of(100, CURRENCY_CODE)
					.add(OVERSIZE_COST)
					.multiply(DeliveryServiceLoad.High.getCoefficient())),
			
			Arguments.of(30, true, true, DeliveryServiceLoad.High,
				Money.of(200, CURRENCY_CODE)
					.add(OVERSIZE_COST)
					.add(FRAGILITY_COST)
					.multiply(DeliveryServiceLoad.High.getCoefficient())),
			
			Arguments.of(30, false, false, DeliveryServiceLoad.VeryHigh,
				Money.of(200, CURRENCY_CODE)
					.add(BASIC_SIZE_COST)
					.multiply(DeliveryServiceLoad.VeryHigh.getCoefficient())),
			
			Arguments.of(30, true, true, DeliveryServiceLoad.AVERAGE,
				Money.of(200, CURRENCY_CODE)
					.add(OVERSIZE_COST)
					.add(FRAGILITY_COST)),
			
			Arguments.of(30, false, false, DeliveryServiceLoad.OverAverage,
				Money.of(200, CURRENCY_CODE)
					.add(BASIC_SIZE_COST)
					.multiply(DeliveryServiceLoad.OverAverage.getCoefficient())),
			
			Arguments.of(100, true, false, DeliveryServiceLoad.High,
				Money.of(300, CURRENCY_CODE)
					.add(OVERSIZE_COST)
					.multiply(DeliveryServiceLoad.High.getCoefficient())),
			
			Arguments.of(100, true, false, DeliveryServiceLoad.AVERAGE,
				Money.of(300, CURRENCY_CODE)
					.add(OVERSIZE_COST))
		);
	}
	
	@ParameterizedTest
	@MethodSource({ "coreLogicTestDataProvider", "pairwiseDataProvider" })
	void deliveryCalculatorBasicLogicTest(
		final double distance,
		final boolean isCargoOversize,
		final boolean isCargoFragile,
		final DeliveryServiceLoad deliveryServiceLoad,
		final MonetaryAmount expectedCost
	) {
		final var actualCost = calculator.calculateDeliveryCost(
			distance,
			isCargoOversize,
			isCargoFragile,
			deliveryServiceLoad);
		
		assertThat(actualCost).isEqualTo(expectedCost);
	}
	
	private static Stream<Arguments> minimumDeliveryCostDataProvider() {
		return Stream.of(
			Arguments.of(1, false, false, DeliveryServiceLoad.AVERAGE),
			Arguments.of(1, true, false, DeliveryServiceLoad.AVERAGE),
			Arguments.of(1, false, false, DeliveryServiceLoad.VeryHigh)
		);
	}
	
	@ParameterizedTest
	@MethodSource("minimumDeliveryCostDataProvider")
	void minimumDeliveryCostTest(
		final double distance,
		final boolean isCargoOversize,
		final boolean isCargoFragile,
		final DeliveryServiceLoad deliveryServiceLoad
	) {
		final var calculator = new DeliveryCalculator(Money.of(400, CURRENCY_CODE));
		
		final var actualCost = calculator.calculateDeliveryCost(
			distance,
			isCargoOversize,
			isCargoFragile,
			deliveryServiceLoad);
		
		assertThat(actualCost).isEqualTo(calculator.minimumDeliveryCost);
	}
	
	@Test
	void minimumDeliveryCostIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new DeliveryCalculator(null));
	}
	
	@Test
	void minimumDeliveryCostForFragileCargoTest() {
		final var calculator = new DeliveryCalculator(Money.of(500, CURRENCY_CODE));
		
		final var actualCost = calculator.calculateDeliveryCost(
			1,
			false,
			true,
			DeliveryServiceLoad.AVERAGE);
		
		assertThat(actualCost).isEqualTo(calculator.minimumDeliveryCost);
	}
	
	private static Stream<Arguments> edgeCasesDataDataProvider() {
		return Stream.of(
			// Delivery Service Load is null
			Arguments.of(1, false, false, null, IllegalArgumentException.class),
			
			// incorrect distance
			Arguments.of(0, false, false, DeliveryServiceLoad.AVERAGE, IllegalArgumentException.class),
			Arguments.of(-1, false, false, DeliveryServiceLoad.AVERAGE, IllegalArgumentException.class),
			
			// fragile cargo cannot be transported for more than 30 Km
			Arguments.of(30.001, false, true, DeliveryServiceLoad.AVERAGE, IllegalArgumentException.class)
		);
	}
	
	@ParameterizedTest
	@MethodSource("edgeCasesDataDataProvider")
	void deliveryCalculatorEdgeCasesTest(
		final double distance,
		final boolean isCargoOversize,
		final boolean isCargoFragile,
		final DeliveryServiceLoad deliveryServiceLoad,
		final Class<? extends Exception> expectedExceptionType
	) {
		Assertions.assertThrows(
			expectedExceptionType,
			() -> calculator.calculateDeliveryCost(
				distance,
				isCargoOversize,
				isCargoFragile,
				deliveryServiceLoad));
	}
}
