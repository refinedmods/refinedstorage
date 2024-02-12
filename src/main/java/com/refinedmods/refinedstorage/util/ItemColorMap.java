package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.item.blockitem.ColoredBlockItem;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemColorMap extends ColorMap<Item, ColoredBlockItem> {
    public ItemColorMap(DeferredRegister<Item> registry) {
        super(registry);
    }

    public ItemColorMap(DeferredRegister<Item> registry, List<Runnable> lateRegistration) {
        super(registry, lateRegistration);
    }

    public <S extends BaseBlock> void registerItemsFromBlocks(ColorMap<Block, S> blockMap) {
        DeferredHolder<Block, S> originalBlock = blockMap.get(DEFAULT_COLOR);
        map.put(DEFAULT_COLOR, registerBlockItemFor(originalBlock, DEFAULT_COLOR, originalBlock));
        lateRegistration.add(() -> blockMap.forEach((color, block) -> {
            if (color != DEFAULT_COLOR) {
                map.put(color, registerBlockItemFor(block, color, originalBlock));
            }
        }));
        RSItems.COLORED_ITEM_TAGS.put(
            ItemTags.create(new ResourceLocation(RS.ID, blockMap.get(DEFAULT_COLOR).getId().getPath())),
            this
        );
    }

    private <S extends BaseBlock> DeferredHolder<Item, ColoredBlockItem> registerBlockItemFor(
        DeferredHolder<Block, S> block,
        DyeColor color,
        DeferredHolder<Block, S> translationBlock
    ) {
        return registry.register(
            block.getId().getPath(),
            () -> new ColoredBlockItem(
                block.get(),
                new Item.Properties(),
                color,
                BlockUtils.getBlockTranslation(translationBlock.get())
            )
        );
    }
}
