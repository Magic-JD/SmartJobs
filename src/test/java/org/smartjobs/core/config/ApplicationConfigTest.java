package org.smartjobs.core.config;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class ApplicationConfigTest {

    private final ApplicationConfig applicationConfig = new ApplicationConfig();

    @Test
    void testThatDecimalFormatProvidesTheCorrectFormat(){
        DecimalFormat decimalFormat = applicationConfig.decimalFormat();
        String format = decimalFormat.format(1_000_000L);
        assertEquals("1,000,000", format);
    }

}
