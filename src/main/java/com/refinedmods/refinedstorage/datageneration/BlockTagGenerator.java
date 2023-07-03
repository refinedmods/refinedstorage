package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends BlockTagsProvider {
    public BlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TagAppender<Block> noRelocationTag = tag(BlockTags.create(new ResourceLocation("forge:relocation_not_supported")));

        RSBlocks.COLORED_BLOCK_TAGS.forEach((tag, map) -> {
            map.values().forEach(block -> tag(tag).add(block.get()));
            noRelocationTag.addTags(tag);
        });
        RSBlocks.STORAGE_BLOCKS.forEach((tag, block) -> noRelocationTag.add(resourceKey(block.get())));
        RSBlocks.FLUID_STORAGE_BLOCKS.forEach((tag, block) -> noRelocationTag.add(resourceKey(block.get())));

        noRelocationTag.add(
            resourceKey(RSBlocks.IMPORTER.get()),
            resourceKey(RSBlocks.EXPORTER.get()),
            resourceKey(RSBlocks.EXTERNAL_STORAGE.get()),
            resourceKey(RSBlocks.DISK_DRIVE.get()),
            resourceKey(RSBlocks.INTERFACE.get()),
            resourceKey(RSBlocks.FLUID_INTERFACE.get()),
            resourceKey(RSBlocks.STORAGE_MONITOR.get()),
            resourceKey(RSBlocks.CONSTRUCTOR.get()),
            resourceKey(RSBlocks.DESTRUCTOR.get()),
            resourceKey(RSBlocks.PORTABLE_GRID.get()),
            resourceKey(RSBlocks.CREATIVE_PORTABLE_GRID.get())
        );
    }

    private ResourceKey<Block> resourceKey(Block block) {
        return ForgeRegistries.BLOCKS.getResourceKey(block).get();
    }
}
