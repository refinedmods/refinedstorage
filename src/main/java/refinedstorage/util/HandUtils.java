package refinedstorage.util;

import net.minecraft.util.EnumHand;

public class HandUtils {
    public static EnumHand getHandById(int id) {
        switch (id) {
            case 0:
                return EnumHand.MAIN_HAND;
            case 1:
                return EnumHand.OFF_HAND;
        }

        return EnumHand.MAIN_HAND;
    }

    public static int getIdFromHand(EnumHand hand) {
        switch (hand) {
            case MAIN_HAND:
                return 0;
            case OFF_HAND:
                return 1;
        }

        return 0;
    }
}
