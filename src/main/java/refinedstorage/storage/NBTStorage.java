package refinedstorage.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

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

    public NBTStorage(NBTTagCompound nbt, int capacity, int priority) {
        this.nbt = nbt;
        this.capacity = capacity;
        this.priority = priority;
    }

    @Override
    public void addItems(List<ItemGroup> items) {
        NBTTagList list = (NBTTagList) nbt.getTag(NBT_ITEMS);

        for (int i = 0; i < list.tagCount(); ++i) {
            items.add(createItemGroupFromNBT(list.getCompoundTagAt(i)));
        }
    }

    @Override
    public void push(ItemStack stack) {
        NBTTagList list = (NBTTagList) nbt.getTag(NBT_ITEMS);

        nbt.setInteger(NBT_STORED, getStored(nbt) + stack.stackSize);

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            ItemGroup group = createItemGroupFromNBT(tag);

            if (group.compareNoQuantity(stack)) {
                tag.setInteger(NBT_ITEM_QUANTITY, group.getQuantity() + stack.stackSize);

                return;
            }
        }

        NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(stack.getItem()));
        tag.setInteger(NBT_ITEM_QUANTITY, stack.stackSize);
        tag.setInteger(NBT_ITEM_DAMAGE, stack.getItemDamage());

        if (stack.hasTagCompound()) {
            tag.setTag(NBT_ITEM_NBT, stack.getTagCompound());
        }

        list.appendTag(tag);
    }

    @Override
    public ItemStack take(ItemStack stack, int flags) {
        int quantity = stack.stackSize;

        NBTTagList list = (NBTTagList) nbt.getTag(NBT_ITEMS);

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            ItemGroup group = createItemGroupFromNBT(tag);

            if (group.compare(stack, flags)) {
                if (quantity > group.getQuantity()) {
                    quantity = group.getQuantity();
                }

                tag.setInteger(NBT_ITEM_QUANTITY, group.getQuantity() - quantity);

                if (group.getQuantity() - quantity == 0) {
                    list.removeTag(i);
                }

                nbt.setInteger(NBT_STORED, getStored(nbt) - quantity);

                ItemStack newItem = group.toItemStack();

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

    private ItemGroup createItemGroupFromNBT(NBTTagCompound tag) {
        return new ItemGroup(Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)), tag.getInteger(NBT_ITEM_QUANTITY), tag.getInteger(NBT_ITEM_DAMAGE), tag.hasKey(NBT_ITEM_NBT) ? ((NBTTagCompound) tag.getTag(NBT_ITEM_NBT)) : null);
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
