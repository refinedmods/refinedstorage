package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class StorageDiskFluid implements IStorageDisk<FluidStack> {
    static final String NBT_VERSION = "Version";
    static final String NBT_CAPACITY = "Capacity";
    static final String NBT_FLUIDS = "Fluids";

    private World world;
    private int capacity;
    private Multimap<Fluid, FluidStack> stacks = ArrayListMultimap.create();

    @Nullable
    private IStorageDiskListener listener;
    private IStorageDiskContainerContext context;

    public StorageDiskFluid(World world, int capacity) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        this.world = world;
        this.capacity = capacity;
    }

    @Override
    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();

        NBTTagList list = new NBTTagList();

        for (FluidStack stack : stacks.values()) {
            list.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }

        tag.setString(NBT_VERSION, RS.VERSION);
        tag.setTag(NBT_FLUIDS, list);
        tag.setInteger(NBT_CAPACITY, capacity);

        return tag;
    }

    @Override
    public Collection<FluidStack> getStacks() {
        return stacks.values();
    }

    @Override
    @Nullable
    public FluidStack insert(@Nonnull FluidStack stack, int size, Action action) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (otherStack.isFluidEqual(stack)) {
                if (getCapacity() != -1 && getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return StackUtils.copy(stack, size);
                    }

                    if (action == Action.PERFORM) {
                        otherStack.amount += remainingSpace;

                        onChanged();
                    }

                    return StackUtils.copy(otherStack, size - remainingSpace);
                } else {
                    if (action == Action.PERFORM) {
                        otherStack.amount += size;

                        onChanged();
                    }

                    return null;
                }
            }
        }

        if (getCapacity() != -1 && getStored() + size > getCapacity()) {
            int remainingSpace = getCapacity() - getStored();

            if (remainingSpace <= 0) {
                return StackUtils.copy(stack, size);
            }

            if (action == Action.PERFORM) {
                stacks.put(stack.getFluid(), StackUtils.copy(stack, remainingSpace));

                onChanged();
            }

            return StackUtils.copy(stack, size - remainingSpace);
        } else {
            if (action == Action.PERFORM) {
                stacks.put(stack.getFluid(), StackUtils.copy(stack, size));

                onChanged();
            }

            return null;
        }
    }

    @Override
    @Nullable
    public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, Action action) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                if (size > otherStack.amount) {
                    size = otherStack.amount;
                }

                if (action == Action.PERFORM) {
                    if (otherStack.amount - size == 0) {
                        stacks.remove(otherStack.getFluid(), otherStack);
                    } else {
                        otherStack.amount -= size;
                    }

                    onChanged();
                }

                return StackUtils.copy(otherStack, size);
            }
        }

        return null;
    }

    @Override
    public int getStored() {
        return stacks.values().stream().mapToInt(s -> s.amount).sum();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public AccessType getAccessType() {
        return context.getAccessType();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable FluidStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        return remainder == null ? size : (size - remainder.amount);
    }

    @Override
    public void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public String getId() {
        return StorageDiskFactoryFluid.ID;
    }

    Multimap<Fluid, FluidStack> getRawStacks() {
        return stacks;
    }

    private void onChanged() {
        if (listener != null) {
            listener.onChanged();
        }

        API.instance().getStorageDiskManager(world).markForSaving();
    }
}
