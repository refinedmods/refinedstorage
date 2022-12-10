package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ControllerLootFunction extends LootItemConditionalFunction {
    protected ControllerLootFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder(ControllerLootFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        BlockEntity blockEntity = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof ControllerBlockEntity) {
            INetwork network = ((ControllerBlockEntity) blockEntity).getRemovedNetwork() == null ? ((ControllerBlockEntity) blockEntity).getNetwork() : ((ControllerBlockEntity) blockEntity).getRemovedNetwork();

            itemStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> energy.receiveEnergy(network.getEnergyStorage().getEnergyStored(), false));
        }

        return itemStack;
    }

    @Override
    public LootItemFunctionType getType() {
        return RSLootFunctions.CONTROLLER.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<ControllerLootFunction> {
        @Override
        public ControllerLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            return new ControllerLootFunction(conditions);
        }
    }
}
