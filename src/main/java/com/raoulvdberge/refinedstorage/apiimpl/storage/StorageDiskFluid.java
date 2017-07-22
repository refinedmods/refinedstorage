package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.StorageDiskType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.StackUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Supplier;

public class StorageDiskFluid implements IStorageDisk<FluidStack> {
    private static final int PROTOCOL = 1;

    private static final String NBT_PROTOCOL = "Protocol";

    private static final String NBT_FLUIDS = "Fluids";
    private static final String NBT_STORED = "Stored";

    private NBTTagCompound tag;
    private int capacity;

    private Multimap<Fluid, FluidStack> stacks = ArrayListMultimap.create();

    private Runnable listener = () -> {
    };
    private Supplier<Boolean> voidExcess;
    private Supplier<AccessType> accessType;

    public StorageDiskFluid(NBTTagCompound tag, int capacity) {
        this.tag = tag;
        this.capacity = capacity;
    }

    public NBTTagCompound getStorageTag() {
        return tag;
    }

    @Override
    public void readFromNBT() {
        NBTTagList list = (NBTTagList) tag.getTag(NBT_FLUIDS);

        for (int i = 0; i < list.tagCount(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompoundTagAt(i));

            if (stack != null) {
                stacks.put(stack.getFluid(), stack);
            }
        }
    }

    @Override
    public void writeToNBT() {
        NBTTagList list = new NBTTagList();

        for (FluidStack stack : stacks.values()) {
            list.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_FLUIDS, list);
        tag.setInteger(NBT_PROTOCOL, PROTOCOL);
    }

    @Override
    public StorageDiskType getType() {
        return StorageDiskType.FLUIDS;
    }

    @Override
    public Collection<FluidStack> getStacks() {
        return stacks.values();
    }

    @Override
    @Nullable
    public synchronized FluidStack insert(@Nonnull FluidStack stack, int size, boolean simulate) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (otherStack.isFluidEqual(stack)) {
                if (getCapacity() != -1 && getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        if (voidExcess.get()) {
                            return null;
                        }

                        return StackUtils.copy(stack, size);
                    }

                    if (!simulate) {
                        tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                        otherStack.amount += remainingSpace;

                        listener.run();
                    }

                    return voidExcess.get() ? null : StackUtils.copy(otherStack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        tag.setInteger(NBT_STORED, getStored() + size);

                        otherStack.amount += size;

                        listener.run();
                    }

                    return null;
                }
            }
        }

        if (getCapacity() != -1 && getStored() + size > getCapacity()) {
            int remainingSpace = getCapacity() - getStored();

            if (remainingSpace <= 0) {
                if (voidExcess.get()) {
                    return null;
                }

                return StackUtils.copy(stack, size);
            }

            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                stacks.put(stack.getFluid(), StackUtils.copy(stack, remainingSpace));

                listener.run();
            }

            return voidExcess.get() ? null : StackUtils.copy(stack, size - remainingSpace);
        } else {
            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + size);

                stacks.put(stack.getFluid(), StackUtils.copy(stack, size));

                listener.run();
            }

            return null;
        }
    }

    @Override
    @Nullable
    public synchronized FluidStack extract(@Nonnull FluidStack stack, int size, int flags, boolean simulate) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                if (size > otherStack.amount) {
                    size = otherStack.amount;
                }

                if (!simulate) {
                    if (otherStack.amount - size == 0) {
                        stacks.remove(otherStack.getFluid(), otherStack);
                    } else {
                        otherStack.amount -= size;
                    }

                    tag.setInteger(NBT_STORED, getStored() - size);

                    listener.run();
                }

                return StackUtils.copy(otherStack, size);
            }
        }

        return null;
    }

    @Override
    public int getStored() {
        return getStored(tag);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public AccessType getAccessType() {
        return accessType.get();
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

        int inserted = remainder == null ? size : (size - remainder.amount);

        if (voidExcess.get() && storedPreInsertion + inserted > getCapacity()) {
            inserted = getCapacity() - storedPreInsertion;
        }

        return inserted;
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_FLUIDS) && stack.getTagCompound().hasKey(NBT_STORED);
    }

    @Override
    public void onPassContainerContext(Runnable listener, Supplier<Boolean> voidExcess, Supplier<AccessType> accessType) {
        this.listener = listener;
        this.voidExcess = voidExcess;
        this.accessType = accessType;
    }

    public static NBTTagCompound getShareTag(@Nullable NBTTagCompound tag) {
        NBTTagCompound otherTag = new NBTTagCompound();

        otherTag.setInteger(NBT_STORED, getStored(tag));
        otherTag.setTag(NBT_FLUIDS, new NBTTagList());
        otherTag.setInteger(NBT_PROTOCOL, PROTOCOL);

        return otherTag;
    }

    public static int getStored(@Nullable NBTTagCompound tag) {
        return tag == null ? 0 : tag.getInteger(NBT_STORED);
    }

    public static NBTTagCompound getTag() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_FLUIDS, new NBTTagList());
        tag.setInteger(NBT_STORED, 0);
        tag.setInteger(NBT_PROTOCOL, PROTOCOL);

        return tag;
    }
}
