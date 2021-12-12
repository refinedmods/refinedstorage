package com.refinedmods.refinedstorage.apiimpl.storage.disk;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.FluidStorageDiskFactory;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class FluidStorageDisk implements IStorageDisk<FluidStack> {
    public static final String NBT_VERSION = "Version";
    public static final String NBT_CAPACITY = "Capacity";
    public static final String NBT_FLUIDS = "Fluids";
    public static final String NBT_OWNER = "Owner";
    public static final int VERSION = 1;

    @Nullable
    private final ServerLevel world;
    private final int capacity;
    private final Multimap<Fluid, FluidStack> stacks = ArrayListMultimap.create();
    private final UUID owner;

    @Nullable
    private IStorageDiskListener listener;
    private IStorageDiskContainerContext context;

    public FluidStorageDisk(@Nullable ServerLevel world, int capacity, @Nullable UUID owner) {
        this.world = world;
        this.capacity = capacity;
        this.owner = owner;
    }

    @Override
    public CompoundTag writeToNbt() {
        CompoundTag tag = new CompoundTag();

        ListTag list = new ListTag();

        for (FluidStack stack : stacks.values()) {
            list.add(stack.writeToNBT(new CompoundTag()));
        }

        tag.putInt(NBT_VERSION, VERSION);
        tag.put(NBT_FLUIDS, list);
        tag.putInt(NBT_CAPACITY, capacity);

        if (owner != null) {
            tag.putUUID(NBT_OWNER, owner);
        }

        return tag;
    }

    @Override
    public Collection<FluidStack> getStacks() {
        return stacks.values();
    }

    @Override
    @Nonnull
    public FluidStack insert(@Nonnull FluidStack stack, int size, Action action) {
        if (stack.isEmpty()) {
            return stack;
        }

        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (otherStack.isFluidEqual(stack)) {
                if (getCapacity() != -1 && getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return StackUtils.copy(stack, size);
                    }

                    if (action == Action.PERFORM) {
                        otherStack.grow(remainingSpace);

                        onChanged();
                    }

                    return StackUtils.copy(otherStack, size - remainingSpace);
                } else {
                    if (action == Action.PERFORM) {
                        otherStack.grow(size);

                        onChanged();
                    }

                    return FluidStack.EMPTY;
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

            return FluidStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, Action action) {
        if (stack.isEmpty()) {
            return stack;
        }

        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                if (size > otherStack.getAmount()) {
                    size = otherStack.getAmount();
                }

                if (action == Action.PERFORM) {
                    if (otherStack.getAmount() - size == 0) {
                        stacks.remove(otherStack.getFluid(), otherStack);
                    } else {
                        otherStack.shrink(size);
                    }

                    onChanged();
                }

                return StackUtils.copy(otherStack, size);
            }
        }

        return FluidStack.EMPTY;
    }

    @Override
    public int getStored() {
        return stacks.values().stream().mapToInt(FluidStack::getAmount).sum();
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

    @Nullable
    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable FluidStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        return remainder == null ? size : (size - remainder.getAmount());
    }

    @Override
    public void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ResourceLocation getFactoryId() {
        return FluidStorageDiskFactory.ID;
    }

    public Multimap<Fluid, FluidStack> getRawStacks() {
        return stacks;
    }

    private void onChanged() {
        if (listener != null) {
            listener.onChanged();
        }

        if (world != null) {
            API.instance().getStorageDiskManager(world).markForSaving();
        }
    }
}
