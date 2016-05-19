package refinedstorage.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBTStorage implements IStorage {
    public static final String NBT_ITEMS = "Items";
    public static final String NBT_STORED = "Stored";

    public static final String NBT_ITEM_TYPE = "Type";
    public static final String NBT_ITEM_QUANTITY = "Quantity";
    public static final String NBT_ITEM_DAMAGE = "Damage";
    public static final String NBT_ITEM_NBT = "NBT";

    private NBTTagCompound tag;
    private int capacity;
    private int priority;
    private boolean dirty;

    // We use a map here because is much faster than looping over a list
    private Map<ItemGroupMeta, Integer> groups = new HashMap<ItemGroupMeta, Integer>();

    public NBTStorage(NBTTagCompound tag, int capacity, int priority) {
        this.tag = tag;
        this.capacity = capacity;
        this.priority = priority;

        readFromNBT();
    }

    public void readFromNBT() {
        NBTTagList list = (NBTTagList) tag.getTag(NBT_ITEMS);

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            ItemGroupMeta meta = new ItemGroupMeta(
                Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)),
                tag.getInteger(NBT_ITEM_DAMAGE),
                tag.hasKey(NBT_ITEM_NBT) ? ((NBTTagCompound) tag.getTag(NBT_ITEM_NBT)) : null
            );

            groups.put(meta, tag.getInteger(NBT_ITEM_QUANTITY));
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();

        for (Map.Entry<ItemGroupMeta, Integer> entry : groups.entrySet()) {
            NBTTagCompound itemTag = new NBTTagCompound();

            itemTag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(entry.getKey().getType()));
            itemTag.setInteger(NBT_ITEM_QUANTITY, entry.getValue());
            itemTag.setInteger(NBT_ITEM_DAMAGE, entry.getKey().getDamage());

            if (entry.getKey().hasTag()) {
                itemTag.setTag(NBT_ITEM_NBT, entry.getKey().getTag());
            }

            list.appendTag(itemTag);
        }

        tag.setTag(NBT_ITEMS, list);
    }

    @Override
    public void addItems(List<ItemGroup> items) {
        for (Map.Entry<ItemGroupMeta, Integer> entry : groups.entrySet()) {
            items.add(new ItemGroup(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void push(ItemStack stack) {
        markDirty();

        tag.setInteger(NBT_STORED, getStored(tag) + stack.stackSize);

        for (Map.Entry<ItemGroupMeta, Integer> entry : groups.entrySet()) {
            if (entry.getKey().compareNoQuantity(stack)) {
                groups.put(entry.getKey(), entry.getValue() + stack.stackSize);

                return;
            }
        }

        groups.put(new ItemGroupMeta(stack), stack.stackSize);
    }

    @Override
    public ItemStack take(ItemStack stack, int flags) {
        int quantity = stack.stackSize;

        for (Map.Entry<ItemGroupMeta, Integer> entry : groups.entrySet()) {
            if (entry.getKey().compare(stack, flags)) {
                if (quantity > entry.getValue()) {
                    quantity = entry.getValue();
                }

                if (entry.getValue() - quantity == 0) {
                    groups.remove(entry.getKey());
                } else {
                    groups.put(entry.getKey(), entry.getValue() - quantity);
                }

                tag.setInteger(NBT_STORED, getStored(tag) - quantity);

                ItemStack result = new ItemStack(
                    entry.getKey().getType(),
                    quantity,
                    entry.getKey().getDamage()
                );

                result.setTagCompound(entry.getKey().getTag());

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

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
