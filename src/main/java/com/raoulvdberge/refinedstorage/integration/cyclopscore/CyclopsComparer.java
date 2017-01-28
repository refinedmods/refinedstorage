package com.raoulvdberge.refinedstorage.integration.cyclopscore;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;

public final class CyclopsComparer {
    public static int comparerFlagsToItemMatch(int flags) {
        int itemMatch = 0;

        if ((flags & IComparer.COMPARE_DAMAGE) == IComparer.COMPARE_DAMAGE) {
            itemMatch |= ItemMatch.DAMAGE;
        }

        if ((flags & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT) {
            itemMatch |= ItemMatch.NBT;
        }

        if ((flags & IComparer.COMPARE_QUANTITY) == IComparer.COMPARE_QUANTITY) {
            itemMatch |= ItemMatch.STACKSIZE;
        }

        return itemMatch;
    }

    public static int itemMatchToComparerFlags(int itemMatch) {
        int flags = 0;

        if ((itemMatch & ItemMatch.DAMAGE) == ItemMatch.DAMAGE) {
            flags |= IComparer.COMPARE_DAMAGE;
        }

        if ((itemMatch & ItemMatch.NBT) == ItemMatch.NBT) {
            flags |= IComparer.COMPARE_NBT;
        }

        if ((itemMatch & ItemMatch.STACKSIZE) == ItemMatch.STACKSIZE) {
            flags |= IComparer.COMPARE_QUANTITY;
        }

        return flags;
    }
}
