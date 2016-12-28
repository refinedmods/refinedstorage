package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeHolder;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class NetworkNodeSolderer extends NetworkNode {
    public static final String ID = "solderer";

    private static final String NBT_WORKING = "Working";
    private static final String NBT_PROGRESS = "Progress";

    private ItemHandlerBasic items = new ItemHandlerBasic(3, new ItemHandlerListenerNetworkNode(this)) {
        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            for (ISoldererRecipe recipe : API.instance().getSoldererRegistry().getRecipes()) {
                if (API.instance().getComparer().isEqual(recipe.getRow(slot), stack, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | IComparer.COMPARE_OREDICT | IComparer.COMPARE_STRIP_NBT)) {
                    return super.insertItem(slot, stack, simulate);
                }
            }

            return stack;
        }
    };
    private ItemHandlerBasic result = new ItemHandlerBasic(1, new ItemHandlerListenerNetworkNode(this)) {
        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }
    };
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED);

    private ISoldererRecipe recipe;

    private boolean working = false;
    private int progress = 0;

    public NetworkNodeSolderer(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.soldererUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (network == null) {
            return;
        }

        if (items.getStackInSlot(1).isEmpty() && items.getStackInSlot(2).isEmpty() && result.getStackInSlot(0).isEmpty()) {
            stop();
        } else {
            ISoldererRecipe newRecipe = API.instance().getSoldererRegistry().getRecipe(items);

            if (newRecipe == null) {
                stop();
            } else if (newRecipe != recipe) {
                boolean sameItem = !result.getStackInSlot(0).isEmpty() && API.instance().getComparer().isEqualNoQuantity(result.getStackInSlot(0), newRecipe.getResult());

                if (result.getStackInSlot(0).isEmpty() || (sameItem && ((result.getStackInSlot(0).getCount() + newRecipe.getResult().getCount()) <= result.getStackInSlot(0).getMaxStackSize()))) {
                    recipe = newRecipe;
                    progress = 0;
                    working = true;

                    markDirty();
                }
            } else if (working) {
                progress += 1 + upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);

                if (progress >= recipe.getDuration()) {
                    if (!result.getStackInSlot(0).isEmpty()) {
                        result.getStackInSlot(0).grow(recipe.getResult().getCount());
                    } else {
                        result.setStackInSlot(0, recipe.getResult().copy());
                    }

                    for (int i = 0; i < 3; ++i) {
                        if (!recipe.getRow(i).isEmpty()) {
                            items.extractItem(i, recipe.getRow(i).getCount(), false);
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
    public void onConnectedStateChange(INetworkMaster network, boolean state) {
        super.onConnectedStateChange(network, state);

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
    public String getId() {
        return ID;
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

    public ISoldererRecipe getRecipe() {
        return recipe;
    }

    public boolean isWorking() {
        return working;
    }

    public int getProgress() {
        return progress;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(items, result, upgrades);
    }
}
