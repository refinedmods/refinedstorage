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
    private int id;

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
        buf.writeBoolean(tag != null);

        if (tag != null) {
            ByteBufUtils.writeTag(buf, tag);
        }
    }

    public Item getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public void setTag(NBTTagCompound tag) {
        this.tag = tag;
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

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(type, quantity, damage);

        stack.setTagCompound(tag);

        return stack;
    }

    public boolean compare(ItemGroup other, int flags) {
        if ((flags & RefinedStorageUtils.COMPARE_DAMAGE) == RefinedStorageUtils.COMPARE_DAMAGE) {
            if (damage != other.getDamage()) {
                return false;
            }
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

        if ((flags & RefinedStorageUtils.COMPARE_QUANTITY) == RefinedStorageUtils.COMPARE_QUANTITY) {
            if (quantity != other.getQuantity()) {
                return false;
            }
        }

        return type == other.getType();
    }

    public boolean compare(ItemStack stack, int flags) {
        if ((flags & RefinedStorageUtils.COMPARE_DAMAGE) == RefinedStorageUtils.COMPARE_DAMAGE) {
            if (damage != stack.getItemDamage()) {
                return false;
            }
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

        if ((flags & RefinedStorageUtils.COMPARE_QUANTITY) == RefinedStorageUtils.COMPARE_QUANTITY) {
            if (quantity != stack.stackSize) {
                return false;
            }
        }

        return type == stack.getItem();
    }

    public boolean compareNoQuantity(ItemGroup other) {
        return compare(other, RefinedStorageUtils.COMPARE_NBT | RefinedStorageUtils.COMPARE_DAMAGE);
    }

    public boolean compareNoQuantity(ItemStack stack) {
        return compare(stack, RefinedStorageUtils.COMPARE_NBT | RefinedStorageUtils.COMPARE_DAMAGE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemGroup itemGroup = (ItemGroup) o;

        if (quantity != itemGroup.quantity) return false;
        if (damage != itemGroup.damage) return false;
        if (!type.equals(itemGroup.type)) return false;
        return tag != null ? tag.equals(itemGroup.tag) : itemGroup.tag == null;

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + quantity;
        result = 31 * result + damage;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }
}
