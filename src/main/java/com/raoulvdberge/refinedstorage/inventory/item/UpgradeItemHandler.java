package com.raoulvdberge.refinedstorage.inventory.item;

import com.raoulvdberge.refinedstorage.inventory.item.validator.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.inventory.item.validator.ItemValidatorUpgrade;
import com.raoulvdberge.refinedstorage.item.UpgradeItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class UpgradeItemHandler extends BaseItemHandler {
    public UpgradeItemHandler(int size, @Nullable Consumer<Integer> listener, UpgradeItem.Type... supportedUpgrades) {
        super(size, listener, new ItemValidatorBasic[supportedUpgrades.length]);

        for (int i = 0; i < supportedUpgrades.length; ++i) {
            this.validators[i] = new ItemValidatorUpgrade(supportedUpgrades[i]);
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

    public int getFortuneLevel() {
        int maxFortune = 0;

        for (int i = 0; i < getSlots(); ++i) {
            ItemStack slot = getStackInSlot(i);

            if (slot.getItem() instanceof UpgradeItem) {
                int fortune = ((UpgradeItem) slot.getItem()).getType().getFortuneLevel();

                if (fortune > maxFortune) {
                    maxFortune = fortune;
                }
            }
        }

        return maxFortune;
    }

    public int getItemInteractCount() {
        return hasUpgrade(UpgradeItem.Type.STACK) ? 64 : 1;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
