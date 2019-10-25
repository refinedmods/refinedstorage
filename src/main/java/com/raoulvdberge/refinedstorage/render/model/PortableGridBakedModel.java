package com.raoulvdberge.refinedstorage.render.model;

import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PortableGridBakedModel extends DelegateBakedModel {
    private final IBakedModel baseConnected;
    private final IBakedModel baseDisconnected;
    private final IBakedModel disk;
    private final IBakedModel diskNearCapacity;
    private final IBakedModel diskFull;
    private final IBakedModel diskDisconnected;

    private final CustomItemOverrideList itemOverrideList = new CustomItemOverrideList();

    public PortableGridBakedModel(IBakedModel baseConnected,
                                  IBakedModel baseDisconnected,
                                  IBakedModel disk,
                                  IBakedModel diskNearCapacity,
                                  IBakedModel diskFull,
                                  IBakedModel diskDisconnected) {
        super(baseConnected);

        this.baseConnected = baseConnected;
        this.baseDisconnected = baseDisconnected;
        this.disk = disk;
        this.diskNearCapacity = diskNearCapacity;
        this.diskFull = diskFull;
        this.diskDisconnected = diskDisconnected;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverrideList;
    }

    private class CustomItemOverrideList extends ItemOverrideList {
        @Nullable
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
            PortableGrid portableGrid = new PortableGrid(null, stack);

            IBakedModel myDisk = null;

            switch (portableGrid.getDiskState()) {
                case NORMAL:
                    myDisk = disk;
                    break;
                case NEAR_CAPACITY:
                    myDisk = diskNearCapacity;
                    break;
                case FULL:
                    myDisk = diskFull;
                    break;
                case DISCONNECTED:
                    myDisk = diskDisconnected;
                    break;
                case NONE:
                    break;
            }

            if (portableGrid.isActive()) {
                return new PortableGridItemBakedModel(baseConnected, myDisk);
            } else {
                return new PortableGridItemBakedModel(baseDisconnected, myDisk);
            }
        }
    }
}
