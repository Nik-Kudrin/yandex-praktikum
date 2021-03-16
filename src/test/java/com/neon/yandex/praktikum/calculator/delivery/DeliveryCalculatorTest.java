package com.neon.yandex.praktikum.calculator.delivery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class DeliveryCalculatorTest {

    private static final Logger log = LogManager.getLogger(DeliveryCalculator.class.getName());

    @Test
    void testFunction() {
        log.info("debug");
        assertThat(1).isEqualTo(2).describedAs("ddd");
    }
}
