package com.neon.yandex.praktikum.calculator.delivery;

import net.jqwik.api.*;
import org.javamoney.moneta.Money;

import static com.neon.yandex.praktikum.calculator.delivery.DeliveryTestConst.BASIC_DISTANCE_COST;
import static com.neon.yandex.praktikum.calculator.delivery.DeliveryTestConst.BASIC_SIZE_COST;

class DeliveryCalculatorPropertyBasedTest {
    @Property
    boolean under2Km_ThereIsOnlyOnePrice(@ForAll("distanceGenerator") final double distance) {
        final var calculator = new DeliveryCalculator(Money.of(0, "RUB"));

        return calculator.calculateDeliveryCost(distance, false, false, DeliveryServiceLoad.AVERAGE)
                .equals(BASIC_DISTANCE_COST.add(BASIC_SIZE_COST));
    }

    @Provide
    Arbitrary<Double> distanceGenerator() {
        return Arbitraries.doubles().between(0, false, 2, true);
    }
}
