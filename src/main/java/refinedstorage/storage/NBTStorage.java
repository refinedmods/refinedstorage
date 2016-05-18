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

    @Override
    public void addItems(List<ItemGroup> items) {
        for (Map.Entry<ItemGroupMeta, Integer> entry : groups.entrySet()) {
            items.add(new ItemGroup(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void push(ItemStack stack) {
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

    public NBTTagCompound getTag() {
        return tag;
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
