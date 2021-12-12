package com.refinedmods.refinedstorage.datageneration;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

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
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(Pair.of(RSBlockLootTables::new, LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        //NO OP
    }

    @Override
    public String getName() {
        return "Refined Storage Loot Tables";
    }

    private static class RSBlockLootTables extends BlockLoot {
        @Override
        protected void addTables() {
            RSBlocks.CONTROLLER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), ControllerLootFunction.builder()));
            RSBlocks.CREATIVE_CONTROLLER.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.CRAFTER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), CrafterLootFunction.builder()));
            RSBlocks.GRID.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.CRAFTING_GRID.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.FLUID_GRID.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.PATTERN_GRID.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.SECURITY_MANAGER.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.WIRELESS_TRANSMITTER.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.RELAY.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.NETWORK_TRANSMITTER.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.NETWORK_RECEIVER.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.DISK_MANIPULATOR.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.CRAFTING_MONITOR.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.CRAFTER_MANAGER.values().forEach(block -> dropSelf(block.get()));
            RSBlocks.DETECTOR.values().forEach(block -> dropSelf(block.get()));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RSBlocks.COLORED_BLOCKS.stream().map(RegistryObject::get).collect(Collectors.toList());
        }

        private void genBlockItemLootTableWithFunction(Block block, LootItemFunction.Builder builder) {
            add(block, LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block)
                        .apply(builder))
                    .when(ExplosionCondition.survivesExplosion())));
        }
    }
}
