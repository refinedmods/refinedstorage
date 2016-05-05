package refinedstorage.util;

import refinedstorage.inventory.InventorySimple;
import refinedstorage.item.ItemUpgrade;

public class UpgradeUtils {
    public static int getSpeed(InventorySimple upgrades) {
        return getSpeed(upgrades, 9, 2);
    }

    public static int getSpeed(InventorySimple inventory, int speed, int speedIncrease) {
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getMetadata() == ItemUpgrade.TYPE_SPEED) {
                speed -= speedIncrease;
            }
        }

        return speed;
    }

    public static boolean hasUpgrade(InventorySimple inventory, int type) {
        return getUpgradeCount(inventory, type) > 0;
    }

    public static int getUpgradeCount(InventorySimple inventory, int type) {
        int upgrades = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getMetadata() == type) {
                upgrades++;
            }
        }

        return upgrades;
    }
}
