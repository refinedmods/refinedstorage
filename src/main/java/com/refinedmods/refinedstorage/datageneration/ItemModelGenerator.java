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
        coloredBlockItemModelBuilder(RSBlocks.CRAFTER);
        coloredBlockItemModelBuilder(RSBlocks.GRID);
        coloredBlockItemModelBuilder(RSBlocks.PATTERN_GRID);
        coloredBlockItemModelBuilder(RSBlocks.FLUID_GRID);
        coloredBlockItemModelBuilder(RSBlocks.CRAFTING_GRID);
        coloredBlockItemModelBuilder(RSBlocks.CONTROLLER);
        coloredBlockItemModelBuilder(RSBlocks.CREATIVE_CONTROLLER, "controller");
        coloredBlockItemModelBuilder(RSBlocks.SECURITY_MANAGER);
        coloredBlockItemModelBuilder(RSBlocks.RELAY);
        coloredBlockItemModelBuilder(RSBlocks.NETWORK_TRANSMITTER);
        coloredBlockItemModelBuilder(RSBlocks.NETWORK_RECEIVER);
        coloredBlockItemModelBuilder(RSBlocks.DISK_MANIPULATOR);
        coloredBlockItemModelBuilder(RSBlocks.CRAFTER_MANAGER);
        coloredBlockItemModelBuilder(RSBlocks.CRAFTING_MONITOR);
        coloredBlockItemModelBuilder(RSBlocks.DETECTOR);
        coloredBlockItemModelBuilder(RSBlocks.WIRELESS_TRANSMITTER);
    }

    private void coloredBlockItemModelBuilder(Block block) {
        coloredBlockItemModelBuilder(block, null);
    }

    private void coloredBlockItemModelBuilder(Block block, String replacement) {
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
