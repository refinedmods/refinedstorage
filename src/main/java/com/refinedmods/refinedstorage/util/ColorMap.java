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
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ColorMap<T extends IForgeRegistryEntry<? super T>> {
    public static final DyeColor DEFAULT_COLOR = DyeColor.LIGHT_BLUE;

    private final Map<DyeColor, RegistryObject<T>> colorMap = new HashMap<>();

    private DeferredRegister<Item> itemRegister = null;
    private DeferredRegister<Block> blockRegister = null;
    private List<Runnable> lateRegistration = null;
    private Map<Tags.IOptionalNamedTag<Item>, ColorMap<BlockItem>> itemTags = null;

    public ColorMap(DeferredRegister<Block> blockRegister) {
        this.blockRegister = blockRegister;
    }

    public ColorMap(DeferredRegister<Item> itemRegister, List<Runnable> lateRegistration, Map<Tags.IOptionalNamedTag<Item>, ColorMap<BlockItem>> itemTags) {
        this.itemRegister = itemRegister;
        this.lateRegistration = lateRegistration;
        this.itemTags = itemTags;
    }

    public RegistryObject<T> get(DyeColor color) {
        return colorMap.get(color);
    }

    public Collection<RegistryObject<T>> values() {
        return colorMap.values();
    }

    public void put(DyeColor color, RegistryObject<T> object) {
        colorMap.put(color, object);
    }

    public void forEach(BiConsumer<DyeColor, RegistryObject<T>> consumer) {
        colorMap.forEach(consumer);
    }

    public Block[] getBlocks() {
        return colorMap.values().stream().map(RegistryObject::get).toArray(Block[]::new);
    }

    public <S extends Block> void registerBlocks(String name, Supplier<S> blockFactory) {
        for (DyeColor color : DyeColor.values()) {
            String prefix = color != DEFAULT_COLOR ? color + "_" : "";
            RegistryObject<S> block = blockRegister.register(prefix + name, blockFactory);
            colorMap.put(color, (RegistryObject<T>) block);
            RSBlocks.COLORED_BLOCKS.add(block);
        }
    }

    public <S extends BaseBlock> void registerItemsFromBlocks(ColorMap<S> blockMap) {
        RegistryObject<S> originalBlock = blockMap.get(DEFAULT_COLOR);
        colorMap.put(DEFAULT_COLOR, registerBlockItemFor(originalBlock, DEFAULT_COLOR, originalBlock));
        lateRegistration.add(() -> blockMap.forEach((color, block) -> {
            if (color != DEFAULT_COLOR) {
                colorMap.put(color, registerBlockItemFor(block, color, originalBlock));
            }
        }));
        RSItems.COLORED_ITEM_TAGS.put(ItemTags.createOptional(new ResourceLocation(RS.ID, blockMap.get(DEFAULT_COLOR).getId().getPath())), (ColorMap<BlockItem>) this);
    }

    private <S extends BaseBlock> RegistryObject<T> registerBlockItemFor(RegistryObject<S> block, DyeColor color, RegistryObject<S> translationBlock) {
        return (RegistryObject<T>) itemRegister.register(block.getId().getPath(), () -> new ColoredBlockItem(block.get(), new Item.Properties().group(RS.MAIN_GROUP), color, new StringTextComponent(translationBlock.get().getTranslationKey())));
    }
}
