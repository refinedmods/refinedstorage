package refinedstorage.inventory;

import net.minecraft.tileentity.TileEntity;
import refinedstorage.RSItems;
import refinedstorage.item.ItemUpgrade;

public class ItemHandlerUpgrade extends ItemHandlerBasic {
    public ItemHandlerUpgrade(int size, TileEntity tile, int... supportedUpgrades) {
        super(size, tile, new IItemValidator[supportedUpgrades.length]);

        for (int i = 0; i < supportedUpgrades.length; ++i) {
            this.validators[i] = new ItemValidatorBasic(RSItems.UPGRADE, supportedUpgrades[i]);
        }
    }

    public int getSpeed() {
        return getSpeed(9, 2);
    }

    public int getSpeed(int speed, int speedIncrease) {
        for (int i = 0; i < getSlots(); ++i) {
            if (getStackInSlot(i) != null && getStackInSlot(i).getItemDamage() == ItemUpgrade.TYPE_SPEED) {
                speed -= speedIncrease;
            }
        }

        return speed;
    }

    public boolean hasUpgrade(int type) {
        return getUpgradeCount(type) > 0;
    }

    public int getUpgradeCount(int type) {
        int upgrades = 0;

        for (int i = 0; i < getSlots(); ++i) {
            if (getStackInSlot(i) != null && getStackInSlot(i).getItemDamage() == type) {
                upgrades++;
            }
        }

        return upgrades;
    }

    public int getEnergyUsage() {
        int usage = 0;

        for (int i = 0; i < getSlots(); ++i) {
            if (getStackInSlot(i) != null) {
                usage += ItemUpgrade.getEnergyUsage(getStackInSlot(i));
            }
        }

        return usage;
    }

    public int getFortuneLevel() {
        for (int i = 0; i < getSlots(); ++i) {
            if (getStackInSlot(i) != null && getStackInSlot(i).getItemDamage() == ItemUpgrade.TYPE_FORTUNE) {
                return ItemUpgrade.getFortuneLevel(getStackInSlot(i));
            }
        }

        return 0;
    }

    public int getInteractStackSize() {
        return hasUpgrade(ItemUpgrade.TYPE_STACK) ? 64 : 1;
    }
}
