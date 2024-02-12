package com.refinedmods.refinedstorage.loottable;

import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class PortableGridBlockLootFunction implements LootItemFunction {
    @Override
    public LootItemFunctionType getType() {
        return RSLootFunctions.PORTABLE_GRID;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        BlockEntity blockEntity = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof PortableGridBlockEntity) {
            ((PortableGridBlockEntity) blockEntity).applyDataFromBlockEntityToItem(stack);
        }

        return stack;
    }
}
