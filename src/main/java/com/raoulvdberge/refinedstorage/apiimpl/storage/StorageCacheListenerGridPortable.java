package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.StackListResult;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class StorageCacheListenerGridPortable implements IStorageCacheListener<ItemStack> {
    private IPortableGrid portableGrid;
    private ServerPlayerEntity player;

    public StorageCacheListenerGridPortable(IPortableGrid portableGrid, ServerPlayerEntity player) {
        this.portableGrid = portableGrid;
        this.player = player;
    }

    @Override
    public void onAttached() {
        /*RS.INSTANCE.network.sendTo(new MessageGridItemUpdate(buf -> {
            buf.writeInt(portableGrid.getItemCache().getList().getStacks().size());

            for (ItemStack stack : portableGrid.getItemCache().getList().getStacks()) {
                StackUtils.writeItemStack(buf, stack, null, false);

                IStorageTracker.IStorageTrackerEntry entry = portableGrid.getItemStorageTracker().get(stack);
                buf.writeBoolean(entry != null);
                if (entry != null) {
                    buf.writeLong(entry.getTime());
                    ByteBufUtils.writeUTF8String(buf, entry.getName());
                }
            }
        }, false), player); TODO */
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(StackListResult<ItemStack> delta) {
        // TODO RS.INSTANCE.network.sendTo(new MessageGridItemDelta(null, portableGrid.getItemStorageTracker(), stack, size), player);
    }

    @Override
    public void onChangedBulk(List<StackListResult<ItemStack>> storageCacheDeltas) {
        // TODO RS.INSTANCE.network.sendTo(new MessageGridItemDelta(null, portableGrid.getItemStorageTracker(), stacks), player);
    }
}
