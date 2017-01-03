package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.StorageDiskType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A implementation of {@link IStorage<ItemStack>} that stores storage items in NBT.
 */
public class StorageDiskItem implements IStorageDisk<ItemStack> {
    private static final int PROTOCOL = 1;

    private static final String NBT_PROTOCOL = "Protocol";

    private static final String NBT_ITEMS = "Items";
    private static final String NBT_STORED = "Stored";

    private static final String NBT_ITEM_TYPE = "Type";
    private static final String NBT_ITEM_QUANTITY = "Quantity";
    private static final String NBT_ITEM_DAMAGE = "Damage";
    private static final String NBT_ITEM_NBT = "NBT";
    private static final String NBT_ITEM_CAPS = "Caps";

    private NBTTagCompound tag;
    private int capacity;

    private NonNullList<ItemStack> stacks = NonNullList.create();

    /**
     * @param tag      The NBT tag we are reading from and writing the amount stored to, has to be initialized with {@link StorageDiskItem#getTag()} if it doesn't exist yet
     * @param capacity The capacity of this storage, -1 for infinite capacity
     */
    public StorageDiskItem(NBTTagCompound tag, int capacity) {
        this.tag = tag;
        this.capacity = capacity;
    }

    @Override
    public void readFromNBT() {
        NBTTagList list = (NBTTagList) tag.getTag(NBT_ITEMS);

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            ItemStack stack = new ItemStack(
                Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)),
                tag.getInteger(NBT_ITEM_QUANTITY),
                tag.getInteger(NBT_ITEM_DAMAGE),
                tag.hasKey(NBT_ITEM_CAPS) ? tag.getCompoundTag(NBT_ITEM_CAPS) : null
            );

            stack.setTagCompound(tag.hasKey(NBT_ITEM_NBT) ? tag.getCompoundTag(NBT_ITEM_NBT) : null);

            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
    }

    @Override
    public void writeToNBT() {
        NBTTagList list = new NBTTagList();

        // Dummy value for extracting ForgeCaps
        NBTTagCompound dummy = new NBTTagCompound();

        for (ItemStack stack : stacks) {
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

        tag.setTag(NBT_ITEMS, list);
        tag.setInteger(NBT_PROTOCOL, PROTOCOL);
    }

    @Override
    public StorageDiskType getType() {
        return StorageDiskType.ITEMS;
    }

    @Override
    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    @Override
    @Nullable
    public synchronized ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        for (ItemStack otherStack : stacks) {
            if (API.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                if (getCapacity() != -1 && getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        if (isVoiding()) {
                            return null;
                        }

                        return ItemHandlerHelper.copyStackWithSize(stack, size);
                    }

                    if (!simulate) {
                        tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                        otherStack.grow(remainingSpace);

                        onChanged();
                    }

                    return isVoiding() ? null : ItemHandlerHelper.copyStackWithSize(otherStack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        tag.setInteger(NBT_STORED, getStored() + size);

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
                if (isVoiding()) {
                    return null;
                }

                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                stacks.add(ItemHandlerHelper.copyStackWithSize(stack, remainingSpace));

                onChanged();
            }

            return isVoiding() ? null : ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
        } else {
            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + size);

                stacks.add(ItemHandlerHelper.copyStackWithSize(stack, size));

                onChanged();
            }

            return null;
        }
    }

    @Override
    @Nullable
    public synchronized ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        for (ItemStack otherStack : stacks) {
            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                if (size > otherStack.getCount()) {
                    size = otherStack.getCount();
                }

                if (!simulate) {
                    if (otherStack.getCount() - size == 0) {
                        stacks.remove(otherStack);
                    } else {
                        otherStack.shrink(size);
                    }

                    tag.setInteger(NBT_STORED, getStored() - size);

                    onChanged();
                }

                return ItemHandlerHelper.copyStackWithSize(otherStack, size);
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
    public void onChanged() {
        // NO OP
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_ITEMS) && stack.getTagCompound().hasKey(NBT_STORED);
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        int inserted = remainder == null ? size : (size - remainder.getCount());

        if (isVoiding() && storedPreInsertion + inserted > getCapacity()) {
            inserted = getCapacity() - storedPreInsertion;
        }

        return inserted;
    }

    public static NBTTagCompound getShareTag(NBTTagCompound tag) {
        NBTTagCompound otherTag = new NBTTagCompound();

        otherTag.setInteger(NBT_STORED, getStored(tag));
        otherTag.setTag(NBT_ITEMS, new NBTTagList()); // To circumvent not being able to insert disks in Disk Drives (see ItemStorageNBT#isValid(ItemStack)).
        otherTag.setInteger(NBT_PROTOCOL, PROTOCOL);

        return otherTag;
    }

    public static int getStored(NBTTagCompound tag) {
        return tag.getInteger(NBT_STORED);
    }

    public static NBTTagCompound getTag() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_ITEMS, new NBTTagList());
        tag.setInteger(NBT_STORED, 0);
        tag.setInteger(NBT_PROTOCOL, PROTOCOL);

        return tag;
    }

    public static ItemStack initDisk(ItemStack stack) {
        stack.setTagCompound(getTag());

        return stack;
    }
}