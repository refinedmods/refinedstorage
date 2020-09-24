package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.item.blockitem.ColoredBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ColorMap<T extends IForgeRegistryEntry<? super T>> {
    Map<DyeColor, RegistryObject<T>> colormap = new HashMap<>();

    public RegistryObject<T> get(DyeColor color) {
        return colormap.get(color);
    }

    public Collection<RegistryObject<T>> values() {
        return colormap.values();
    }

    public void put(DyeColor color, RegistryObject<T> object) {
        colormap.put(color, object);
    }

    public void forEach(BiConsumer<DyeColor, RegistryObject<T>> consumer) {
        colormap.forEach(consumer);
    }

    public Block[] getBlocks() {
        return colormap.values().stream().map(RegistryObject::get).toArray(Block[]::new);
    }

    public <S extends Block> void registerColoredBlocks(String name, Supplier<S> blockFactory) {
        for (DyeColor color : DyeColor.values()) {
            String prefix = color != BlockUtils.DEFAULT_COLOR ? color + "_" : "";
            RegistryObject<S> block = RSBlocks.BLOCKS.register(prefix + name, blockFactory);
            colormap.put(color, (RegistryObject<T>) block);
            RSBlocks.COLORED_BLOCKS.add(block);
        }
    }

    public <S extends BaseBlock> void registerColoredItemsFromBlocks(ColorMap<S> blockMap) {
        RegistryObject<S> originalBlock = blockMap.get(BlockUtils.DEFAULT_COLOR);
        colormap.put(BlockUtils.DEFAULT_COLOR, registerColoredBlockItemFor(originalBlock, BlockUtils.DEFAULT_COLOR, originalBlock));
        RSItems.LATE_REGISTRATION.add(() -> blockMap.forEach((color, block) -> {
            if (color != BlockUtils.DEFAULT_COLOR) {
                colormap.put(color, registerColoredBlockItemFor(block, color, originalBlock));
            }
        }));
        RSItems.COLORED_ITEM_TAGS.put(ItemTags.createOptional(new ResourceLocation(RS.ID, blockMap.get(BlockUtils.DEFAULT_COLOR).getId().getPath())), (ColorMap<BlockItem>) this);
    }

    private <S extends BaseBlock> RegistryObject<T> registerColoredBlockItemFor(RegistryObject<S> block, DyeColor color, RegistryObject<S> translationBlock) {
        return (RegistryObject<T>) RSItems.ITEMS.register(block.getId().getPath(), () -> new ColoredBlockItem(block.get(), new Item.Properties().group(RS.MAIN_GROUP), color, translationBlock));
    }
}
