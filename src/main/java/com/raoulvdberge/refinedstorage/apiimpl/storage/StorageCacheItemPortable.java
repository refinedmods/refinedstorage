package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.network.MessageGridItemDelta;
import com.raoulvdberge.refinedstorage.network.MessageGridItemUpdate;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StorageCacheItemPortable implements IStorageCache<ItemStack> {
    private IPortableGrid portableGrid;
    private IStackList<ItemStack> list = API.instance().createItemStackList();
    private List<Runnable> listeners = new LinkedList<>();

    public StorageCacheItemPortable(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void invalidate() {
        list.clear();

        if (portableGrid.getStorage() != null) {
            portableGrid.getStorage().getStacks().forEach(list::add);
        }

        listeners.forEach(Runnable::run);

        portableGrid.getWatchers().forEach(this::sendUpdateTo);
    }

    @Override
    public void add(@Nonnull ItemStack stack, int size, boolean rebuilding, boolean batched) {
        list.add(stack, size);

        if (!rebuilding) {
            portableGrid.getWatchers().forEach(w -> RS.INSTANCE.network.sendTo(new MessageGridItemDelta(null, portableGrid.getStorageTracker(), stack, size), (EntityPlayerMP) w));

            listeners.forEach(Runnable::run);
        }
    }

    @Override
    public void remove(@Nonnull ItemStack stack, int size, boolean batched) {
        if (list.remove(stack, size)) {
            portableGrid.getWatchers().forEach(w -> RS.INSTANCE.network.sendTo(new MessageGridItemDelta(null, portableGrid.getStorageTracker(), stack, -size), (EntityPlayerMP) w));

            listeners.forEach(Runnable::run);
        }
    }

    @Override
    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    @Override
    public void sort() {
        // NO OP
    }

    @Override
    public IStackList<ItemStack> getList() {
        return list;
    }

    @Override
    public List<IStorage<ItemStack>> getStorages() {
        return Collections.emptyList();
    }

    public void sendUpdateTo(EntityPlayer player) {
        RS.INSTANCE.network.sendTo(new MessageGridItemUpdate(buf -> {
            buf.writeInt(list.getStacks().size());

            for (ItemStack stack : list.getStacks()) {
                StackUtils.writeItemStack(buf, stack, null, false);

                IStorageTracker.IStorageTrackerEntry entry = portableGrid.getStorageTracker().get(stack);
                buf.writeBoolean(entry != null);
                if (entry != null) {
                    buf.writeLong(entry.getTime());
                    ByteBufUtils.writeUTF8String(buf, entry.getName());
                }
            }
        }, false), (EntityPlayerMP) player);
    }
}
