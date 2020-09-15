package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        coloredItemModelBuilder(RSBlocks.CRAFTER);
        coloredItemModelBuilder(RSBlocks.GRID);
        coloredItemModelBuilder(RSBlocks.PATTERN_GRID);
        coloredItemModelBuilder(RSBlocks.FLUID_GRID);
        coloredItemModelBuilder(RSBlocks.CRAFTING_GRID);
        coloredItemModelBuilder(RSBlocks.CONTROLLER);
        coloredItemModelBuilder(RSBlocks.CREATIVE_CONTROLLER, "controller");
        coloredItemModelBuilder(RSBlocks.SECURITY_MANAGER);
        coloredItemModelBuilder(RSBlocks.RELAY);
        coloredItemModelBuilder(RSBlocks.NETWORK_TRANSMITTER);
        coloredItemModelBuilder(RSBlocks.NETWORK_RECEIVER);
        coloredItemModelBuilder(RSBlocks.DISK_MANIPULATOR);
        coloredItemModelBuilder(RSBlocks.CRAFTER_MANAGER);
        coloredItemModelBuilder(RSBlocks.CRAFTING_MONITOR);

    }

    private void coloredItemModelBuilder(Block block) {
        coloredItemModelBuilder(block, null);
    }

    private void coloredItemModelBuilder(Block block, String replacement) {
        String name = block.getRegistryName().getPath();
        ItemModelBuilder builder = getBuilder("item/" + name); //name of the Item Model with overrides
        for (int i = 0; i < DyeColor.values().length; i++) {
            builder.override().predicate(new ResourceLocation(RS.ID, "color"), i)
                .model(withExistingParent("item/" + name + "/" + DyeColor.byId(i), //name of actual Item Model
                    new ResourceLocation(RS.ID, "block/" + (replacement == null ? name : replacement) + "/" + DyeColor.byId(i)))) //name of Block Model
                .end();
        }
    }
}
