package com.refinedmods.refinedstorage.inventory.item;

import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ConfiguredItemsInFilterItemHandler extends ItemStackHandler {
    private final ItemStack stack;

    public ConfiguredItemsInFilterItemHandler(ItemStack stack) {
        super(27);

        this.stack = stack;

        if (stack.hasTag()) {
            StackUtils.readItems(this, 0, stack.getTag());
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        StackUtils.writeItems(this, 0, stack.getTag());
    }

    public NonNullList<ItemStack> getConfiguredItems() {
        return stacks;
    }
}
