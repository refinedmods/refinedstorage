package com.refinedmods.refinedstorage.util;

import net.minecraft.util.text.TranslationTextComponent;

public final class TimeUtils {
    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long YEAR = DAY * 365;

    public static String getAgo(long ago, String by) {
        long diff = System.currentTimeMillis() - ago;

        if (diff < SECOND * 10) {
            return new TranslationTextComponent("misc.refinedstorage.last_modified.just_now", by).getString();
        } else if (diff < MINUTE) {
            return new TranslationTextComponent("misc.refinedstorage.last_modified.second" + ((diff / SECOND) > 1 ? "s" : ""), diff / SECOND, by).getString();
        } else if (diff < HOUR) {
            return new TranslationTextComponent("misc.refinedstorage.last_modified.minute" + ((diff / MINUTE) > 1 ? "s" : ""), diff / MINUTE, by).getString();
        } else if (diff < DAY) {
            return new TranslationTextComponent("misc.refinedstorage.last_modified.hour" + ((diff / HOUR) > 1 ? "s" : ""), diff / HOUR, by).getString();
        } else if (diff < WEEK) {
            return new TranslationTextComponent("misc.refinedstorage.last_modified.day" + ((diff / DAY) > 1 ? "s" : ""), diff / DAY, by).getString();
        } else if (diff < YEAR) {
            return new TranslationTextComponent("misc.refinedstorage.last_modified.week" + ((diff / WEEK) > 1 ? "s" : ""), diff / WEEK, by).getString();
        }

        return new TranslationTextComponent("misc.refinedstorage.last_modified.year" + ((diff / YEAR) > 1 ? "s" : ""), diff / YEAR, by).getString();
    }
}
