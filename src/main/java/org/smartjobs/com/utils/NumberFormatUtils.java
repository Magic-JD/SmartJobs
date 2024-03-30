package org.smartjobs.com.utils;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Component
public class NumberFormatUtils {

    public String formatNumber(double number) {
        NumberFormat nf = new DecimalFormat("##.#");
        return nf.format(number);
    }
}