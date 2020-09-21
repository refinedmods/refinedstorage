package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSItems;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class TagGenerator extends ItemTagsProvider {

    public TagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, modId, existingFileHelper);
    }

    @Override
    protected void registerTags() {
        RSItems.COLORED_ITEM_TAGS.forEach((tag, map) -> {
            map.values().forEach(item -> {
                getOrCreateBuilder(tag).add(item.get());
            });
        });
    }
}
