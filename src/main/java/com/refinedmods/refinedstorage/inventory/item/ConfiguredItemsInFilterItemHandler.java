package com.refinedmods.refinedstorage.inventory.item;

import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

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
            stack.setTag(new CompoundNBT());
        }

        StackUtils.writeItems(this, 0, stack.getTag());
    }

    public NonNullList<ItemStack> getConfiguredItems() {
        return stacks;
    }
}
