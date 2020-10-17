package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.tile.FluidStorageTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public class FluidStorageBlockUtils {
    private FluidStorageBlockUtils() {
    }

    public static ResourceLocation getNetworkNodeId(FluidStorageType type) {
        switch (type) {
            case SIXTY_FOUR_K:
                return FluidStorageNetworkNode.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK_ID;
            case TWO_HUNDRED_FIFTY_SIX_K:
                return FluidStorageNetworkNode.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK_ID;
            case THOUSAND_TWENTY_FOUR_K:
                return FluidStorageNetworkNode.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK_ID;
            case FOUR_THOUSAND_NINETY_SIX_K:
                return FluidStorageNetworkNode.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK_ID;
            case CREATIVE:
                return FluidStorageNetworkNode.CREATIVE_FLUID_STORAGE_BLOCK_ID;
            default:
                throw new IllegalArgumentException("Unknown storage type " + type);
        }
    }

    public static TileEntityType<FluidStorageTile> getTileEntityType(FluidStorageType type) {
        switch (type) {
            case SIXTY_FOUR_K:
                return RSTiles.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK;
            case TWO_HUNDRED_FIFTY_SIX_K:
                return RSTiles.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK;
            case THOUSAND_TWENTY_FOUR_K:
                return RSTiles.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK;
            case FOUR_THOUSAND_NINETY_SIX_K:
                return RSTiles.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK;
            case CREATIVE:
                return RSTiles.CREATIVE_FLUID_STORAGE_BLOCK;
            default:
                throw new IllegalArgumentException("Unknown storage type " + type);
        }
    }
}
