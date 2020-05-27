package com.refinedmods.refinedstorage.loottable;

import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class PortableGridBlockLootFunction implements ILootFunction {
    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        TileEntity tile = lootContext.get(LootParameters.BLOCK_ENTITY);

        if (tile instanceof PortableGridTile) {
            ((PortableGridTile) tile).applyDataFromTileToItem(stack);
        }

        return stack;
    }
}
