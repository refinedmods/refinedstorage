package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.StorageDiskType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StorageDiskFluid implements IStorageDisk<FluidStack> {
    private static final int PROTOCOL = 1;

    private static final String NBT_PROTOCOL = "Protocol";

    private static final String NBT_FLUIDS = "Fluids";
    private static final String NBT_STORED = "Stored";

    private NBTTagCompound tag;
    private int capacity;

    private NonNullList<FluidStack> stacks = NonNullList.create();

    private Runnable listener = () -> {
    };

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
                stacks.add(stack);
            }
        }
    }

    @Override
    public void writeToNBT() {
        NBTTagList list = new NBTTagList();

        for (FluidStack stack : stacks) {
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
    public NonNullList<FluidStack> getStacks() {
        return stacks;
    }

    @Override
    @Nullable
    public synchronized FluidStack insert(@Nonnull FluidStack stack, int size, boolean simulate) {
        for (FluidStack otherStack : stacks) {
            if (otherStack.isFluidEqual(stack)) {
                if (getCapacity() != -1 && getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        if (isVoiding()) {
                            return null;
                        }

                        return RSUtils.copyStackWithSize(stack, size);
                    }

                    if (!simulate) {
                        tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                        otherStack.amount += remainingSpace;

                        listener.run();
                    }

                    return isVoiding() ? null : RSUtils.copyStackWithSize(otherStack, size - remainingSpace);
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
                if (isVoiding()) {
                    return null;
                }

                return RSUtils.copyStackWithSize(stack, size);
            }

            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                stacks.add(RSUtils.copyStackWithSize(stack, remainingSpace));

                listener.run();
            }

            return isVoiding() ? null : RSUtils.copyStackWithSize(stack, size - remainingSpace);
        } else {
            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + size);

                stacks.add(RSUtils.copyStackWithSize(stack, size));

                listener.run();
            }

            return null;
        }
    }

    @Override
    @Nullable
    public synchronized FluidStack extract(@Nonnull FluidStack stack, int size, int flags, boolean simulate) {
        for (FluidStack otherStack : stacks) {
            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                if (size > otherStack.amount) {
                    size = otherStack.amount;
                }

                if (!simulate) {
                    if (otherStack.amount - size == 0) {
                        stacks.remove(otherStack);
                    } else {
                        otherStack.amount -= size;
                    }

                    tag.setInteger(NBT_STORED, getStored() - size);

                    listener.run();
                }

                return RSUtils.copyStackWithSize(otherStack, size);
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
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isVoiding() {
        return false;
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable FluidStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        int inserted = remainder == null ? size : (size - remainder.amount);

        if (isVoiding() && storedPreInsertion + inserted > getCapacity()) {
            inserted = getCapacity() - storedPreInsertion;
        }

        return inserted;
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_FLUIDS) && stack.getTagCompound().hasKey(NBT_STORED);
    }

    @Override
    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    public static NBTTagCompound getShareTag(NBTTagCompound tag) {
        NBTTagCompound otherTag = new NBTTagCompound();

        otherTag.setInteger(NBT_STORED, getStored(tag));
        otherTag.setTag(NBT_FLUIDS, new NBTTagList()); // To circumvent not being able to insert disks in Disk Drives (see FluidStorageNBT#isValid(ItemStack)).
        otherTag.setInteger(NBT_PROTOCOL, PROTOCOL);

        return otherTag;
    }

    public static int getStored(NBTTagCompound tag) {
        return tag.getInteger(NBT_STORED);
    }

    public static NBTTagCompound getTag() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_FLUIDS, new NBTTagList());
        tag.setInteger(NBT_STORED, 0);
        tag.setInteger(NBT_PROTOCOL, PROTOCOL);

        return tag;
    }

    public static ItemStack initDisk(ItemStack stack) {
        stack.setTagCompound(getTag());

        return stack;
    }
}
