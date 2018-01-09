package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.raoulvdberge.refinedstorage.api.util.IQuantityFormatter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class QuantityFormatter implements IQuantityFormatter {
    private DecimalFormat formatterWithUnits = new DecimalFormat("####0.#", DecimalFormatSymbols.getInstance(Locale.US));
    private DecimalFormat formatter = new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.US));

    public QuantityFormatter() {
        formatterWithUnits.setRoundingMode(RoundingMode.DOWN);
    }

    @Override
    public String formatWithUnits(int qty) {
        if (qty >= 1_000_000) {
            float qtyShort = (float) qty / 1_000_000F;

            if (qty >= 100_000_000) {
                qtyShort = Math.round(qtyShort); // XXX.XM looks weird.
            }

            return formatterWithUnits.format(qtyShort) + "M";
        } else if (qty >= 1000) {
            float qtyShort = (float) qty / 1000F;

            if (qty >= 100_000) {
                qtyShort = Math.round(qtyShort); // XXX.XK looks weird.
            }

            return formatterWithUnits.format(qtyShort) + "K";
        }

        return String.valueOf(qty);
    }

    @Override
    public String format(int qty) {
        return formatter.format(qty);
    }
}
