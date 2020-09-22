package com.refinedmods.refinedstorage.datageneration;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.refinedmods.refinedstorage.RSBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
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
            RSBlocks.COLORED_BLOCKS.forEach(block -> registerDropSelfLootTable(block.get()));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RSBlocks.COLORED_BLOCKS.stream().map(RegistryObject::get).collect(Collectors.toList());
        }
    }
}
