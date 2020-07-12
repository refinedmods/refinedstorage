package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ControllerBlockItem extends EnergyBlockItem {
    public ControllerBlockItem(ControllerBlock block) {
        super(block, new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1), block.getType() == NetworkType.CREATIVE, () -> RS.SERVER_CONFIG.getController().getCapacity());

        this.setRegistryName(block.getRegistryName());
        ItemModelsProperties.func_239418_a_(this, new ResourceLocation("energy_type"), (stack, world, entity) -> {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
            if (storage != null) {
                return Network.getEnergyType(storage.getEnergyStored(), storage.getMaxEnergyStored()).ordinal();
            }

            return ControllerBlock.EnergyType.OFF.ordinal();
        });
    }
}
