package com.refinedmods.refinedstorage.item.group;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.ColoredNetworkBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class MainItemGroup extends ItemGroup {
    public MainItemGroup() {
        super(RS.ID);
    }

    @Override
    public ItemStack createIcon() {
        ItemStack stack = new ItemStack(RSBlocks.CREATIVE_CONTROLLER);
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putInt(ColoredNetworkBlock.COLOR_NBT, DyeColor.LIGHT_BLUE.getId());
        stack.setTag(tag);
        return stack;
    }
}
