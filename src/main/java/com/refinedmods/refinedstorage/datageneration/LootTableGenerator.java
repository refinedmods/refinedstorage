package com.refinedmods.refinedstorage.datageneration;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.ColoredNetworkBlock;
import com.refinedmods.refinedstorage.block.FluidStorageBlock;
import com.refinedmods.refinedstorage.block.PortableGridBlock;
import com.refinedmods.refinedstorage.block.StorageBlock;
import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import com.refinedmods.refinedstorage.loottable.PortableGridBlockLootFunction;
import com.refinedmods.refinedstorage.loottable.StorageBlockLootFunction;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LootTableGenerator extends LootTableProvider {
    public LootTableGenerator(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(Pair.of(RSBlockLootTables::new, LootParameterSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        //NO OP
    }

    @Override
    public String getName() {
        return "Refined Storage Loot Tables";
    }

    private static class RSBlockLootTables extends BlockLootTables {
        @Override
        protected void addTables() {
            RSBlocks.CONTROLLER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), ControllerLootFunction.builder(), true));
            RSBlocks.CREATIVE_CONTROLLER.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.CRAFTER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), CrafterLootFunction.builder(), true));
            RSBlocks.GRID.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.CRAFTING_GRID.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.FLUID_GRID.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.PATTERN_GRID.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.SECURITY_MANAGER.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.WIRELESS_TRANSMITTER.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.RELAY.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.NETWORK_TRANSMITTER.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.NETWORK_RECEIVER.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.DISK_MANIPULATOR.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.CRAFTING_MONITOR.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.CRAFTER_MANAGER.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.DETECTOR.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.STORAGE_BLOCKS.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), StorageBlockLootFunction.builder(), false));
            RSBlocks.FLUID_STORAGE_BLOCKS.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), StorageBlockLootFunction.builder(), false));
            genBlockItemLootTableWithFunction(RSBlocks.PORTABLE_GRID.get(), PortableGridBlockLootFunction.builder(), false);
            genBlockItemLootTableWithFunction(RSBlocks.CREATIVE_PORTABLE_GRID.get(), PortableGridBlockLootFunction.builder(), false);

            RSBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
                if (!(block instanceof ColoredNetworkBlock) && !(block instanceof StorageBlock) && !(block instanceof FluidStorageBlock) && !(block instanceof PortableGridBlock)) {
                    registerDropSelfLootTable(block);
                }
            });
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RSBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
        }

        private void genBlockItemLootTableWithFunction(Block block, ILootFunction.IBuilder builder, boolean survivesExplosion) {
            LootPool.Builder poolBuilder = LootPool.builder()
                    .rolls(ConstantRange.of(1))
                    .addEntry(ItemLootEntry.builder(block)
                            .acceptFunction(builder));
            if (survivesExplosion) poolBuilder.acceptCondition(SurvivesExplosion.builder());
            registerLootTable(block, LootTable.builder().addLootPool(poolBuilder));
        }
    }
}
