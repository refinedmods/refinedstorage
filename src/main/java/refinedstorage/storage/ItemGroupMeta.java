package refinedstorage.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.RefinedStorageUtils;

public class ItemGroupMeta {
    private Item type;
    private int damage;
    private NBTTagCompound tag;

    public ItemGroupMeta(Item type, int damage, NBTTagCompound tag) {
        this.type = type;
        this.damage = damage;
        this.tag = tag;
    }

    public ItemGroupMeta(ItemStack stack) {
        this(stack.getItem(), stack.getItemDamage(), stack.getTagCompound());
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

    public boolean compare(ItemGroupMeta other, int flags) {
        if (type != other.getType()) {
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

    public boolean compareNoQuantity(ItemGroupMeta other) {
        return compare(other, RefinedStorageUtils.COMPARE_NBT | RefinedStorageUtils.COMPARE_DAMAGE);
    }

    public boolean compareNoQuantity(ItemStack stack) {
        return compare(stack, RefinedStorageUtils.COMPARE_NBT | RefinedStorageUtils.COMPARE_DAMAGE);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        ItemGroupMeta meta = (ItemGroupMeta) other;

        if (damage != meta.damage || !type.equals(meta.type)) {
            return false;
        }

        return tag != null ? tag.equals(meta.tag) : meta.tag == null;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + damage;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }
}
