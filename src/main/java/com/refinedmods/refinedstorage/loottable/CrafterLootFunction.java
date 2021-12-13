package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.CrafterBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CrafterLootFunction extends LootItemConditionalFunction {
    protected CrafterLootFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder(CrafterLootFunction::new);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext lootContext) {
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

    @Override
    public LootItemFunctionType getType() {
        return RSLootFunctions.getCrafter();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<CrafterLootFunction> {
        @Override
        public CrafterLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            return new CrafterLootFunction(conditions);
        }
    }
}
