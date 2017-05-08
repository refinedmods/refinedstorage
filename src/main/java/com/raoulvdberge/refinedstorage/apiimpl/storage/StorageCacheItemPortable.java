package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.network.MessageGridItemDelta;
import com.raoulvdberge.refinedstorage.network.MessageGridItemUpdate;
import com.raoulvdberge.refinedstorage.tile.grid.PortableGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class StorageCacheItemPortable implements IStorageCache<ItemStack> {
    private PortableGrid portableGrid;
    private IStackList<ItemStack> list = API.instance().createItemStackList();

    public StorageCacheItemPortable(PortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void invalidate() {
        list.clear();

        if (portableGrid.getStorage() != null) {
            portableGrid.getStorage().getStacks().forEach(list::add);
        }

        RS.INSTANCE.network.sendTo(new MessageGridItemUpdate((buf) -> {
            buf.writeInt(list.getStacks().size());

            for (ItemStack stack : list.getStacks()) {
                RSUtils.writeItemStack(buf, stack, null, false);

                buf.writeInt(API.instance().getItemStackHashCode(stack));
                buf.writeBoolean(false);
                buf.writeBoolean(false);
            }
        }, false), (EntityPlayerMP) portableGrid.getPlayer());
    }

    @Override
    public void add(@Nonnull ItemStack stack, int size, boolean rebuilding) {
        list.add(stack, size);

        if (!rebuilding) {
            RS.INSTANCE.network.sendTo(new MessageGridItemDelta(getSendHandler(stack), size), (EntityPlayerMP) portableGrid.getPlayer());
        }
    }

    @Override
    public void remove(@Nonnull ItemStack stack, int size) {
        if (list.remove(stack, size)) {
            RS.INSTANCE.network.sendTo(new MessageGridItemDelta(getSendHandler(stack), -size), (EntityPlayerMP) portableGrid.getPlayer());
        }
    }

    private Consumer<ByteBuf> getSendHandler(@Nonnull ItemStack stack) {
        return buf -> {
            RSUtils.writeItemStack(buf, stack, null, false);

            buf.writeInt(API.instance().getItemStackHashCode(stack));
            buf.writeBoolean(false);
            buf.writeBoolean(false);
        };
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
}
