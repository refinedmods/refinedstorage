package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class StorageTrackerFluid implements IStorageTracker<FluidStack> {
    private static final String NBT_STACK = "Stack";
    private static final String NBT_NAME = "Name";
    private static final String NBT_TIME = "Time";

    private Map<FluidStack, IStorageTrackerEntry> changes = new HashMap<>(); // TODO broken
    /*
    private Map<FluidStack, IStorageTrackerEntry> changes = new TCustomHashMap<>(new HashingStrategy<FluidStack>() {
        @Override
        public int computeHashCode(FluidStack stack) {
            return API.instance().getFluidStackHashCode(stack);
        }

        @Override
        public boolean equals(FluidStack left, FluidStack right) {
            return API.instance().getComparer().isEqual(left, right, IComparer.COMPARE_NBT);
        }
    });*/

    private Runnable listener;

    public StorageTrackerFluid(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void changed(PlayerEntity player, FluidStack stack) {
        changes.put(stack, new StorageTrackerEntry(System.currentTimeMillis(), player.getName().getString())); // TODO: correct?

        listener.run();
    }

    @Override
    public IStorageTrackerEntry get(FluidStack stack) {
        return changes.get(stack);
    }

    public void readFromNbt(ListNBT list) {
        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT tag = list.getCompound(i);

            FluidStack stack = FluidStack.loadFluidStackFromNBT(tag.getCompound(NBT_STACK));

            if (stack != null) {
                changes.put(
                    stack,
                    new StorageTrackerEntry(tag.getLong(NBT_TIME), tag.getString(NBT_NAME))
                );
            }
        }
    }

    public ListNBT serializeNbt() {
        ListNBT list = new ListNBT();

        for (Map.Entry<FluidStack, IStorageTrackerEntry> entry : changes.entrySet()) {
            CompoundNBT tag = new CompoundNBT();

            tag.putLong(NBT_TIME, entry.getValue().getTime());
            tag.putString(NBT_NAME, entry.getValue().getName());
            tag.put(NBT_STACK, entry.getKey().writeToNBT(new CompoundNBT()));

            list.add(tag);
        }

        return list;
    }
}
