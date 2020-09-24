package com.refinedmods.refinedstorage.datageneration;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
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
            RSBlocks.CONTROLLER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), ControllerLootFunction.builder()));
            RSBlocks.CREATIVE_CONTROLLER.values().forEach(block -> registerDropSelfLootTable(block.get()));
            RSBlocks.CRAFTER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), CrafterLootFunction.builder()));
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
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RSBlocks.COLORED_BLOCKS.stream().map(RegistryObject::get).collect(Collectors.toList());
        }

        private void genBlockItemLootTableWithFunction(Block block, ILootFunction.IBuilder builder) {
            registerLootTable(block, LootTable.builder().addLootPool(
                LootPool.builder()
                    .rolls(ConstantRange.of(1))
                    .addEntry(ItemLootEntry.builder(block)
                        .acceptFunction(builder))
                    .acceptCondition(SurvivesExplosion.builder())));
        }
    }
}
