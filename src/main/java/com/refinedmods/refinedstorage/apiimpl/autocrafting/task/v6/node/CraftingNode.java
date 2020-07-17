package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.IoUtil;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

public class CraftingNode extends Node {
    private static final String NBT_RECIPE = "Recipe";
    private final NonNullList<ItemStack> recipe;

    public CraftingNode(ICraftingPattern pattern, boolean root, NonNullList<ItemStack> recipe) {
        super(pattern, root);
        this.recipe = recipe;
    }

    public CraftingNode(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        super(network, tag);
        this.recipe = NonNullList.create();
        ListNBT tookList = tag.getList(NBT_RECIPE, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tookList.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(tookList.getCompound(i));

            // Can be empty.
            recipe.add(stack);
        }
    }

    @Override
    public void update(INetwork network, int ticks, NodeList nodes, IStorageDisk<ItemStack> internalStorage, IStorageDisk<FluidStack> internalFluidStorage) {
        for (ICraftingPatternContainer container : network.getCraftingManager().getAllContainer(getPattern())) {
            int interval = container.getUpdateInterval();
            if (interval < 0) {
                throw new IllegalStateException(container + " has an update interval of < 0");
            }

            if (interval == 0 || ticks % interval == 0) {
                for (int i = 0; i < container.getMaximumSuccessfulCraftingUpdates(); i++) {
                    if (getQuantity() <= 0) {
                        nodes.remove(this);
                        return;
                    }

                    if (IoUtil.extractFromInternalItemStorage(getItemsToUse(true).getStacks(), internalStorage, Action.SIMULATE) != null) {
                        IoUtil.extractFromInternalItemStorage(getItemsToUse(false).getStacks(), internalStorage, Action.PERFORM);

                        ItemStack output = getPattern().getOutput(getRecipe());

                        if (!isRoot()) {
                            internalStorage.insert(output, output.getCount(), Action.PERFORM);
                        } else {
                            ItemStack remainder = network.insertItem(output, output.getCount(), Action.PERFORM);

                            internalStorage.insert(remainder, remainder.getCount(), Action.PERFORM);
                        }

                        // Byproducts need to always be inserted in the internal storage for later reuse further in the task.
                        // Regular outputs can be inserted into the network *IF* it's a root since it's *NOT* expected to be used later on.
                        for (ItemStack byp : getPattern().getByproducts(getRecipe())) {
                            internalStorage.insert(byp, byp.getCount(), Action.PERFORM);
                        }

                        next();
                        // TODO currentStep++;
                        network.getCraftingManager().onTaskChanged();
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public NonNullList<ItemStack> getRecipe() {
        return recipe;
    }

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = super.writeToNbt();

        ListNBT tookList = new ListNBT();
        for (ItemStack took : this.recipe) {
            tookList.add(StackUtils.serializeStackToNbt(took));
        }

        tag.put(NBT_RECIPE, tookList);

        return tag;
    }
}
