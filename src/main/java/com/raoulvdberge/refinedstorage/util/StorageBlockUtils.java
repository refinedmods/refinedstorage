package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.ItemStorageType;
import com.raoulvdberge.refinedstorage.tile.StorageTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public class StorageBlockUtils {
    public static ResourceLocation getNetworkNodeId(ItemStorageType type) {
        switch (type) {
            case ONE_K:
                return StorageNetworkNode.ONE_K_STORAGE_BLOCK_ID;
            case FOUR_K:
                return StorageNetworkNode.FOUR_K_STORAGE_BLOCK_ID;
            case SIXTEEN_K:
                return StorageNetworkNode.SIXTEEN_K_STORAGE_BLOCK_ID;
            case SIXTY_FOUR_K:
                return StorageNetworkNode.SIXTY_FOUR_K_STORAGE_BLOCK_ID;
            case CREATIVE:
                return StorageNetworkNode.CREATIVE_STORAGE_BLOCK_ID;
            default:
                throw new IllegalArgumentException("Unknown storage type " + type);
        }
    }

    public static TileEntityType<StorageTile> getTileEntityType(ItemStorageType type) {
        switch (type) {
            case ONE_K:
                return RSTiles.ONE_K_STORAGE_BLOCK;
            case FOUR_K:
                return RSTiles.FOUR_K_STORAGE_BLOCK;
            case SIXTEEN_K:
                return RSTiles.SIXTEEN_K_STORAGE_BLOCK;
            case SIXTY_FOUR_K:
                return RSTiles.SIXTY_FOUR_K_STORAGE_BLOCK;
            case CREATIVE:
                return RSTiles.CREATIVE_STORAGE_BLOCK;
            default:
                throw new IllegalArgumentException("Unknown storage type " + type);
        }
    }
}
