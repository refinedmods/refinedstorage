package refinedstorage.tile;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.container.ContainerCrafter;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.IItemValidator;
import refinedstorage.inventory.UpgradeItemHandler;
import refinedstorage.item.ItemPattern;
import refinedstorage.item.ItemUpgrade;

public class TileCrafter extends TileNode implements ICraftingPatternContainer {
    private BasicItemHandler patterns = new BasicItemHandler(9, this, new IItemValidator() {
        @Override
        public boolean isValid(ItemStack stack) {
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

    private UpgradeItemHandler upgrades = new UpgradeItemHandler(4, this, ItemUpgrade.TYPE_SPEED);

    @Override
    public int getEnergyUsage() {
        int usage = RefinedStorage.INSTANCE.crafterUsage + upgrades.getEnergyUsage();

        for (int i = 0; i < patterns.getSlots(); ++i) {
            if (patterns.getStackInSlot(i) != null) {
                usage += RefinedStorage.INSTANCE.crafterPerPatternUsage;
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
    public void onConnectionChange(INetworkMaster network, boolean state) {
        if (!state) {
            for (ICraftingTask task : network.getCraftingTasks()) {
                if (task.getPattern().getContainerPosition().equals(pos)) {
                    network.cancelCraftingTask(task);
                }
            }
        }

        network.rebuildPatterns();
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        TileBase.readItems(patterns, 0, nbt);
        TileBase.readItems(upgrades, 1, nbt);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        TileBase.writeItems(patterns, 0, tag);
        TileBase.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public int getSpeed() {
        return 20 - (upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED) * 4);
    }

    @Override
    public IItemHandler getConnectedItems() {
        return TileBase.getItemHandler(getFacingTile(), getDirection().getOpposite());
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
