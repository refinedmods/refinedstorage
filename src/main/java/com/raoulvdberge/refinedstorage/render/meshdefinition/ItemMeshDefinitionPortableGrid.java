package com.raoulvdberge.refinedstorage.render.meshdefinition;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskSyncData;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.item.itemblock.ItemBlockPortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.TilePortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.UUID;

public class ItemMeshDefinitionPortableGrid implements ItemMeshDefinition {
    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        ItemHandlerBase disk = new ItemHandlerBase(1);

        if (stack.hasTagCompound()) {
            StackUtils.readItems(disk, 4, stack.getTagCompound());
        }

        UUID diskId = disk.getStackInSlot(0).isEmpty() ? null : ((IStorageDiskProvider) disk.getStackInSlot(0).getItem()).getId(disk.getStackInSlot(0));

        IPortableGrid.IPortableGridRenderInfo renderInfo = new IPortableGrid.IPortableGridRenderInfo() {
            @Override
            public int getStored() {
                if (diskId == null) {
                    return 0;
                }

                API.instance().getStorageDiskSync().sendRequest(diskId);

                IStorageDiskSyncData data = API.instance().getStorageDiskSync().getData(diskId);

                return data == null ? 0 : data.getStored();
            }

            @Override
            public int getCapacity() {
                if (diskId == null) {
                    return 0;
                }

                API.instance().getStorageDiskSync().sendRequest(diskId);

                IStorageDiskSyncData data = API.instance().getStorageDiskSync().getData(diskId);

                return data == null ? 0 : data.getCapacity();
            }

            @Override
            public boolean hasStorage() {
                return diskId != null;
            }

            @Override
            public boolean isActive() {
                return (stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() > 0 || stack.getMetadata() == ItemBlockPortableGrid.TYPE_CREATIVE) && hasStorage();
            }
        };

        return new ModelResourceLocation(RS.ID + ":portable_grid", "connected=" + Boolean.toString(renderInfo.isActive()) + ",direction=north,disk_state=" + TilePortableGrid.getDiskState(renderInfo));
    }
}
