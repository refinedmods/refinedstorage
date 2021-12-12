package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.api.network.INetwork;
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
    protected ControllerLootFunction(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        TileEntity tile = lootContext.getParamOrNull(LootParameters.BLOCK_ENTITY);

        if (tile instanceof ControllerTile) {
            INetwork network = ((ControllerTile) tile).getRemovedNetwork() == null ? ((ControllerTile) tile).getNetwork() : ((ControllerTile) tile).getRemovedNetwork();

            itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> energy.receiveEnergy(network.getEnergyStorage().getEnergyStored(), false));
        }

        return itemStack;
    }

    @Override
    public LootFunctionType getType() {
        return RSLootFunctions.getController();
    }

    public static LootFunction.Builder<?> builder() {
        return simpleBuilder(ControllerLootFunction::new);
    }

    public static class Serializer extends LootFunction.Serializer<ControllerLootFunction> {
        @Override
        public ControllerLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditions) {
            return new ControllerLootFunction(conditions);
        }
    }
}
