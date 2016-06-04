package refinedstorage.tile;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.autocrafting.task.ICraftingTask;
import refinedstorage.container.ContainerCrafter;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.BasicItemValidator;
import refinedstorage.inventory.IItemValidator;
import refinedstorage.item.ItemPattern;
import refinedstorage.item.ItemUpgrade;

public class TileCrafter extends TileMachine {
    private BasicItemHandler patterns = new BasicItemHandler(PATTERN_SLOTS, this, new IItemValidator() {
        @Override
        public boolean valid(ItemStack stack) {
            return stack.getItem() == RefinedStorageItems.PATTERN && ItemPattern.isValid(stack);
        }
    });

    private BasicItemHandler upgrades = new BasicItemHandler(4, this, new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED));

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
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        RefinedStorageUtils.readItems(patterns, 0, nbt);
        RefinedStorageUtils.readItems(upgrades, 1, nbt);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RefinedStorageUtils.writeItems(patterns, 0, tag);
        RefinedStorageUtils.writeItems(upgrades, 1, tag);

        return tag;
    }

    public int getSpeed() {
        return 20 - (RefinedStorageUtils.getUpgradeCount(upgrades, ItemUpgrade.TYPE_SPEED) * 4);
    }

    public IItemHandler getPatterns() {
        return patterns;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDroppedItems() {
        return new CombinedInvWrapper(patterns, upgrades);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != getDirection()) {
            return (T) patterns;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != getDirection()) || super.hasCapability(capability, facing);
    }
}
