package com.refinedmods.refinedstorage.util

import net.minecraft.client.resources.I18n

object TimeUtils {
    private const val SECOND: Long = 1000
    private const val MINUTE = SECOND * 60
    private const val HOUR = MINUTE * 60
    private const val DAY = HOUR * 24
    private const val WEEK = DAY * 7
    private const val YEAR = DAY * 365
    fun getAgo(ago: Long, by: String?): String {
        val diff = System.currentTimeMillis() - ago
        if (diff < SECOND * 10) {
            return I18n.format("misc.refinedstorage.last_modified.just_now", by)
        } else if (diff < MINUTE) {
            return I18n.format("misc.refinedstorage.last_modified.second" + if (diff / SECOND > 1) "s" else "", diff / SECOND, by)
        } else if (diff < HOUR) {
            return I18n.format("misc.refinedstorage.last_modified.minute" + if (diff / MINUTE > 1) "s" else "", diff / MINUTE, by)
        } else if (diff < DAY) {
            return I18n.format("misc.refinedstorage.last_modified.hour" + if (diff / HOUR > 1) "s" else "", diff / HOUR, by)
        } else if (diff < WEEK) {
            return I18n.format("misc.refinedstorage.last_modified.day" + if (diff / DAY > 1) "s" else "", diff / DAY, by)
        } else if (diff < YEAR) {
            return I18n.format("misc.refinedstorage.last_modified.week" + if (diff / WEEK > 1) "s" else "", diff / WEEK, by)
        }
        return I18n.format("misc.refinedstorage.last_modified.year" + if (diff / YEAR > 1) "s" else "", diff / YEAR, by)
    }
}