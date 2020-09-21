package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        RSBlocks.CRAFTER.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.GRID.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.PATTERN_GRID.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.FLUID_GRID.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.CRAFTING_GRID.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.CONTROLLER.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.CREATIVE_CONTROLLER.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), "controller", color));
        RSBlocks.SECURITY_MANAGER.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.RELAY.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.NETWORK_TRANSMITTER.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.NETWORK_RECEIVER.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.DISK_MANIPULATOR.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.CRAFTER_MANAGER.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.CRAFTING_MONITOR.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.DETECTOR.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
        RSBlocks.WIRELESS_TRANSMITTER.forEach((color, block) -> coloredBlockItemModelBuilder(block.get(), color));
    }

    private void coloredBlockItemModelBuilder(Block block, DyeColor color) {
        coloredBlockItemModelBuilder(block, null, color);
    }

    private void coloredBlockItemModelBuilder(Block block, String replacement, DyeColor color) {
        String blockName = block.getRegistryName().getPath();
        String name = blockName;
        if (color != DyeColor.LIGHT_BLUE) {
            name = blockName.substring(blockName.indexOf(color.getString()) + color.getString().length() + 1);
        }
        withExistingParent("item/" + blockName, //name of Item Model
            new ResourceLocation(RS.ID, "block/" + (replacement == null ? name : replacement) + "/" + color)); //name of Block Model
    }

}
