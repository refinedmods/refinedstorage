package com.raoulvdberge.refinedstorage.api.util;

/**
 * Utilities for formatting quantities.
 */
public interface IQuantityFormatter {
    /**
     * Formats a quantity as they are formatted in the Grid.
     * Formatted as following: "####0.#".
     * <p>
     * If the quantity is equal to or bigger than 1000 it will be displayed as the quantity divided by 1000 (without any decimals) and a "K" appended.
     * If the quantity is equal to or bigger than 1000000 it will be displayed as the quantity divided by 1000000 (without any decimals) and a "M" appended.
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    String formatWithUnits(int qty);

    /**
     * Formats a quantity as they are formatted on the disk tooltips.
     * Formatted as following: "#,###".
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    String format(int qty);

    /**
     * Divides quantity by 1000 and appends "B".
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    String formatInBucketForm(int qty);

    /**
     * Used in Fluid Grid.
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    String formatInBucketFormWithOnlyTrailingDigitsIfZero(int qty);
}
