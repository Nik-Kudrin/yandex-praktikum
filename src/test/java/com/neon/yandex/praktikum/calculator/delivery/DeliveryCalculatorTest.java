package com.neon.yandex.praktikum.calculator.delivery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.FastMoney;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import javax.money.MonetaryAmount;

import static org.assertj.core.api.Assertions.assertThat;

public class DeliveryCalculatorTest {
	
	private static final Logger log = LogManager.getLogger(DeliveryCalculator.class.getName());
	private static final String currencyCode = "RUB";
	private static final DeliveryCalculator calculator = new DeliveryCalculator(currencyCode);
	
	// TODO: think about comparision problem in number with floating point
	// TODO: pairwise testing -> generate cases
	
	@Test
	void testFunction() {
		
		calculator.calculateDeliveryCost(0, true, false, DeliveryServiceLoad.AVERAGE);
	}
	
	private static Stream<Arguments> coreLogicTestDataProvider() {
		return Stream.of(
			Arguments.of(0, true, true, DeliveryServiceLoad.AVERAGE, FastMoney.of(0, currencyCode))//,
//			Arguments.of("", true),
//			Arguments.of("  ", true),
//			Arguments.of("not blank", false)
		);
	}
	
	@ParameterizedTest
	@MethodSource("coreLogicTestDataProvider")
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
	
	private static Stream<Arguments> edgeCasesDataDataProvider() {
		return Stream.of(
			Arguments.of(0, false, false, DeliveryServiceLoad.AVERAGE, new IllegalArgumentException())
		);
	}
	
	@ParameterizedTest
	@MethodSource("edgeCasesDataDataProvider")
	void deliveryCalculatorEdgeCasesTest(
		final double distance,
		final boolean isCargoOversize,
		final boolean isCargoFragile,
		final DeliveryServiceLoad deliveryServiceLoad,
		final Exception expectedException
	) {
		Assertions.assertThrows(
			expectedException.getClass(),
			() -> calculator.calculateDeliveryCost(
				distance,
				isCargoOversize,
				isCargoFragile,
				deliveryServiceLoad));
	}
}
