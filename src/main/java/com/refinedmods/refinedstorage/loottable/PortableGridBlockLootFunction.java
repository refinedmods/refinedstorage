package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile;
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
        BlockEntity tile = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        if (tile instanceof PortableGridTile) {
            ((PortableGridTile) tile).applyDataFromTileToItem(stack);
        }

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return RSLootFunctions.getPortableGrid();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<PortableGridBlockLootFunction> {
        @Override
        public PortableGridBlockLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            return new PortableGridBlockLootFunction(conditions);
        }
    }
}
