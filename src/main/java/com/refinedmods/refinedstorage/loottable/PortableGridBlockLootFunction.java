package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;

public class PortableGridBlockLootFunction extends LootFunction {
    protected PortableGridBlockLootFunction(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext lootContext) {
        TileEntity tile = lootContext.get(LootParameters.BLOCK_ENTITY);

        if (tile instanceof PortableGridTile) {
            ((PortableGridTile) tile).applyDataFromTileToItem(stack);
        }

        return stack;
    }

    @Override
    public LootFunctionType func_230425_b_() {
        return RSLootFunctions.PORTABLE_GRID;
    }

    public static class Serializer extends LootFunction.Serializer<PortableGridBlockLootFunction> {
        @Override
        public PortableGridBlockLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditions) {
            return new PortableGridBlockLootFunction(conditions);
        }
    }
}
