package com.refinedmods.refinedstorage.util;

import net.minecraft.client.resources.I18n;

public final class TimeUtils {
    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long YEAR = DAY * 365;

    private TimeUtils() {
    }

    public static String getAgo(long ago, String by) {
        long diff = System.currentTimeMillis() - ago;

        if (diff < SECOND * 10) {
            return I18n.get("misc.refinedstorage.last_modified.just_now", by);
        } else if (diff < MINUTE) {
            return I18n.get("misc.refinedstorage.last_modified.second" + ((diff / SECOND) > 1 ? "s" : ""), diff / SECOND, by);
        } else if (diff < HOUR) {
            return I18n.get("misc.refinedstorage.last_modified.minute" + ((diff / MINUTE) > 1 ? "s" : ""), diff / MINUTE, by);
        } else if (diff < DAY) {
            return I18n.get("misc.refinedstorage.last_modified.hour" + ((diff / HOUR) > 1 ? "s" : ""), diff / HOUR, by);
        } else if (diff < WEEK) {
            return I18n.get("misc.refinedstorage.last_modified.day" + ((diff / DAY) > 1 ? "s" : ""), diff / DAY, by);
        } else if (diff < YEAR) {
            return I18n.get("misc.refinedstorage.last_modified.week" + ((diff / WEEK) > 1 ? "s" : ""), diff / WEEK, by);
        }

        return I18n.get("misc.refinedstorage.last_modified.year" + ((diff / YEAR) > 1 ? "s" : ""), diff / YEAR, by);
    }
}
