package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.StackListResult;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class StorageCacheListenerGridPortableFluid implements IStorageCacheListener<FluidStack> {
    private IPortableGrid portableGrid;
    private ServerPlayerEntity player;

    public StorageCacheListenerGridPortableFluid(IPortableGrid portableGrid, ServerPlayerEntity player) {
        this.portableGrid = portableGrid;
        this.player = player;
    }

    @Override
    public void onAttached() {
        /*RS.INSTANCE.network.sendTo(new MessageGridFluidUpdate(buf -> {
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
        }, false), player); TODO */
    }

    @Override
    public void onInvalidated() {
        // NO OP
    }

    @Override
    public void onChanged(StackListResult<FluidStack> delta) {
        // TODO RS.INSTANCE.network.sendTo(new MessageGridFluidDelta(null, portableGrid.getFluidStorageTracker(), stack, size), player);

    }

    @Override
    public void onChangedBulk(List<StackListResult<FluidStack>> storageCacheDeltas) {
        /* TODO
        for (Pair<FluidStack, Integer> stack : stacks) {
            onChanged(stack.getLeft(), stack.getRight());
        }

         */
    }
}
