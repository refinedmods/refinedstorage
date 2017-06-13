package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerProxy;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class NetworkNodeSolderer extends NetworkNode {
    public static final String ID = "solderer";

    public static final String NBT_WORKING = "Working";
    private static final String NBT_PROGRESS = "Progress";

    private ItemHandlerBase ingredients = new ItemHandlerBase(3, new ItemHandlerListenerNetworkNode(this)) {
        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            for (ISoldererRecipe recipe : API.instance().getSoldererRegistry().getRecipes()) {
                for (ItemStack possibility : recipe.getRow(slot)) {
                    if (API.instance().getComparer().isEqualNoQuantity(possibility, stack)) {
                        return super.insertItem(slot, stack, simulate);
                    }
                }
            }

            return stack;
        }
    };

    private ItemHandlerBase result = new ItemHandlerBase(1, new ItemHandlerListenerNetworkNode(this)) {
        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }
    };

    private ItemHandlerProxy items = new ItemHandlerProxy(ingredients, result);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED);

    private ISoldererRecipe recipe;

    private boolean working = false;
    private boolean wasWorking = false;
    private int progress = 0;

    public NetworkNodeSolderer(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.soldererUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (wasWorking != working) {
            wasWorking = working;

            markDirty();

            RSUtils.updateBlock(world, pos);
        }

        if (network == null || !canUpdate()) {
            return;
        }

        if (ingredients.getStackInSlot(1).isEmpty() && ingredients.getStackInSlot(2).isEmpty() && result.getStackInSlot(0).isEmpty()) {
            stop();
        } else {
            ISoldererRecipe newRecipe = API.instance().getSoldererRegistry().getRecipe(ingredients);

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
                            ingredients.extractItem(i, recipe.getRow(i).get(0).getCount(), false);
                        }
                    }

                    recipe = null;
                    progress = 0;
                    // Don't set working to false yet, wait till the next update because we may have another stack waiting.
                }

                markDirty();
            }
        }
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        if (!state) {
            stop();
        }
    }

    private void stop() {
        progress = 0;
        working = false;
        recipe = null;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(ingredients, 0, tag);
        RSUtils.readItems(upgrades, 1, tag);
        RSUtils.readItems(result, 2, tag);

        recipe = API.instance().getSoldererRegistry().getRecipe(ingredients);

        if (tag.hasKey(NBT_WORKING)) {
            working = tag.getBoolean(NBT_WORKING);
            wasWorking = working;
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

        RSUtils.writeItems(ingredients, 0, tag);
        RSUtils.writeItems(upgrades, 1, tag);
        RSUtils.writeItems(result, 2, tag);

        tag.setBoolean(NBT_WORKING, working);
        tag.setInteger(NBT_PROGRESS, progress);

        return tag;
    }

    public ItemHandlerBase getIngredients() {
        return ingredients;
    }

    public ItemHandlerBase getResult() {
        return result;
    }

    public ItemHandlerProxy getItems() {
        return items;
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
        return new CombinedInvWrapper(ingredients, result, upgrades);
    }
}
