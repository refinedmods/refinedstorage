package com.refinedmods.refinedstorage.inventory.item;

import com.refinedmods.refinedstorage.inventory.item.validator.UpgradeItemValidator;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import net.minecraft.world.item.ItemStack;

public class UpgradeItemHandler extends BaseItemHandler {
    public UpgradeItemHandler(int size, UpgradeItem.Type... supportedUpgrades) {
        super(size);

        for (UpgradeItem.Type supportedUpgrade : supportedUpgrades) {
            addValidator(new UpgradeItemValidator(supportedUpgrade));
        }
    }

    public int getSpeed() {
        return getSpeed(9, 2);
    }

    public int getSpeed(int speed, int speedIncrease) {
        for (int i = 0; i < getSlots(); ++i) {
            ItemStack slot = getStackInSlot(i);

            if (slot.getItem() instanceof UpgradeItem && ((UpgradeItem) slot.getItem()).getType() == UpgradeItem.Type.SPEED) {
                speed -= speedIncrease;
            }
        }

        return speed;
    }

    public boolean hasUpgrade(UpgradeItem.Type type) {
        for (int i = 0; i < getSlots(); ++i) {
            ItemStack slot = getStackInSlot(i);

            if (slot.getItem() instanceof UpgradeItem && ((UpgradeItem) slot.getItem()).getType() == type) {
                return true;
            }
        }

        return false;
    }

    public int getUpgradeCount(UpgradeItem.Type type) {
        int upgrades = 0;

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack slot = getStackInSlot(i);

            if (slot.getItem() instanceof UpgradeItem && ((UpgradeItem) slot.getItem()).getType() == type) {
                upgrades++;
            }
        }

        return upgrades;
    }

    public int getEnergyUsage() {
        int usage = 0;

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack slot = getStackInSlot(i);

            if (slot.getItem() instanceof UpgradeItem) {
                usage += ((UpgradeItem) slot.getItem()).getType().getEnergyUsage();
            }
        }

        return usage;
    }

    public int getStackInteractCount() {
        return hasUpgrade(UpgradeItem.Type.STACK) ? 64 : 1;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
