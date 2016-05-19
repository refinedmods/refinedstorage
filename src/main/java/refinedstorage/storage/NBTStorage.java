package refinedstorage.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public abstract class NBTStorage implements IStorage {
    public static final String NBT_ITEMS = "Items";
    public static final String NBT_STORED = "Stored";

    public static final String NBT_ITEM_TYPE = "Type";
    public static final String NBT_ITEM_QUANTITY = "Quantity";
    public static final String NBT_ITEM_DAMAGE = "Damage";
    public static final String NBT_ITEM_NBT = "NBT";

    private NBTTagCompound tag;
    private int capacity;

    private boolean dirty;

    private List<ItemGroup> groups = new ArrayList<ItemGroup>();

    public NBTStorage(NBTTagCompound tag, int capacity) {
        this.tag = tag;
        this.capacity = capacity;

        readFromNBT();
    }

    public void readFromNBT() {
        NBTTagList list = (NBTTagList) tag.getTag(NBT_ITEMS);

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            ItemGroup group = new ItemGroup(
                Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)),
                tag.getInteger(NBT_ITEM_QUANTITY),
                tag.getInteger(NBT_ITEM_DAMAGE),
                tag.hasKey(NBT_ITEM_NBT) ? ((NBTTagCompound) tag.getTag(NBT_ITEM_NBT)) : null
            );

            groups.add(group);
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();

        for (ItemGroup group : groups) {
            NBTTagCompound itemTag = new NBTTagCompound();

            itemTag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(group.getType()));
            itemTag.setInteger(NBT_ITEM_QUANTITY, group.getQuantity());
            itemTag.setInteger(NBT_ITEM_DAMAGE, group.getDamage());

            if (group.hasTag()) {
                itemTag.setTag(NBT_ITEM_NBT, group.getTag());
            }

            list.appendTag(itemTag);
        }

        tag.setTag(NBT_ITEMS, list);
    }

    @Override
    public void addItems(List<ItemGroup> items) {
        items.addAll(groups);
    }

    @Override
    public void push(ItemStack stack) {
        tag.setInteger(NBT_STORED, getStored(tag) + stack.stackSize);

        for (ItemGroup group : groups) {
            if (group.compareNoQuantity(stack)) {
                group.setQuantity(group.getQuantity() + stack.stackSize);

                markDirty();

                return;
            }
        }

        groups.add(new ItemGroup(stack));

        markDirty();
    }

    @Override
    public ItemStack take(ItemStack stack, int flags) {
        int quantity = stack.stackSize;

        for (ItemGroup group : groups) {
            if (group.compare(stack, flags)) {
                if (quantity > group.getQuantity()) {
                    quantity = group.getQuantity();
                }

                if (group.getQuantity() - quantity == 0) {
                    groups.remove(group);
                } else {
                    group.setQuantity(group.getQuantity() - quantity);
                }

                tag.setInteger(NBT_STORED, getStored(tag) - quantity);

                ItemStack result = group.toStack();

                result.stackSize = quantity;

                markDirty();

                return result;
            }
        }

        return null;
    }

    @Override
    public boolean canPush(ItemStack stack) {
        return capacity == -1 || (getStored(tag) + stack.stackSize) <= capacity;
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

    public static int getStored(NBTTagCompound tag) {
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
