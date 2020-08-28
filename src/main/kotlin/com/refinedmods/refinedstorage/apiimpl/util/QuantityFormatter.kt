package com.refinedmods.refinedstorage.apiimpl.util

import com.refinedmods.refinedstorage.api.util.IQuantityFormatter
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class QuantityFormatter : IQuantityFormatter {
    private val formatterWithUnits = DecimalFormat("####0.#", DecimalFormatSymbols.getInstance(Locale.US))
    private val formatter = DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.US))
    private val bucketFormatter = DecimalFormat("####0.###", DecimalFormatSymbols.getInstance(Locale.US))
    override fun formatWithUnits(qty: Int): String? {
        return formatWithUnits(qty.toLong())
    }

    override fun formatWithUnits(qty: Long): String? {
        if (qty >= 1000000000) {
            return formatterWithUnits.format(Math.round(qty.toFloat() / 1000000000).toLong()) + "B"
        } else if (qty >= 1000000) {
            var qtyShort = qty.toFloat() / 1000000f
            if (qty >= 100000000) {
                qtyShort = Math.round(qtyShort).toFloat() // XXX.XM looks weird.
            }
            return formatterWithUnits.format(qtyShort.toDouble()) + "M"
        } else if (qty >= 1000) {
            var qtyShort = qty.toFloat() / 1000f
            if (qty >= 100000) {
                qtyShort = Math.round(qtyShort).toFloat() // XXX.XK looks weird.
            }
            return formatterWithUnits.format(qtyShort.toDouble()) + "K"
        }
        return qty.toString()
    }

    override fun format(qty: Int): String? {
        return formatter.format(qty.toLong())
    }

    override fun format(qty: Long): String? {
        return formatter.format(qty)
    }

    override fun formatInBucketForm(qty: Int): String? {
        return "todo B" // TODO Fix
//        return bucketFormatter.format(qty.toFloat() / FluidAttributes.BUCKET_VOLUME as Float.toDouble()) + " B"
    }

    override fun formatInBucketFormWithOnlyTrailingDigitsIfZero(qty: Int): String? {
        return "todo" // TODO Fix
//        val amountRaw = qty.toFloat() / FluidAttributes.BUCKET_VOLUME as Float
//        val amount = amountRaw.toInt()
//        return if (amount >= 1) {
//            instance().getQuantityFormatter()!!.formatWithUnits(amount)
//        } else {
//            String.format("%.1f", amountRaw)
//        }
    }

    init {
        formatterWithUnits.roundingMode = RoundingMode.DOWN
    }
}