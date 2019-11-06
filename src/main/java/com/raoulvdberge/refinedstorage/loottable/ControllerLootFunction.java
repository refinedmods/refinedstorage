package com.raoulvdberge.refinedstorage.loottable;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraftforge.energy.CapabilityEnergy;

public class ControllerLootFunction implements ILootFunction {
    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        TileEntity tile = lootContext.get(LootParameters.BLOCK_ENTITY);

        if (tile instanceof ControllerTile) {
            INetwork network = ((ControllerTile) tile).getRemovedNetwork() == null ? ((ControllerTile) tile).getNetwork() : ((ControllerTile) tile).getRemovedNetwork();

            itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> energy.receiveEnergy(network.getEnergyStorage().getEnergyStored(), false));
        }

        return itemStack;
    }
}
