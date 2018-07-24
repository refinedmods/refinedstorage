package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeExternalStorage;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerExternalStorage extends ContainerBase {
    private NetworkNodeExternalStorage externalStorage;

    public ContainerExternalStorage(TileExternalStorage externalStorage, EntityPlayer player) {
        super(externalStorage, player);

        this.externalStorage = externalStorage.getNode();

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(externalStorage.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> externalStorage.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterFluid(externalStorage.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> externalStorage.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 141);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = getSlot(index);

        if (slot.getHasStack() && index > 8) {
            if (externalStorage.getType() == IType.ITEMS) {
                return transferToFilters(slot.getStack(), 0, 9);
            } else {
                return transferToFluidFilters(slot.getStack());
            }
        }

        return ItemStack.EMPTY;
    }
}
