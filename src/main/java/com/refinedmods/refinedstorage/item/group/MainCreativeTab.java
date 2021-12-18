package com.refinedmods.refinedstorage.item.group;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MainCreativeTab extends CreativeModeTab {
    public MainCreativeTab() {
        super(RS.ID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RSBlocks.CREATIVE_CONTROLLER.get(ColorMap.DEFAULT_COLOR).get());
    }
}
