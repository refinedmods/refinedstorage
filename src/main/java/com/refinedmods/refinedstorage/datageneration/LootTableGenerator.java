package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.loottable.ControllerLootFunction;
import com.refinedmods.refinedstorage.loottable.CrafterLootFunction;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collections;
import java.util.stream.Collectors;

public class LootTableGenerator extends BlockLootSubProvider {
    public LootTableGenerator() {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        RSBlocks.CONTROLLER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(), ControllerLootFunction::new));
        RSBlocks.CREATIVE_CONTROLLER.values().forEach(block -> dropSelf(block.get()));
        RSBlocks.CRAFTER.values().forEach(block -> genBlockItemLootTableWithFunction(block.get(),
            CrafterLootFunction::new));
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
        return RSBlocks.COLORED_BLOCKS.stream().map(DeferredHolder::get).collect(Collectors.toList());
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
