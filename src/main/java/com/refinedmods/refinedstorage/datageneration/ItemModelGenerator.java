package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.Map;

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

    private <T extends Block> void coloredBlockItemModelBuilder(Map<DyeColor, RegistryObject<T>> blockMap) {
        coloredBlockItemModelBuilder(blockMap, null);
    }

    private <T extends Block> void coloredBlockItemModelBuilder(Map<DyeColor, RegistryObject<T>> blockMap, @Nullable String replacement) {
        blockMap.forEach((color, block) -> {
            String folder = replacement == null ? blockMap.get(DyeColor.LIGHT_BLUE).getId().getPath() : replacement;
            withExistingParent("item/" + block.getId().getPath(), //name of Item Model
                new ResourceLocation(RS.ID, "block/" + folder + "/" + color)); //name of Block Model
        });
    }
}
