package com.raoulvdberge.refinedstorage.render.meshdefinition;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.enums.ControllerEnergyType;
import com.raoulvdberge.refinedstorage.block.enums.ControllerType;
import com.raoulvdberge.refinedstorage.item.itemblock.ItemBlockController;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class ItemMeshDefinitionController implements ItemMeshDefinition {
    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        ControllerEnergyType energyType = stack.getItemDamage() == ControllerType.CREATIVE.getId() ? ControllerEnergyType.ON : TileController.getEnergyType(ItemBlockController.getEnergyStored(stack), RS.INSTANCE.config.controllerCapacity);

        return new ModelResourceLocation(RS.ID + ":controller", "energy_type=" + energyType);
    }
}
