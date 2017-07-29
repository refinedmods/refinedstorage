package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerProxy;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
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
                    if (API.instance().getComparer().isEqual(possibility, stack, IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE | IComparer.COMPARE_STRIP_NBT)) {
                        return super.insertItem(slot, stack, simulate);
                    }
                }
            }

            return stack;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            recipe = API.instance().getSoldererRegistry().getRecipe(this);
            progress = 0;
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

    private boolean wasWorking;
    private boolean working;
    private ISoldererRecipe recipe;
    private int progress;

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

            WorldUtils.updateBlock(world, pos);
        }

        if (network == null || !canUpdate()) {
            return;
        }

        if (working) {
            if (recipe == null) {
                working = false;

                markDirty();
            } else if ((result.getStackInSlot(0).isEmpty() || API.instance().getComparer().isEqualNoQuantity(recipe.getResult(), result.getStackInSlot(0))) && result.getStackInSlot(0).getCount() + recipe.getResult().getCount() <= result.getStackInSlot(0).getMaxStackSize()) {
                progress++;

                if (progress >= getDuration()) {
                    ItemStack resultSlot = result.getStackInSlot(0);

                    if (resultSlot.isEmpty()) {
                        result.setStackInSlot(0, recipe.getResult().copy());
                    } else {
                        resultSlot.grow(recipe.getResult().getCount());
                    }

                    for (int i = 0; i < 3; ++i) {
                        ItemStack ingredientSlot = ingredients.getStackInSlot(i);

                        if (!ingredientSlot.isEmpty()) {
                            ingredientSlot.shrink(recipe.getRow(i).get(0).getCount());
                        }
                    }

                    recipe = API.instance().getSoldererRegistry().getRecipe(ingredients);
                    progress = 0;
                }

                markDirty();
            }
        } else if (recipe != null) {
            working = true;

            markDirty();
        }
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        if (!state) {
            recipe = null;
            progress = 0;
            working = false;
        } else {
            recipe = API.instance().getSoldererRegistry().getRecipe(ingredients);
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(ingredients, 0, tag);
        StackUtils.readItems(upgrades, 1, tag);
        StackUtils.readItems(result, 2, tag);

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

        StackUtils.writeItems(ingredients, 0, tag);
        StackUtils.writeItems(upgrades, 1, tag);
        StackUtils.writeItems(result, 2, tag);

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

    public boolean isWorking() {
        return working;
    }

    public int getProgress() {
        return progress;
    }

    public int getDuration() {
        if (recipe == null) {
            return 0;
        }

        return (int) ((float) recipe.getDuration() - ((float) recipe.getDuration() / 100F * ((float) upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED) * RS.INSTANCE.config.soldererSpeedIncreasePerSpeedUpgrade)));
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(ingredients, result, upgrades);
    }
}
