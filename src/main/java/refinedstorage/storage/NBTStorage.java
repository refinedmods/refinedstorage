package refinedstorage.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class NBTStorage implements IStorage {
    public static final String NBT_ITEMS = "Items";
    public static final String NBT_STORED = "Stored";

    public static final String NBT_ITEM_TYPE = "Type";
    public static final String NBT_ITEM_QUANTITY = "Quantity";
    public static final String NBT_ITEM_DAMAGE = "Damage";
    public static final String NBT_ITEM_NBT = "NBT";

    private NBTTagCompound nbt;
    private int capacity;
    private int priority;
    private List<ItemGroup> items = new ArrayList<ItemGroup>();

    public NBTStorage(NBTTagCompound nbt, int capacity, int priority) {
        this.nbt = nbt;
        this.capacity = capacity;
        this.priority = priority;

        readFromNBT();
    }

    public void readFromNBT() {
        NBTTagList list = (NBTTagList) nbt.getTag(NBT_ITEMS);

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            items.add(new ItemGroup(
                Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)),
                tag.getInteger(NBT_ITEM_QUANTITY),
                tag.getInteger(NBT_ITEM_DAMAGE),
                tag.hasKey(NBT_ITEM_NBT) ? ((NBTTagCompound) tag.getTag(NBT_ITEM_NBT)) : null)
            );
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();

        for (ItemGroup item : items) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(item.getType()));
            tag.setInteger(NBT_ITEM_QUANTITY, item.getQuantity());
            tag.setInteger(NBT_ITEM_DAMAGE, item.getDamage());

            if (item.getTag() != null) {
                tag.setTag(NBT_ITEM_NBT, item.getTag());
            }

            list.appendTag(tag);
        }

        nbt.setTag(NBT_ITEMS, list);
    }

    @Override
    public void addItems(List<ItemGroup> items) {
        items.addAll(this.items);
    }

    @Override
    public void push(ItemStack stack) {
        nbt.setInteger(NBT_STORED, getStored(nbt) + stack.stackSize);

        for (ItemGroup item : items) {
            if (item.compareNoQuantity(stack)) {
                item.setQuantity(item.getQuantity() + stack.stackSize);

                return;
            }
        }

        items.add(new ItemGroup(stack));
    }

    @Override
    public ItemStack take(ItemStack stack, int flags) {
        int quantity = stack.stackSize;

        for (ItemGroup item : items) {
            if (item.compare(stack, flags)) {
                if (quantity > item.getQuantity()) {
                    quantity = item.getQuantity();
                }

                item.setQuantity(item.getQuantity() - quantity);

                if (item.getQuantity() == 0) {
                    items.remove(item);
                }

                nbt.setInteger(NBT_STORED, getStored(nbt) - quantity);

                ItemStack newItem = item.toItemStack();

                newItem.stackSize = quantity;

                return newItem;
            }
        }

        return null;
    }

    @Override
    public boolean canPush(ItemStack stack) {
        if (capacity == -1) {
            return true;
        }

        return (getStored(nbt) + stack.stackSize) <= capacity;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static int getStored(NBTTagCompound tag) {
        return tag.getInteger(NBT_STORED);
    }

    public static NBTTagCompound getBaseNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_ITEMS, new NBTTagList());
        tag.setInteger(NBT_STORED, 0);

        return tag;
    }

    public static ItemStack initNBT(ItemStack stack) {
        stack.setTagCompound(getBaseNBT());
        return stack;
    }
}
