package refinedstorage.storage;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.RefinedStorageUtils;

public class ItemGroup {
    private Item type;
    private int quantity;
    private int damage;
    private NBTTagCompound tag;
    // Used clientside
    private int id;
    private ItemStack cachedStack;

    public ItemGroup(ByteBuf buf) {
        this.id = buf.readInt();
        this.type = Item.getItemById(buf.readInt());
        this.quantity = buf.readInt();
        this.damage = buf.readInt();
        this.tag = buf.readBoolean() ? ByteBufUtils.readTag(buf) : null;
    }

    public ItemGroup(Item type, int quantity, int damage, NBTTagCompound tag) {
        this.type = type;
        this.quantity = quantity;
        this.damage = damage;
        this.tag = tag;
    }

    public ItemGroup(ItemStack stack) {
        this(stack.getItem(), stack.stackSize, stack.getItemDamage(), stack.getTagCompound());
    }

    public void toBytes(ByteBuf buf, int id) {
        buf.writeInt(id);
        buf.writeInt(Item.getIdFromItem(type));
        buf.writeInt(quantity);
        buf.writeInt(damage);
        buf.writeBoolean(hasTag());

        if (hasTag()) {
            ByteBufUtils.writeTag(buf, tag);
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Item getType() {
        return type;
    }

    public void setType(Item type) {
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean hasTag() {
        return tag != null;
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public void setTag(NBTTagCompound tag) {
        this.tag = tag;
    }

    public boolean compare(ItemGroup other, int flags) {
        if (type != other.getType()) {
            return false;
        }

        if ((flags & RefinedStorageUtils.COMPARE_QUANTITY) == RefinedStorageUtils.COMPARE_QUANTITY && quantity != other.getQuantity()) {
            return false;
        }

        if ((flags & RefinedStorageUtils.COMPARE_DAMAGE) == RefinedStorageUtils.COMPARE_DAMAGE && damage != other.getDamage()) {
            return false;
        }

        if ((flags & RefinedStorageUtils.COMPARE_NBT) == RefinedStorageUtils.COMPARE_NBT) {
            if ((tag != null && other.getTag() == null) || (tag == null && other.getTag() != null)) {
                return false;
            }

            if (tag != null && other.getTag() != null) {
                if (!tag.equals(other.getTag())) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean compare(ItemStack stack, int flags) {
        if (type != stack.getItem()) {
            return false;
        }

        if ((flags & RefinedStorageUtils.COMPARE_QUANTITY) == RefinedStorageUtils.COMPARE_QUANTITY && quantity != stack.stackSize) {
            return false;
        }

        if ((flags & RefinedStorageUtils.COMPARE_DAMAGE) == RefinedStorageUtils.COMPARE_DAMAGE && damage != stack.getItemDamage()) {
            return false;
        }

        if ((flags & RefinedStorageUtils.COMPARE_NBT) == RefinedStorageUtils.COMPARE_NBT) {
            if ((tag != null && stack.getTagCompound() == null) || (tag == null && stack.getTagCompound() != null)) {
                return false;
            }

            if (tag != null && stack.getTagCompound() != null) {
                if (!tag.equals(stack.getTagCompound())) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean compareNoQuantity(ItemGroup other) {
        return compare(other, RefinedStorageUtils.COMPARE_NBT | RefinedStorageUtils.COMPARE_DAMAGE);
    }

    public boolean compareNoQuantity(ItemStack stack) {
        return compare(stack, RefinedStorageUtils.COMPARE_NBT | RefinedStorageUtils.COMPARE_DAMAGE);
    }

    public int getId() {
        return id;
    }

    public ItemGroup copy() {
        return copy(quantity);
    }

    public ItemGroup copy(int newQuantity) {
        return new ItemGroup(type, newQuantity, damage, tag);
    }

    public ItemStack toStack() {
        ItemStack stack = new ItemStack(type, quantity, damage);

        stack.setTagCompound(tag);

        return stack;
    }

    public ItemStack toCachedStack() {
        if (cachedStack == null) {
            cachedStack = toStack();
        }

        return cachedStack;
    }
}
