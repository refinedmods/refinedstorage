package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.item.blockitem.ColoredBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ColorMap<T extends IForgeRegistryEntry<? super T>> {
    public static final DyeColor DEFAULT_COLOR = DyeColor.LIGHT_BLUE;

    private final Map<DyeColor, RegistryObject<T>> map = new EnumMap<>(DyeColor.class);

    private DeferredRegister<Item> itemRegister;
    private DeferredRegister<Block> blockRegister;
    private List<Runnable> lateRegistration;

    public ColorMap(DeferredRegister<Block> blockRegister) {
        this.blockRegister = blockRegister;
    }

    public ColorMap(DeferredRegister<Item> itemRegister, List<Runnable> lateRegistration) {
        this.itemRegister = itemRegister;
        this.lateRegistration = lateRegistration;
    }

    public RegistryObject<T> get(DyeColor color) {
        return map.get(color);
    }

    public Collection<RegistryObject<T>> values() {
        return map.values();
    }

    public void put(DyeColor color, RegistryObject<T> object) {
        map.put(color, object);
    }

    public void forEach(BiConsumer<DyeColor, RegistryObject<T>> consumer) {
        map.forEach(consumer);
    }

    public Block[] getBlocks() {
        return map.values().stream().map(RegistryObject::get).toArray(Block[]::new);
    }

    public <S extends Block> void registerBlocks(String name, Supplier<S> blockFactory) {
        for (DyeColor color : DyeColor.values()) {
            String prefix = color != DEFAULT_COLOR ? color + "_" : "";
            RegistryObject<S> block = blockRegister.register(prefix + name, blockFactory);
            map.put(color, (RegistryObject<T>) block);
            RSBlocks.COLORED_BLOCKS.add(block);
        }
    }

    public <S extends BaseBlock> void registerItemsFromBlocks(ColorMap<S> blockMap) {
        RegistryObject<S> originalBlock = blockMap.get(DEFAULT_COLOR);
        map.put(DEFAULT_COLOR, registerBlockItemFor(originalBlock, DEFAULT_COLOR, originalBlock));
        lateRegistration.add(() -> blockMap.forEach((color, block) -> {
            if (color != DEFAULT_COLOR) {
                map.put(color, registerBlockItemFor(block, color, originalBlock));
            }
        }));
        RSItems.COLORED_ITEM_TAGS.put(ItemTags.createOptional(new ResourceLocation(RS.ID, blockMap.get(DEFAULT_COLOR).getId().getPath())), (ColorMap<BlockItem>) this);
    }

    private <S extends BaseBlock> RegistryObject<T> registerBlockItemFor(RegistryObject<S> block, DyeColor color, RegistryObject<S> translationBlock) {
        return (RegistryObject<T>) itemRegister.register(
            block.getId().getPath(),
            () -> new ColoredBlockItem(
                block.get(),
                new Item.Properties().tab(RS.MAIN_GROUP),
                color,
                BlockUtils.getBlockTranslation(translationBlock.get())
            )
        );
    }

    public <S extends BaseBlock> ActionResultType changeBlockColor(BlockState state, ItemStack heldItem, World world, BlockPos pos, PlayerEntity player) {
        DyeColor color = DyeColor.getColor(heldItem);
        if (color == null || state.getBlock().equals(map.get(color).get())) {
            return ActionResultType.PASS;
        }

        return setBlockState(getNewState((RegistryObject<S>) map.get(color), state), heldItem, world, pos, player);
    }

    private <S extends BaseBlock> BlockState getNewState(RegistryObject<S> block, BlockState state) {
        BlockState newState = block.get().defaultBlockState();

        if (((NetworkNodeBlock) block.get()).hasConnectedState()) {
            newState = newState.setValue(NetworkNodeBlock.CONNECTED, state.getValue(NetworkNodeBlock.CONNECTED));
        }
        if (block.get().getDirection() != BlockDirection.NONE) {
            newState = newState.setValue(block.get().getDirection().getProperty(), state.getValue(block.get().getDirection().getProperty()));
        }

        return newState;
    }

    public ActionResultType setBlockState(BlockState newState, ItemStack heldItem, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClientSide) {
            world.setBlockAndUpdate(pos, newState);
            if (((ServerPlayerEntity) player).gameMode.getGameModeForPlayer() != GameType.CREATIVE) {
                heldItem.shrink(1);
            }
        }

        return ActionResultType.SUCCESS;
    }
}
