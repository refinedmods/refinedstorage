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
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.container.ContainerCrafter;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.BasicItemValidator;
import refinedstorage.inventory.IItemValidator;
import refinedstorage.item.ItemPattern;
import refinedstorage.item.ItemUpgrade;

public class TileCrafter extends TileNode implements ICraftingPatternContainer, IConnectionHandler {
    private BasicItemHandler patterns = new BasicItemHandler(9, this, new IItemValidator() {
        @Override
        public boolean valid(ItemStack stack) {
            return stack.getItem() == RefinedStorageItems.PATTERN && ItemPattern.isValid(stack);
        }
    }) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (network != null) {
                network.rebuildPatterns();
            }
        }
    };

    private BasicItemHandler upgrades = new BasicItemHandler(4, this, new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED));

    @Override
    public int getEnergyUsage() {
        int usage = RefinedStorage.INSTANCE.crafterRfUsage + RefinedStorageUtils.getUpgradeEnergyUsage(upgrades);

        for (int i = 0; i < patterns.getSlots(); ++i) {
            if (patterns.getStackInSlot(i) != null) {
                usage += RefinedStorage.INSTANCE.crafterPerPatternRfUsage;
            }
        }

        return usage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerCrafter.class;
    }

    @Override
    public void disconnect(World world) {
        for (ICraftingTask task : network.getCraftingTasks()) {
            if (task.getPattern().getContainer(world) == this) {
                network.cancelCraftingTask(task);
            }
        }

        super.disconnect(world);
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

    @Override
    public int getSpeed() {
        return 20 - (RefinedStorageUtils.getUpgradeCount(upgrades, ItemUpgrade.TYPE_SPEED) * 4);
    }

    @Override
    public IItemHandler getConnectedItems() {
        return RefinedStorageUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());
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

    @Override
    public void onConnected(INetworkMaster network) {
        network.rebuildPatterns();
    }

    @Override
    public void onDisconnected(INetworkMaster network) {
        network.rebuildPatterns();
    }
}
