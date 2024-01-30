package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSItems;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemTagGenerator extends ItemTagsProvider {
    public ItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                            CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_, String modId,
                            ExistingFileHelper existingFileHelper) {
        super(output, lookup, p_275322_, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        RSItems.COLORED_ITEM_TAGS.forEach((tag, map) -> map.values().forEach(item -> tag(tag).add(item.get())));
    }
}
