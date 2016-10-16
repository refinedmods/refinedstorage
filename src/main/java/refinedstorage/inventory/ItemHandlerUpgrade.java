package refinedstorage.inventory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
                usage += ItemUpgrade.getEnergyUsage(getStackInSlot(i).getItemDamage());
            }
        }

        return usage;
    }

    public int getInteractStackSize() {
        return hasUpgrade(ItemUpgrade.TYPE_STACK) ? 64 : 1;
    }

    public int getForuneLevel() {
        for (int i = 0; i < getSlots(); ++i) {
            if (getStackInSlot(i) != null && getStackInSlot(i).getItemDamage() == ItemUpgrade.TYPE_FORTUNE) {
                NBTTagCompound tag = getStackInSlot(i).getTagCompound();
                if (tag.hasKey(ItemUpgrade.NBT_FORTUNE)) {
                    int level = tag.getInteger(ItemUpgrade.NBT_FORTUNE);
                    System.out.println(level);
                    return level;
                }
            }
        }

        return 0;
    }
}
