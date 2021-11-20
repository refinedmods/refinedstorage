package com.refinedmods.refinedstorage.item.property;

import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class ControllerItemPropertyGetter implements IItemPropertyGetter {
    @Override
    public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
        if (storage != null) {
            return Network.getEnergyType(storage.getEnergyStored(), storage.getMaxEnergyStored()).ordinal();
        }

        return ControllerBlock.EnergyType.OFF.ordinal();
    }
}
