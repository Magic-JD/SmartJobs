package org.smartjobs.adaptors.view.web.utils;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class NumberFormatUtilsTest {

    public static final String EXPECTED_NUMBER = "43.6";
    private final NumberFormatUtils numberFormatUtils = new NumberFormatUtils();

    @Test
    void testFormatNumberShouldReturnTheResultToJustOneDecimalPlace() {
        assertEquals(EXPECTED_NUMBER, numberFormatUtils.formatNumber(43.63232342));
    }

    @Test
    void testFormatNumberShouldRoundUpTheResult() {
        assertEquals(EXPECTED_NUMBER, numberFormatUtils.formatNumber(43.555));
    }
}