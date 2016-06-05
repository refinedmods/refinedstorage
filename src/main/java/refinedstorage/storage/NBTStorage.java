package refinedstorage.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class NBTStorage implements IStorage {
    public static final String NBT_ITEMS = "Items";
    public static final String NBT_STORED = "Stored";

    public static final String NBT_ITEM_TYPE = "Type";
    public static final String NBT_ITEM_QUANTITY = "Quantity";
    public static final String NBT_ITEM_DAMAGE = "Damage";
    public static final String NBT_ITEM_NBT = "NBT";
    public static final String NBT_CAPS = "Caps";

    private NBTTagCompound tag;
    private int capacity;

    private boolean dirty;

    private List<ItemStack> stacks = new ArrayList<ItemStack>();

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
                tag.getInteger(NBT_ITEM_DAMAGE)
            );

            stack.setTagCompound(tag.hasKey(NBT_ITEM_NBT) ? ((NBTTagCompound) tag.getTag(NBT_ITEM_NBT)) : null);

            if (stack.getItem() != null) {
                stacks.add(stack);
            }
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();

        for (ItemStack stack : stacks) {
            NBTTagCompound itemTag = new NBTTagCompound();

            itemTag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(stack.getItem()));
            itemTag.setInteger(NBT_ITEM_QUANTITY, stack.stackSize);
            itemTag.setInteger(NBT_ITEM_DAMAGE, stack.getItemDamage());

            if (stack.hasTagCompound()) {
                itemTag.setTag(NBT_ITEM_NBT, stack.getTagCompound());
            }

            list.appendTag(itemTag);
        }

        tag.setTag(NBT_ITEMS, list);
    }

    @Override
    public void addItems(List<ItemStack> items) {
        items.addAll(stacks);
    }

    @Override
    public void push(ItemStack stack) {
        tag.setInteger(NBT_STORED, getStored() + stack.stackSize);

        for (ItemStack s : stacks) {
            if (RefinedStorageUtils.compareStackNoQuantity(s, stack)) {
                s.stackSize += stack.stackSize;

                markDirty();

                return;
            }
        }

        stacks.add(stack.copy());

        markDirty();
    }

    @Override
    public ItemStack take(ItemStack stack, int size, int flags) {
        for (ItemStack s : stacks) {
            if (RefinedStorageUtils.compareStack(s, stack, flags)) {
                if (size > s.stackSize) {
                    size = s.stackSize;
                }

                if (s.stackSize - size == 0) {
                    stacks.remove(s);
                } else {
                    s.stackSize -= size;
                }

                tag.setInteger(NBT_STORED, getStored() - size);

                markDirty();

                return ItemHandlerHelper.copyStackWithSize(s, size);
            }
        }

        return null;
    }

    @Override
    public boolean mayPush(ItemStack stack) {
        return capacity == -1 || (getStored() + stack.stackSize) <= capacity;
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
