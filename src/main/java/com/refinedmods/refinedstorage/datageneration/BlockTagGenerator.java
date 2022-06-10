package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class BlockTagGenerator extends BlockTagsProvider {
    public BlockTagGenerator(DataGenerator dataGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        TagAppender<Block> noRelocationTag = tag(BlockTags.create(new ResourceLocation("forge:relocation_not_supported")));
        RSBlocks.COLORED_BLOCK_TAGS.forEach((tag, map) -> {
            map.values().forEach(block -> tag(tag).add(block.get()));
            noRelocationTag.addTags(tag);
        });
    }
}
