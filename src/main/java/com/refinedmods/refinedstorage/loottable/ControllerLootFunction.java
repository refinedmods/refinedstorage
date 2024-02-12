package com.refinedmods.refinedstorage.loottable;

import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ControllerLootFunction implements LootItemFunction {
    @Override
    public LootItemFunctionType getType() {
        return RSLootFunctions.CONTROLLER;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        BlockEntity blockEntity = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof ControllerBlockEntity) {
            INetwork network = ((ControllerBlockEntity) blockEntity).getRemovedNetwork() == null ? ((ControllerBlockEntity) blockEntity).getNetwork() : ((ControllerBlockEntity) blockEntity).getRemovedNetwork();

            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energyStorage != null) {
                energyStorage.receiveEnergy(network.getEnergyStorage().getEnergyStored(), false);
            }
        }

        return stack;
    }
}
