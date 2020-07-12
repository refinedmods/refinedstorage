package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.setup.CommonSetup;
import com.refinedmods.refinedstorage.tile.ControllerTile;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.CapabilityEnergy;

public class ControllerLootFunction extends LootFunction {
    protected ControllerLootFunction(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ItemStack doApply(ItemStack itemStack, LootContext lootContext) {
        TileEntity tile = lootContext.get(LootParameters.BLOCK_ENTITY);

        if (tile instanceof ControllerTile) {
            INetwork network = ((ControllerTile) tile).getRemovedNetwork() == null ? ((ControllerTile) tile).getNetwork() : ((ControllerTile) tile).getRemovedNetwork();

            itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> energy.receiveEnergy(network.getEnergyStorage().getEnergyStored(), false));
        }

        return itemStack;
    }

    @Override
    public LootFunctionType func_230425_b_() {
        return CommonSetup.LOOTFUNCTION_CONTROLLER;
    }

    public static class Serializer extends LootFunction.Serializer<ControllerLootFunction> {

        @Override
        public ControllerLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new ControllerLootFunction(conditionsIn);
        }

    }

}
