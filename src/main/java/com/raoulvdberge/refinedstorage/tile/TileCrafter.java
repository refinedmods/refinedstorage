package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileCrafter extends TileNode implements ICraftingPatternContainer {
    public static final TileDataParameter<Boolean> TRIGGERED_AUTOCRAFTING = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileCrafter>() {
        @Override
        public Boolean getValue(TileCrafter tile) {
            return tile.triggeredAutocrafting;
        }
    }, new ITileDataConsumer<Boolean, TileCrafter>() {
        @Override
        public void setValue(TileCrafter tile, Boolean value) {
            tile.triggeredAutocrafting = value;

            tile.markDirty();
        }
    });

    private static final String NBT_TRIGGERED_AUTOCRAFTING = "TriggeredAutocrafting";

    private ItemHandlerBasic patterns = new ItemHandlerBasic(9, this, s -> {
        // We can only validate the crafting pattern if the world exists.
        // If the world doesn't exist, this is probably called while reading and in that case it doesn't matter.
        if (getWorld() != null) {
            return s.getItem() instanceof ICraftingPatternProvider && ((ICraftingPatternProvider) s.getItem()).create(getWorld(), s, this).isValid();
        }

        return true;
    }) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (getWorld() != null && !getWorld().isRemote) {
                rebuildPatterns();
            }

            if (network != null) {
                network.rebuildPatterns();
            }
        }
    };

    private List<ICraftingPattern> actualPatterns = new ArrayList<>();

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

    private boolean triggeredAutocrafting = false;

    public TileCrafter() {
        dataManager.addWatchedParameter(TRIGGERED_AUTOCRAFTING);
    }

    private void rebuildPatterns() {
        actualPatterns.clear();

        for (int i = 0; i < patterns.getSlots(); ++i) {
            ItemStack patternStack = patterns.getStackInSlot(i);

            if (!patternStack.isEmpty()) {
                ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(getWorld(), patternStack, this);

                if (pattern.isValid()) {
                    actualPatterns.add(pattern);
                }
            }
        }
    }

    @Override
    public int getEnergyUsage() {
        int usage = RS.INSTANCE.config.crafterUsage + upgrades.getEnergyUsage();

        for (int i = 0; i < patterns.getSlots(); ++i) {
            if (!patterns.getStackInSlot(i).isEmpty()) {
                usage += RS.INSTANCE.config.crafterPerPatternUsage;
            }
        }

        return usage;
    }

    @Override
    public void update() {
        if (!getWorld().isRemote && ticks == 0) {
            rebuildPatterns();
        }

        super.update();
    }

    @Override
    public void updateNode() {
        if (triggeredAutocrafting && getWorld().isBlockPowered(pos)) {
            for (ICraftingPattern pattern : actualPatterns) {
                for (ItemStack output : pattern.getOutputs()) {
                    network.scheduleCraftingTask(output, 1, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
                }
            }
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        if (!state) {
            network.getCraftingTasks().stream()
                .filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
                .forEach(network::cancelCraftingTask);
        }

        network.rebuildPatterns();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(patterns, 0, tag);
        RSUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItems(patterns, 0, tag);
        RSUtils.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setBoolean(NBT_TRIGGERED_AUTOCRAFTING, triggeredAutocrafting);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_TRIGGERED_AUTOCRAFTING)) {
            triggeredAutocrafting = tag.getBoolean(NBT_TRIGGERED_AUTOCRAFTING);
        }
    }

    @Override
    public int getSpeedUpdateCount() {
        return upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);
    }

    @Override
    public IItemHandler getFacingInventory() {
        return RSUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return actualPatterns;
    }

    public IItemHandler getPatternItems() {
        return patterns;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(patterns, upgrades);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != getDirection()) {
            return (T) patterns;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != getDirection()) || super.hasCapability(capability, facing);
    }
}
