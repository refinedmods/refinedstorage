package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskListener;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class StorageDiskItem implements IStorageDisk<ItemStack> {
    static final String NBT_VERSION = "Version";
    static final String NBT_CAPACITY = "Capacity";
    static final String NBT_ITEMS = "Items";

    static final String NBT_ITEM_TYPE = "Type";
    static final String NBT_ITEM_QUANTITY = "Quantity";
    static final String NBT_ITEM_DAMAGE = "Damage";
    static final String NBT_ITEM_NBT = "NBT";
    static final String NBT_ITEM_CAPS = "Caps";

    private World world;
    private int capacity;
    private Multimap<Item, ItemStack> stacks = ArrayListMultimap.create();

    @Nullable
    private IStorageDiskListener listener;
    private IStorageDiskContainerContext context;

    public StorageDiskItem(World world, int capacity) {
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

        // Dummy value for extracting ForgeCaps
        NBTTagCompound dummy = new NBTTagCompound();

        for (ItemStack stack : stacks.values()) {
            NBTTagCompound itemTag = new NBTTagCompound();

            itemTag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(stack.getItem()));
            itemTag.setInteger(NBT_ITEM_QUANTITY, stack.getCount());
            itemTag.setInteger(NBT_ITEM_DAMAGE, stack.getItemDamage());

            if (stack.hasTagCompound()) {
                itemTag.setTag(NBT_ITEM_NBT, stack.getTagCompound());
            }

            stack.writeToNBT(dummy);

            if (dummy.hasKey("ForgeCaps")) {
                itemTag.setTag(NBT_ITEM_CAPS, dummy.getTag("ForgeCaps"));
            }

            dummy.removeTag("ForgeCaps");

            list.appendTag(itemTag);
        }

        tag.setString(NBT_VERSION, RS.VERSION);
        tag.setTag(NBT_ITEMS, list);
        tag.setInteger(NBT_CAPACITY, capacity);

        return tag;
    }

    @Override
    public String getId() {
        return StorageDiskFactoryItem.ID;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return stacks.values();
    }

    @Override
    @Nullable
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                if (getCapacity() != -1 && getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        if (context.isVoidExcess()) {
                            return null;
                        }

                        return ItemHandlerHelper.copyStackWithSize(stack, size);
                    }

                    if (!simulate) {
                        otherStack.grow(remainingSpace);

                        onChanged();
                    }

                    return context.isVoidExcess() ? null : ItemHandlerHelper.copyStackWithSize(otherStack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        otherStack.grow(size);

                        onChanged();
                    }

                    return null;
                }
            }
        }

        if (getCapacity() != -1 && getStored() + size > getCapacity()) {
            int remainingSpace = getCapacity() - getStored();

            if (remainingSpace <= 0) {
                if (context.isVoidExcess()) {
                    return null;
                }

                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            if (!simulate) {
                stacks.put(stack.getItem(), ItemHandlerHelper.copyStackWithSize(stack, remainingSpace));

                onChanged();
            }

            return context.isVoidExcess() ? null : ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
        } else {
            if (!simulate) {
                stacks.put(stack.getItem(), ItemHandlerHelper.copyStackWithSize(stack, size));

                onChanged();
            }

            return null;
        }
    }

    @Override
    @Nullable
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        Collection<ItemStack> toAttempt = null;

        if ((flags & IComparer.COMPARE_OREDICT) == IComparer.COMPARE_OREDICT) {
            for (ItemStack ore : StackUtils.getEquivalentStacks(stack)) {
                if (toAttempt == null) {
                    toAttempt = new ArrayList<>(stacks.get(ore.getItem()));
                } else {
                    toAttempt.addAll(stacks.get(ore.getItem()));
                }
            }
        }

        if (toAttempt == null) {
            toAttempt = stacks.get(stack.getItem());
        }

        for (ItemStack otherStack : toAttempt) {
            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                if (size > otherStack.getCount()) {
                    size = otherStack.getCount();
                }

                if (!simulate) {
                    if (otherStack.getCount() - size == 0) {
                        stacks.remove(otherStack.getItem(), otherStack);
                    } else {
                        otherStack.shrink(size);
                    }

                    onChanged();
                }

                return ItemHandlerHelper.copyStackWithSize(otherStack, size);
            }
        }

        return null;
    }

    @Override
    public int getStored() {
        return stacks.values().stream().mapToInt(ItemStack::getCount).sum();
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
    public void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        int inserted = remainder == null ? size : (size - remainder.getCount());

        if (context.isVoidExcess() && storedPreInsertion + inserted > getCapacity()) {
            inserted = getCapacity() - storedPreInsertion;
        }

        return inserted;
    }

    Multimap<Item, ItemStack> getRawStacks() {
        return stacks;
    }

    private void onChanged() {
        if (listener != null) {
            listener.onChanged();
        }

        API.instance().getStorageDiskManager(world).markForSaving();
    }
}