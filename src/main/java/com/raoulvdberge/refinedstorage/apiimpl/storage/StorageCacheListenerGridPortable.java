package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.network.MessageGridItemDelta;
import com.raoulvdberge.refinedstorage.network.MessageGridItemUpdate;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class StorageCacheListenerGridPortable implements IStorageCacheListener<ItemStack> {
    private IPortableGrid portableGrid;
    private EntityPlayerMP player;

    public StorageCacheListenerGridPortable(IPortableGrid portableGrid, EntityPlayerMP player) {
        this.portableGrid = portableGrid;
        this.player = player;
    }

    @Override
    public void onAttached() {
        RS.INSTANCE.network.sendTo(new MessageGridItemUpdate(buf -> {
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
        }, false), player);
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(@Nonnull ItemStack stack, int size) {
        RS.INSTANCE.network.sendTo(new MessageGridItemDelta(null, portableGrid.getItemStorageTracker(), stack, size), player);
    }

    @Override
    public void onChangedBulk(@Nonnull List<Pair<ItemStack, Integer>> stacks)
    {
        RS.INSTANCE.network.sendTo(new MessageGridItemDelta(null, portableGrid.getItemStorageTracker(), stacks), player);
    }
}
