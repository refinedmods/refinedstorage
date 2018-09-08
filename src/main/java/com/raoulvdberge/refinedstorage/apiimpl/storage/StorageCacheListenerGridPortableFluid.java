package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.network.MessageGridFluidDelta;
import com.raoulvdberge.refinedstorage.network.MessageGridFluidUpdate;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class StorageCacheListenerGridPortableFluid implements IStorageCacheListener<FluidStack> {
    private IPortableGrid portableGrid;
    private EntityPlayerMP player;

    public StorageCacheListenerGridPortableFluid(IPortableGrid portableGrid, EntityPlayerMP player) {
        this.portableGrid = portableGrid;
        this.player = player;
    }

    @Override
    public void onAttached() {
        RS.INSTANCE.network.sendTo(new MessageGridFluidUpdate(buf -> {
            int size = portableGrid.getFluidCache().getList().getStacks().size();

            buf.writeInt(size);

            for (FluidStack stack : portableGrid.getFluidCache().getList().getStacks()) {
                StackUtils.writeFluidStackAndHash(buf, stack);

                IStorageTracker.IStorageTrackerEntry entry = portableGrid.getFluidStorageTracker().get(stack);
                buf.writeBoolean(entry != null);
                if (entry != null) {
                    buf.writeLong(entry.getTime());
                    ByteBufUtils.writeUTF8String(buf, entry.getName());
                }

                buf.writeBoolean(false);
                buf.writeBoolean(false);
            }
        }, false), player);
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(@Nonnull FluidStack stack, int size) {
        RS.INSTANCE.network.sendTo(new MessageGridFluidDelta(null, portableGrid.getFluidStorageTracker(), stack, size), player);
    }

    @Override
    public void onChangedBulk(@Nonnull List<Pair<FluidStack, Integer>> stacks) {
        for(Pair<FluidStack, Integer> stack : stacks) {
            onChanged(stack.getLeft(), stack.getRight());
        }
    }
}
