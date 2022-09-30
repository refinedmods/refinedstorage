package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RSBlockEntities;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class BlockEntityTagGenerator extends TagsProvider<BlockEntityType<?>> {
    public BlockEntityTagGenerator(DataGenerator dataGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, Registry.BLOCK_ENTITY_TYPE, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        TagAppender<BlockEntityType<?>> packingTapeBlacklist = tag(TagKey.create(Registry.BLOCK_ENTITY_TYPE_REGISTRY, new ResourceLocation("packingtape:blacklist/problematic")));
        packingTapeBlacklist.add(
                RSBlockEntities.CONTROLLER.get(),
                RSBlockEntities.CREATIVE_CONTROLLER.get(),
                RSBlockEntities.DETECTOR.get(),
                RSBlockEntities.DISK_DRIVE.get(),
                RSBlockEntities.EXPORTER.get(),
                RSBlockEntities.EXTERNAL_STORAGE.get(),
                RSBlockEntities.GRID.get(),
                RSBlockEntities.CRAFTING_GRID.get(),
                RSBlockEntities.PATTERN_GRID.get(),
                RSBlockEntities.FLUID_GRID.get(),
                RSBlockEntities.IMPORTER.get(),
                RSBlockEntities.NETWORK_TRANSMITTER.get(),
                RSBlockEntities.NETWORK_RECEIVER.get(),
                RSBlockEntities.RELAY.get(),
                RSBlockEntities.CABLE.get(),
                RSBlockEntities.ONE_K_STORAGE_BLOCK.get(),
                RSBlockEntities.FOUR_K_STORAGE_BLOCK.get(),
                RSBlockEntities.SIXTEEN_K_STORAGE_BLOCK.get(),
                RSBlockEntities.SIXTY_FOUR_K_STORAGE_BLOCK.get(),
                RSBlockEntities.CREATIVE_STORAGE_BLOCK.get(),
                RSBlockEntities.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK.get(),
                RSBlockEntities.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK.get(),
                RSBlockEntities.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK.get(),
                RSBlockEntities.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK.get(),
                RSBlockEntities.CREATIVE_FLUID_STORAGE_BLOCK.get(),
                RSBlockEntities.SECURITY_MANAGER.get(),
                RSBlockEntities.INTERFACE.get(),
                RSBlockEntities.FLUID_INTERFACE.get(),
                RSBlockEntities.WIRELESS_TRANSMITTER.get(),
                RSBlockEntities.STORAGE_MONITOR.get(),
                RSBlockEntities.CONSTRUCTOR.get(),
                RSBlockEntities.DESTRUCTOR.get(),
                RSBlockEntities.DISK_MANIPULATOR.get(),
                RSBlockEntities.PORTABLE_GRID.get(),
                RSBlockEntities.CREATIVE_PORTABLE_GRID.get(),
                RSBlockEntities.CRAFTER.get(),
                RSBlockEntities.CRAFTER_MANAGER.get(),
                RSBlockEntities.CRAFTING_MONITOR.get()
        );
    }

    @Override
    public String getName() {
        return "Block Entity Type Tags";
    }
}
