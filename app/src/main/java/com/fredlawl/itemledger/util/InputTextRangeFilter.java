package com.fredlawl.itemledger.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class InputTextRangeFilter implements InputFilter {
    private final BigDecimal min;
    private final BigDecimal max;

    public InputTextRangeFilter(BigDecimal min, BigDecimal max) {
        this.min = min;
        this.max = max;
    }

    public InputTextRangeFilter(String min, String max) {
        this(new BigDecimal(min), new BigDecimal(max));
    }

    @Override
    public CharSequence filter(CharSequence source, int sourceStart, int sourceEnd, Spanned destination, int destStart, int destEnd) {
        try {
            String input = destination.subSequence(0, destStart)
                + source.subSequence(sourceStart, sourceEnd).toString()
                + destination.subSequence(destEnd, destination.length());

            if (input.equals("-")) {
                return null;
            }

            if (isInRange(new BigDecimal(input))) {
                return null;
            }
        } catch (NumberFormatException e) {
            // Ignore the number format exception, we don't care
        }

        return "";
    }

    public boolean isInRange(BigDecimal input) {
        return input.compareTo(min) >= 0 && input.compareTo(max) <= 0;
    }
}
