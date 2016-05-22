package refinedstorage.tile.autocrafting;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerCrafter;
import refinedstorage.inventory.IItemValidator;
import refinedstorage.inventory.SimpleItemHandler;
import refinedstorage.inventory.SimpleItemValidator;
import refinedstorage.item.ItemPattern;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileMachine;
import refinedstorage.tile.autocrafting.task.ICraftingTask;

public class TileCrafter extends TileMachine {
    private SimpleItemHandler patterns = new SimpleItemHandler(PATTERN_SLOTS, this, new IItemValidator() {
        @Override
        public boolean valid(ItemStack stack) {
            return stack.getItem() == RefinedStorageItems.PATTERN && ItemPattern.isValid(stack);
        }
    });

    private SimpleItemHandler upgrades = new SimpleItemHandler(4, this, new SimpleItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED));

    public static final int PATTERN_SLOTS = 6;

    @Override
    public int getEnergyUsage() {
        return 2 + RefinedStorageUtils.getUpgradeEnergyUsage(upgrades);
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerCrafter.class;
    }

    @Override
    public void onDisconnected(World world) {
        for (ICraftingTask task : controller.getCraftingTasks()) {
            if (task.getPattern().getCrafter(worldObj) == this) {
                controller.cancelCraftingTask(task);
            }
        }

        super.onDisconnected(world);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        RefinedStorageUtils.restoreItems(patterns, 0, nbt);
        RefinedStorageUtils.restoreItems(upgrades, 1, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        RefinedStorageUtils.saveItems(patterns, 0, nbt);
        RefinedStorageUtils.saveItems(upgrades, 1, nbt);
    }

    public int getSpeed() {
        return 20 - (RefinedStorageUtils.getUpgradeCount(upgrades, ItemUpgrade.TYPE_SPEED, PATTERN_SLOTS) * 4);
    }

    public IItemHandler getPatterns() {
        return patterns;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDroppedItems() {
        SimpleItemHandler dummy = new SimpleItemHandler(PATTERN_SLOTS + 4);

        for (int i = 0; i < PATTERN_SLOTS; ++i) {
            dummy.setStackInSlot(i, patterns.getStackInSlot(i));
        }

        for (int i = 0; i < 4; ++i) {
            dummy.setStackInSlot(PATTERN_SLOTS + i, upgrades.getStackInSlot(i));
        }

        return dummy;
    }
}
