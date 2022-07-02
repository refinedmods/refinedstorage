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
                RSBlockEntities.CONTROLLER,
                RSBlockEntities.CREATIVE_CONTROLLER,
                RSBlockEntities.DETECTOR,
                RSBlockEntities.DISK_DRIVE,
                RSBlockEntities.EXPORTER,
                RSBlockEntities.EXTERNAL_STORAGE,
                RSBlockEntities.GRID,
                RSBlockEntities.CRAFTING_GRID,
                RSBlockEntities.PATTERN_GRID,
                RSBlockEntities.FLUID_GRID,
                RSBlockEntities.IMPORTER,
                RSBlockEntities.NETWORK_TRANSMITTER,
                RSBlockEntities.NETWORK_RECEIVER,
                RSBlockEntities.RELAY,
                RSBlockEntities.CABLE,
                RSBlockEntities.ONE_K_STORAGE_BLOCK,
                RSBlockEntities.FOUR_K_STORAGE_BLOCK,
                RSBlockEntities.SIXTEEN_K_STORAGE_BLOCK,
                RSBlockEntities.SIXTY_FOUR_K_STORAGE_BLOCK,
                RSBlockEntities.CREATIVE_STORAGE_BLOCK,
                RSBlockEntities.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK,
                RSBlockEntities.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK,
                RSBlockEntities.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK,
                RSBlockEntities.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK,
                RSBlockEntities.CREATIVE_FLUID_STORAGE_BLOCK,
                RSBlockEntities.SECURITY_MANAGER,
                RSBlockEntities.INTERFACE,
                RSBlockEntities.FLUID_INTERFACE,
                RSBlockEntities.WIRELESS_TRANSMITTER,
                RSBlockEntities.STORAGE_MONITOR,
                RSBlockEntities.CONSTRUCTOR,
                RSBlockEntities.DESTRUCTOR,
                RSBlockEntities.DISK_MANIPULATOR,
                RSBlockEntities.PORTABLE_GRID,
                RSBlockEntities.CREATIVE_PORTABLE_GRID,
                RSBlockEntities.CRAFTER,
                RSBlockEntities.CRAFTER_MANAGER,
                RSBlockEntities.CRAFTING_MONITOR
        );
    }

    @Override
    public String getName() {
        return "Block Entity Type Tags";
    }
}
