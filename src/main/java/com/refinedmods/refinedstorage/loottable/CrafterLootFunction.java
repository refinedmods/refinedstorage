package com.refinedmods.refinedstorage.loottable;

import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.CrafterBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class CrafterLootFunction implements LootItemFunction {
    @Override
    public LootItemFunctionType getType() {
        return RSLootFunctions.CRAFTER;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        BlockEntity blockEntity = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        CrafterNetworkNode removedNode = ((CrafterBlockEntity) blockEntity).getRemovedNode();
        if (removedNode == null) {
            removedNode = ((CrafterBlockEntity) blockEntity).getNode();
        }

        if (removedNode.getDisplayName() != null) {
            stack.setHoverName(removedNode.getDisplayName());
        }

        return stack;
    }
}
