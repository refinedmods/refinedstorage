package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.HashingStrategy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class StorageTrackerFluid implements IStorageTracker<FluidStack> {
    private static final String NBT_STACK = "Stack";
    private static final String NBT_NAME = "Name";
    private static final String NBT_TIME = "Time";

    private Map<FluidStack, IStorageTrackerEntry> changes = new TCustomHashMap<>(new HashingStrategy<FluidStack>() {
        @Override
        public int computeHashCode(FluidStack stack) {
            return API.instance().getFluidStackHashCode(stack);
        }

        @Override
        public boolean equals(FluidStack left, FluidStack right) {
            return API.instance().getComparer().isEqual(left, right, IComparer.COMPARE_NBT);
        }
    });

    private Runnable listener;

    public StorageTrackerFluid(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void changed(EntityPlayer player, FluidStack stack) {
        changes.put(stack, new StorageTrackerEntry(MinecraftServer.getCurrentTimeMillis(), player.getName()));

        listener.run();
    }

    @Override
    public IStorageTrackerEntry get(FluidStack stack) {
        return changes.get(stack);
    }

    public void readFromNbt(NBTTagList list) {
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            FluidStack stack = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(NBT_STACK));

            if (stack != null) {
                changes.put(
                    stack,
                    new StorageTrackerEntry(tag.getLong(NBT_TIME), tag.getString(NBT_NAME))
                );
            }
        }
    }

    public NBTTagList serializeNbt() {
        NBTTagList list = new NBTTagList();

        for (Map.Entry<FluidStack, IStorageTrackerEntry> entry : changes.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setLong(NBT_TIME, entry.getValue().getTime());
            tag.setString(NBT_NAME, entry.getValue().getName());
            tag.setTag(NBT_STACK, entry.getKey().writeToNBT(new NBTTagCompound()));

            list.appendTag(tag);
        }

        return list;
    }
}
