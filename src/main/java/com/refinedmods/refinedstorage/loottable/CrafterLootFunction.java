package com.refinedmods.refinedstorage.loottable;

import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.refinedmods.refinedstorage.tile.CrafterTile;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class CrafterLootFunction implements ILootFunction {
    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        TileEntity tile = lootContext.get(LootParameters.BLOCK_ENTITY);

        CrafterNetworkNode removedNode = ((CrafterTile) tile).getRemovedNode();
        if (removedNode == null) {
            removedNode = ((CrafterTile) tile).getNode();
        }

        if (removedNode.getDisplayName() != null) {
            stack.setDisplayName(removedNode.getDisplayName());
        }

        return stack;
    }
}
