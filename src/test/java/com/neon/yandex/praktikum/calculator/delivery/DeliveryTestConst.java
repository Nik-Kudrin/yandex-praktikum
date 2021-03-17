package com.neon.yandex.praktikum.calculator.delivery;

import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;

public final class DeliveryTestConst {
    private DeliveryTestConst() {
    }

    static final String CURRENCY_CODE = "RUB";

    static final MonetaryAmount BASIC_SIZE_COST = Money.of(100, CURRENCY_CODE);
    static final MonetaryAmount OVERSIZE_COST = Money.of(200, CURRENCY_CODE);
    static final MonetaryAmount BASIC_DISTANCE_COST = Money.of(50, CURRENCY_CODE);
    static final MonetaryAmount FRAGILITY_COST = Money.of(300, CURRENCY_CODE);
}
