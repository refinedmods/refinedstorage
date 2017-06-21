package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.network.MessageGridItemDelta;
import com.raoulvdberge.refinedstorage.network.MessageGridItemUpdate;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class StorageCacheItemPortable implements IStorageCache<ItemStack> {
    private IPortableGrid portableGrid;
    private IStackList<ItemStack> list = API.instance().createItemStackList();
    private List<BiConsumer<ItemStack, Integer>> listeners = new LinkedList<>();

    public StorageCacheItemPortable(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void invalidate() {
        list.clear();

        if (portableGrid.getStorage() != null) {
            portableGrid.getStorage().getStacks().forEach(list::add);
        }

        portableGrid.getWatchers().forEach(this::sendUpdateTo);
    }

    @Override
    public void add(@Nonnull ItemStack stack, int size, boolean rebuilding) {
        list.add(stack, size);

        if (!rebuilding) {
            portableGrid.getWatchers().forEach(w -> RS.INSTANCE.network.sendTo(new MessageGridItemDelta(null, stack, size), (EntityPlayerMP) w));

            listeners.forEach(l -> l.accept(stack, size));
        }
    }

    @Override
    public void remove(@Nonnull ItemStack stack, int size) {
        if (list.remove(stack, size)) {
            portableGrid.getWatchers().forEach(w -> RS.INSTANCE.network.sendTo(new MessageGridItemDelta(null, stack, -size), (EntityPlayerMP) w));

            listeners.forEach(l -> l.accept(stack, -size));
        }
    }

    @Override
    public void addListener(BiConsumer<ItemStack, Integer> listener) {
        listeners.add(listener);
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
                RSUtils.writeItemStack(buf, stack, null, false);
            }
        }, false), (EntityPlayerMP) player);
    }
}
