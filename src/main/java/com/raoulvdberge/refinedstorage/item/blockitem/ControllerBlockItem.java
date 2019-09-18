package com.raoulvdberge.refinedstorage.item.blockitem;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.ControllerBlock;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ControllerBlockItem extends EnergyBlockItem {
    public ControllerBlockItem(ControllerBlock block) {
        super(block, new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1), block.getType() == ControllerBlock.Type.CREATIVE, () -> RS.CONFIG.getController().getCapacity());

        this.setRegistryName(block.getRegistryName());
        this.addPropertyOverride(new ResourceLocation("energy_type"), (stack, world, entity) -> {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
            if (storage != null) {
                return ControllerTile.getEnergyType(storage.getEnergyStored(), storage.getMaxEnergyStored()).ordinal();
            }

            return ControllerBlock.EnergyType.OFF.ordinal();
        });
    }
}
