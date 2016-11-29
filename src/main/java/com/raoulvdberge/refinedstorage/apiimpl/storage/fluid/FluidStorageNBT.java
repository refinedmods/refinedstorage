package com.raoulvdberge.refinedstorage.apiimpl.storage.fluid;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.fluid.IFluidStorage;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * A implementation of {@link IFluidStorage} that stores storage fluids in NBT.
 */
public abstract class FluidStorageNBT implements IFluidStorage {
    /**
     * The current save protocol that is used. It's set to every {@link FluidStorageNBT} to allow for
     * safe backwards compatibility breaks.
     */
    private static final int PROTOCOL = 1;

    private static final String NBT_PROTOCOL = "Protocol";

    private static final String NBT_FLUIDS = "Fluids";
    private static final String NBT_STORED = "Stored";

    private NBTTagCompound tag;
    private int capacity;
    private TileEntity tile;

    private NonNullList<FluidStack> stacks = NonNullList.create();

    /**
     * @param tag      The NBT tag we are reading from and writing the amount stored to, has to be initialized with {@link FluidStorageNBT#createNBT()} if it doesn't exist yet
     * @param capacity The capacity of this storage, -1 for infinite capacity
     * @param tile     A {@link TileEntity} that the NBT storage is in, will be marked dirty when the storage changes
     */
    public FluidStorageNBT(NBTTagCompound tag, int capacity, @Nullable TileEntity tile) {
        this.tag = tag;
        this.capacity = capacity;
        this.tile = tile;

        readFromNBT();
    }

    private void readFromNBT() {
        NBTTagList list = (NBTTagList) tag.getTag(NBT_FLUIDS);

        for (int i = 0; i < list.tagCount(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompoundTagAt(i));

            if (stack != null) {
                stacks.add(stack);
            }
        }
    }

    /**
     * Writes the items to the NBT tag.
     */
    public void writeToNBT() {
        NBTTagList list = new NBTTagList();

        for (FluidStack stack : stacks) {
            list.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_FLUIDS, list);
        tag.setInteger(NBT_PROTOCOL, PROTOCOL);
    }

    @Override
    public NonNullList<FluidStack> getStacks() {
        return stacks;
    }

    @Override
    public synchronized FluidStack insertFluid(FluidStack stack, int size, boolean simulate) {
        for (FluidStack otherStack : stacks) {
            if (otherStack.isFluidEqual(stack)) {
                if (getCapacity() != -1 && getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return RSUtils.copyStackWithSize(stack, size);
                    }

                    if (!simulate) {
                        tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                        otherStack.amount += remainingSpace;

                        onStorageChanged();
                    }

                    return RSUtils.copyStackWithSize(otherStack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        tag.setInteger(NBT_STORED, getStored() + size);

                        otherStack.amount += size;

                        onStorageChanged();
                    }

                    return null;
                }
            }
        }

        if (getCapacity() != -1 && getStored() + size > getCapacity()) {
            int remainingSpace = getCapacity() - getStored();

            if (remainingSpace <= 0) {
                return RSUtils.copyStackWithSize(stack, size);
            }

            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                stacks.add(RSUtils.copyStackWithSize(stack, remainingSpace));

                onStorageChanged();
            }

            return RSUtils.copyStackWithSize(stack, size - remainingSpace);
        } else {
            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + size);

                stacks.add(RSUtils.copyStackWithSize(stack, size));

                onStorageChanged();
            }

            return null;
        }
    }

    @Override
    public synchronized FluidStack extractFluid(FluidStack stack, int size, int flags, boolean simulate) {
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

                    onStorageChanged();
                }

                return RSUtils.copyStackWithSize(otherStack, size);
            }
        }

        return null;
    }

    public void onStorageChanged() {
        if (tile != null) {
            tile.markDirty();
        }
    }

    @Override
    public int getStored() {
        return getStoredFromNBT(tag);
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return getStored() == getCapacity();
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public static int getStoredFromNBT(NBTTagCompound tag) {
        return tag.getInteger(NBT_STORED);
    }

    public static NBTTagCompound getNBTShareTag(NBTTagCompound tag) {
        NBTTagCompound otherTag = new NBTTagCompound();

        otherTag.setInteger(NBT_STORED, getStoredFromNBT(tag));
        otherTag.setTag(NBT_FLUIDS, new NBTTagList()); // To circumvent not being able to insert disks in Disk Drives (see FluidStorageNBT#isValid(ItemStack)).

        return otherTag;
    }

    /*
     * @return A NBT tag initialized with the fields that {@link NBTStorage} uses
     */
    public static NBTTagCompound createNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_FLUIDS, new NBTTagList());
        tag.setInteger(NBT_STORED, 0);
        tag.setInteger(NBT_PROTOCOL, PROTOCOL);

        return tag;
    }

    public static boolean isValid(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_FLUIDS) && stack.getTagCompound().hasKey(NBT_STORED);
    }

    /**
     * @param stack The {@link ItemStack} to populate with the NBT tags from {@link FluidStorageNBT#createNBT()}
     * @return The provided {@link ItemStack} with NBT tags from {@link FluidStorageNBT#createNBT()}
     */
    public static ItemStack createStackWithNBT(ItemStack stack) {
        stack.setTagCompound(createNBT());

        return stack;
    }
}
