package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class BlockTagGenerator extends BlockTagsProvider {
    public BlockTagGenerator(DataGenerator dataGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        genBlockTagCorrectToolForDrops(RSBlocks.IMPORTER.get());
        genBlockTagCorrectToolForDrops(RSBlocks.EXPORTER.get());
        genBlockTagCorrectToolForDrops(RSBlocks.QUARTZ_ENRICHED_IRON.get());
        genBlockTagCorrectToolForDrops(RSBlocks.MACHINE_CASING.get());
        genBlockTagCorrectToolForDrops(RSBlocks.CABLE.get());
        genBlockTagCorrectToolForDrops(RSBlocks.DISK_DRIVE.get());
        genBlockTagCorrectToolForDrops(RSBlocks.EXTERNAL_STORAGE.get());
        genBlockTagCorrectToolForDrops(RSBlocks.INTERFACE.get());
        genBlockTagCorrectToolForDrops(RSBlocks.FLUID_INTERFACE.get());
        genBlockTagCorrectToolForDrops(RSBlocks.STORAGE_MONITOR.get());
        genBlockTagCorrectToolForDrops(RSBlocks.CONSTRUCTOR.get());
        genBlockTagCorrectToolForDrops(RSBlocks.DESTRUCTOR.get());
        genBlockTagCorrectToolForDrops(RSBlocks.PORTABLE_GRID.get());
        genBlockTagCorrectToolForDrops(RSBlocks.CREATIVE_PORTABLE_GRID.get());

        RSBlocks.CONTROLLER.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.CREATIVE_CONTROLLER.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.CRAFTER.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.GRID.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.CRAFTING_GRID.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.FLUID_GRID.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.PATTERN_GRID.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.SECURITY_MANAGER.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.WIRELESS_TRANSMITTER.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.RELAY.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.NETWORK_TRANSMITTER.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.NETWORK_RECEIVER.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.DISK_MANIPULATOR.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.CRAFTING_MONITOR.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.CRAFTER_MANAGER.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.DETECTOR.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.STORAGE_BLOCKS.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
        RSBlocks.FLUID_STORAGE_BLOCKS.values().forEach(block -> genBlockTagCorrectToolForDrops(block.get()));
    }

    private void genBlockTagCorrectToolForDrops(Block block) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
        tag(BlockTags.NEEDS_IRON_TOOL).add(block);
    }
}
