package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class TagGenerator extends ItemTagsProvider {
    public TagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        RSItems.COLORED_ITEM_TAGS.forEach((tag, map) -> map.values().forEach(item -> tag(tag).add(item.get())));
    }
}
