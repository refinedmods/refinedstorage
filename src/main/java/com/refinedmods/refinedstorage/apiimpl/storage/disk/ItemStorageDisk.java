package com.refinedmods.refinedstorage.apiimpl.storage.disk;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.ItemStorageDiskFactory;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class ItemStorageDisk implements IStorageDisk<ItemStack> {
    public static final String NBT_VERSION = "Version";
    public static final String NBT_CAPACITY = "Capacity";
    public static final String NBT_ITEMS = "Items";
    public static final String NBT_OWNER = "Owner";
    public static final int VERSION = 1;

    @Nullable
    private final ServerWorld world;
    private final int capacity;
    private final Multimap<Item, ItemStack> stacks = ArrayListMultimap.create();
    private final UUID owner;
    private int itemCount;

    @Nullable
    private IStorageDiskListener listener;
    private IStorageDiskContainerContext context;

    public ItemStorageDisk(@Nullable ServerWorld world, int capacity, @Nullable UUID owner) {
        this.world = world;
        this.capacity = capacity;
        this.owner = owner;
    }

    @Override
    public CompoundNBT writeToNbt() {
        CompoundNBT tag = new CompoundNBT();

        ListNBT list = new ListNBT();

        for (ItemStack stack : stacks.values()) {
            list.add(StackUtils.serializeStackToNbt(stack));
        }

        tag.putInt(NBT_VERSION, VERSION);
        tag.put(NBT_ITEMS, list);
        tag.putInt(NBT_CAPACITY, capacity);

        if (owner != null) {
            tag.putUUID(NBT_OWNER, owner);
        }

        return tag;
    }

    @Override
    public ResourceLocation getFactoryId() {
        return ItemStorageDiskFactory.ID;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return stacks.values();
    }

    @Override
    @Nonnull
    public ItemStack insert(@Nonnull ItemStack stack, int size, Action action) {
        if (stack.isEmpty() || itemCount == capacity) {
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }

        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                if (getCapacity() != -1 && getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return ItemHandlerHelper.copyStackWithSize(stack, size);
                    }

                    if (action == Action.PERFORM) {
                        otherStack.grow(remainingSpace);
                        itemCount += remainingSpace;
                        onChanged();
                    }

                    return ItemHandlerHelper.copyStackWithSize(otherStack, size - remainingSpace);
                } else {
                    if (action == Action.PERFORM) {
                        otherStack.grow(size);
                        itemCount += size;

                        onChanged();
                    }

                    return ItemStack.EMPTY;
                }
            }
        }

        if (getCapacity() != -1 && getStored() + size > getCapacity()) {
            int remainingSpace = getCapacity() - getStored();

            if (remainingSpace <= 0) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            if (action == Action.PERFORM) {
                stacks.put(stack.getItem(), ItemHandlerHelper.copyStackWithSize(stack, remainingSpace));
                itemCount += remainingSpace;
                onChanged();
            }

            return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
        } else {
            if (action == Action.PERFORM) {
                stacks.put(stack.getItem(), ItemHandlerHelper.copyStackWithSize(stack, size));
                itemCount += size;

                onChanged();
            }

            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, Action action) {
        if (stack.isEmpty()) {
            return stack;
        }

        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                if (size > otherStack.getCount()) {
                    size = otherStack.getCount();
                }

                if (action == Action.PERFORM) {
                    if (otherStack.getCount() - size == 0) {
                        stacks.remove(otherStack.getItem(), otherStack);
                    } else {
                        otherStack.shrink(size);
                    }

                    itemCount -= size;

                    onChanged();
                }

                return ItemHandlerHelper.copyStackWithSize(otherStack, size);
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getStored() {
        return itemCount;
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
    public void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        return remainder == null ? size : (size - remainder.getCount());
    }

    public Multimap<Item, ItemStack> getRawStacks() {
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

    public void updateItemCount() {
        itemCount = stacks.values().stream().mapToInt(ItemStack::getCount).sum();
    }
}
