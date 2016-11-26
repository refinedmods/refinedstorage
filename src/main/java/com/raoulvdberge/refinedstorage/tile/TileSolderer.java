package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
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

public class TileSolderer extends TileNode {
    public static final TileDataParameter<Integer> DURATION = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileSolderer>() {
        @Override
        public Integer getValue(TileSolderer tile) {
            return tile.recipe != null ? tile.recipe.getDuration() : 0;
        }
    });

    public static final TileDataParameter<Integer> PROGRESS = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileSolderer>() {
        @Override
        public Integer getValue(TileSolderer tile) {
            return tile.progress;
        }
    });

    public static final TileDataParameter<Boolean> WORKING = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileSolderer>() {
        @Override
        public Boolean getValue(TileSolderer tile) {
            return tile.working;
        }
    });

    private static final String NBT_WORKING = "Working";
    private static final String NBT_PROGRESS = "Progress";

    private ItemHandlerBasic items = new ItemHandlerBasic(3, this) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            for (ISoldererRecipe recipe : API.instance().getSoldererRegistry().getRecipes()) {
                if (API.instance().getComparer().isEqualNoQuantity(recipe.getRow(slot), stack) || API.instance().getComparer().isEqualOredict(recipe.getRow(slot), stack)) {
                    return super.insertItem(slot, stack, simulate);
                }
            }

            return stack;
        }
    };
    private ItemHandlerBasic result = new ItemHandlerBasic(1, this) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }
    };
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

    private ISoldererRecipe recipe;

    private boolean working = false;
    private int progress = 0;

    public TileSolderer() {
        dataManager.addWatchedParameter(DURATION);
        dataManager.addWatchedParameter(PROGRESS);
        dataManager.addWatchedParameter(WORKING);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.soldererUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (items.getStackInSlot(1) == null && items.getStackInSlot(2) == null && result.getStackInSlot(0) == null) {
            stop();
        } else {
            ISoldererRecipe newRecipe = API.instance().getSoldererRegistry().getRecipe(items);

            if (newRecipe == null) {
                stop();
            } else if (newRecipe != recipe) {
                boolean sameItem = result.getStackInSlot(0) != null && API.instance().getComparer().isEqualNoQuantity(result.getStackInSlot(0), newRecipe.getResult());

                if (result.getStackInSlot(0) == null || (sameItem && ((result.getStackInSlot(0).stackSize + newRecipe.getResult().stackSize) <= result.getStackInSlot(0).getMaxStackSize()))) {
                    recipe = newRecipe;
                    progress = 0;
                    working = true;

                    markDirty();
                }
            } else if (working) {
                progress += 1 + upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);

                if (progress >= recipe.getDuration()) {
                    if (result.getStackInSlot(0) != null) {
                        result.getStackInSlot(0).stackSize += recipe.getResult().stackSize;
                    } else {
                        result.setStackInSlot(0, recipe.getResult().copy());
                    }

                    for (int i = 0; i < 3; ++i) {
                        if (recipe.getRow(i) != null) {
                            items.extractItem(i, recipe.getRow(i).stackSize, false);
                        }
                    }

                    recipe = null;
                    progress = 0;
                    // Don't set working to false yet, wait till the next update because we may have another stack waiting.

                    markDirty();
                }
            }
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        if (!state) {
            stop();
        }
    }

    private void stop() {
        progress = 0;
        working = false;
        recipe = null;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(items, 0, tag);
        RSUtils.readItems(upgrades, 1, tag);
        RSUtils.readItems(result, 2, tag);

        recipe = API.instance().getSoldererRegistry().getRecipe(items);

        if (tag.hasKey(NBT_WORKING)) {
            working = tag.getBoolean(NBT_WORKING);
        }

        if (tag.hasKey(NBT_PROGRESS)) {
            progress = tag.getInteger(NBT_PROGRESS);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItems(items, 0, tag);
        RSUtils.writeItems(upgrades, 1, tag);
        RSUtils.writeItems(result, 2, tag);

        tag.setBoolean(NBT_WORKING, working);
        tag.setInteger(NBT_PROGRESS, progress);

        return tag;
    }

    public ItemHandlerBasic getItems() {
        return items;
    }

    public ItemHandlerBasic getResult() {
        return result;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(items, result, upgrades);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return facing == EnumFacing.DOWN ? (T) result : (T) items;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
