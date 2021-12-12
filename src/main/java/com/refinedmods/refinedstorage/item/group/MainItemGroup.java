package com.refinedmods.refinedstorage.item.group;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MainItemGroup extends ItemGroup {
    public MainItemGroup() {
        super(RS.ID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RSBlocks.CREATIVE_CONTROLLER.get(ColorMap.DEFAULT_COLOR).get());
    }
}
