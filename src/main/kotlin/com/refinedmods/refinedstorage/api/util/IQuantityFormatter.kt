package com.refinedmods.refinedstorage.api.util

/**
 * Utilities for formatting quantities.
 */
interface IQuantityFormatter {
    /**
     * Formats a quantity as they are formatted in the Grid.
     * Formatted as following: "####0.#".
     *
     *
     * If the quantity is equal to or bigger than 1000 it will be displayed as the quantity divided by 1000 (without any decimals) and a "K" appended.
     * If the quantity is equal to or bigger than 1000000 it will be displayed as the quantity divided by 1000000 (without any decimals) and a "M" appended.
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    fun formatWithUnits(qty: Int): String?

    /**
     * Formats a quantity as they are formatted in the Grid.
     * Formatted as following: "####0.#".
     *
     *
     * If the quantity is equal to or bigger than 1000 it will be displayed as the quantity divided by 1000 (without any decimals) and a "K" appended.
     * If the quantity is equal to or bigger than 1000000 it will be displayed as the quantity divided by 1000000 (without any decimals) and a "M" appended.
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    fun formatWithUnits(qty: Long): String?

    /**
     * Formats a quantity as they are formatted on the disk tooltips.
     * Formatted as following: "#,###".
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    fun format(qty: Int): String?

    /**
     * Formats a quantity as they are formatted on the disk tooltips.
     * Formatted as following: "#,###".
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    fun format(qty: Long): String?

    /**
     * Divides quantity by 1000 and appends "B".
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    fun formatInBucketForm(qty: Int): String?

    /**
     * Used in Fluid Grid.
     *
     * @param qty the quantity
     * @return the formatted quantity
     */
    fun formatInBucketFormWithOnlyTrailingDigitsIfZero(qty: Int): String?
}