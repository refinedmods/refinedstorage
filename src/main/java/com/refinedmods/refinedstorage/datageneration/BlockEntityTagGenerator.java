package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSBlockEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import java.util.concurrent.CompletableFuture;

public class BlockEntityTagGenerator extends TagsProvider<BlockEntityType<?>> {
    public BlockEntityTagGenerator(PackOutput output, String modId, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
        super(output, Registries.BLOCK_ENTITY_TYPE, provider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TagAppender<BlockEntityType<?>> packingTapeBlacklist = tag(
            TagKey.create(Registries.BLOCK_ENTITY_TYPE, new ResourceLocation("packingtape:blacklist/problematic"))
        );
        packingTapeBlacklist.add(
            resourceKey(RSBlockEntities.CONTROLLER.get()),
            resourceKey(RSBlockEntities.CREATIVE_CONTROLLER.get()),
            resourceKey(RSBlockEntities.DETECTOR.get()),
            resourceKey(RSBlockEntities.DISK_DRIVE.get()),
            resourceKey(RSBlockEntities.EXPORTER.get()),
            resourceKey(RSBlockEntities.EXTERNAL_STORAGE.get()),
            resourceKey(RSBlockEntities.GRID.get()),
            resourceKey(RSBlockEntities.CRAFTING_GRID.get()),
            resourceKey(RSBlockEntities.PATTERN_GRID.get()),
            resourceKey(RSBlockEntities.FLUID_GRID.get()),
            resourceKey(RSBlockEntities.IMPORTER.get()),
            resourceKey(RSBlockEntities.NETWORK_TRANSMITTER.get()),
            resourceKey(RSBlockEntities.NETWORK_RECEIVER.get()),
            resourceKey(RSBlockEntities.RELAY.get()),
            resourceKey(RSBlockEntities.CABLE.get()),
            resourceKey(RSBlockEntities.ONE_K_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.FOUR_K_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.SIXTEEN_K_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.SIXTY_FOUR_K_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.CREATIVE_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.CREATIVE_FLUID_STORAGE_BLOCK.get()),
            resourceKey(RSBlockEntities.SECURITY_MANAGER.get()),
            resourceKey(RSBlockEntities.INTERFACE.get()),
            resourceKey(RSBlockEntities.FLUID_INTERFACE.get()),
            resourceKey(RSBlockEntities.WIRELESS_TRANSMITTER.get()),
            resourceKey(RSBlockEntities.STORAGE_MONITOR.get()),
            resourceKey(RSBlockEntities.CONSTRUCTOR.get()),
            resourceKey(RSBlockEntities.DESTRUCTOR.get()),
            resourceKey(RSBlockEntities.DISK_MANIPULATOR.get()),
            resourceKey(RSBlockEntities.PORTABLE_GRID.get()),
            resourceKey(RSBlockEntities.CREATIVE_PORTABLE_GRID.get()),
            resourceKey(RSBlockEntities.CRAFTER.get()),
            resourceKey(RSBlockEntities.CRAFTER_MANAGER.get()),
            resourceKey(RSBlockEntities.CRAFTING_MONITOR.get())
        );
    }

    private ResourceKey<BlockEntityType<?>> resourceKey(BlockEntityType<?> type) {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE.getResourceKey(type).get();
    }

    @Override
    public String getName() {
        return "Block Entity Type Tags";
    }
}
