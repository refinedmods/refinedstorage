package refinedstorage.api.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A implementation of {@link IStorage} that stores storage items in NBT.
 */
public abstract class NBTStorage implements IStorage {
    public static final String NBT_ITEMS = "Items";
    public static final String NBT_STORED = "Stored";

    public static final String NBT_ITEM_TYPE = "Type";
    public static final String NBT_ITEM_QUANTITY = "Quantity";
    public static final String NBT_ITEM_DAMAGE = "Damage";
    public static final String NBT_ITEM_NBT = "NBT";
    public static final String NBT_ITEM_CAPS = "Caps";

    private NBTTagCompound tag;
    private int capacity;

    private boolean dirty;

    private List<ItemStack> stacks = new ArrayList<ItemStack>();

    /**
     * @param tag      The NBT tag we are reading from and writing the amount stored to, has to be initialized with {@link NBTStorage#createNBT()}
     * @param capacity The capacity of this storage
     */
    public NBTStorage(NBTTagCompound tag, int capacity) {
        this.tag = tag;
        this.capacity = capacity;

        readFromNBT();
    }

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

            if (stack.getItem() != null) {
                stacks.add(stack);
            }
        }
    }

    /**
     * Writes the items to the NBT tag, check for {@link NBTStorage#isDirty()} before doing this to be efficient.
     *
     * @param tag The tag to write to
     */
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();

        // Dummy value for extracting ForgeCaps
        NBTTagCompound dummy = new NBTTagCompound();

        for (ItemStack stack : stacks) {
            NBTTagCompound itemTag = new NBTTagCompound();

            itemTag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(stack.getItem()));
            itemTag.setInteger(NBT_ITEM_QUANTITY, stack.stackSize);
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
    }

    @Override
    public void addItems(List<ItemStack> items) {
        items.addAll(stacks);
    }

    @Override
    public ItemStack push(ItemStack stack, int size, boolean simulate) {
        for (ItemStack otherStack : stacks) {
            if (RefinedStorageUtils.compareStackNoQuantity(otherStack, stack)) {
                if (!simulate) {
                    markDirty();
                }

                if (getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return stack;
                    }

                    if (!simulate) {
                        tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                        otherStack.stackSize += remainingSpace;
                    }

                    return ItemHandlerHelper.copyStackWithSize(otherStack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        tag.setInteger(NBT_STORED, getStored() + size);

                        otherStack.stackSize += size;
                    }

                    return null;
                }
            }
        }

        if (!simulate) {
            markDirty();
        }

        if (getStored() + size > getCapacity()) {
            int remainingSpace = getCapacity() - getStored();

            if (remainingSpace <= 0) {
                return stack;
            }

            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + remainingSpace);

                stacks.add(ItemHandlerHelper.copyStackWithSize(stack, remainingSpace));
            }

            return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
        } else {
            if (!simulate) {
                tag.setInteger(NBT_STORED, getStored() + size);

                stacks.add(ItemHandlerHelper.copyStackWithSize(stack, size));
            }

            return null;
        }
    }

    @Override
    public ItemStack take(ItemStack stack, int size, int flags) {
        for (ItemStack otherStack : stacks) {
            if (RefinedStorageUtils.compareStack(otherStack, stack, flags)) {
                if (size > otherStack.stackSize) {
                    size = otherStack.stackSize;
                }

                if (otherStack.stackSize - size == 0) {
                    stacks.remove(otherStack);
                } else {
                    otherStack.stackSize -= size;
                }

                tag.setInteger(NBT_STORED, getStored() - size);

                markDirty();

                return ItemHandlerHelper.copyStackWithSize(otherStack, size);
            }
        }

        return null;
    }

    @Override
    public int getStored() {
        return getStoredFromNBT(tag);
    }

    public int getCapacity() {
        return capacity;
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markClean() {
        this.dirty = false;
    }

    public static int getStoredFromNBT(NBTTagCompound tag) {
        return tag.getInteger(NBT_STORED);
    }

    public static NBTTagCompound createNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_ITEMS, new NBTTagList());
        tag.setInteger(NBT_STORED, 0);

        return tag;
    }

    public static ItemStack createStackWithNBT(ItemStack stack) {
        stack.setTagCompound(createNBT());

        return stack;
    }
}
