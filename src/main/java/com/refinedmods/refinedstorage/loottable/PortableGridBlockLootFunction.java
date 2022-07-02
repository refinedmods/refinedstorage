package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class PortableGridBlockLootFunction extends LootItemConditionalFunction {
    protected PortableGridBlockLootFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext lootContext) {
        BlockEntity blockEntity = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof PortableGridBlockEntity) {
            ((PortableGridBlockEntity) blockEntity).applyDataFromBlockEntityToItem(stack);
        }

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return RSLootFunctions.PORTABLE_GRID.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<PortableGridBlockLootFunction> {
        @Override
        public PortableGridBlockLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            return new PortableGridBlockLootFunction(conditions);
        }
    }
}
