package refinedstorage.storage;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.RefinedStorageUtils;

public class ItemGroup {
    private ItemGroupMeta meta;
    private int quantity;
    // Used clientside
    private int id;

    public ItemGroup(ByteBuf buf) {
        this.id = buf.readInt();
        this.meta = new ItemGroupMeta(
            Item.getItemById(buf.readInt()),
            buf.readInt(),
            buf.readBoolean() ? ByteBufUtils.readTag(buf) : null
        );
        this.quantity = buf.readInt();
    }

    public ItemGroup(ItemGroupMeta meta, int quantity) {
        this.meta = meta;
        this.quantity = quantity;
    }

    public ItemGroup(ItemStack stack) {
        this(new ItemGroupMeta(stack), stack.stackSize);
    }

    public void toBytes(ByteBuf buf, int id) {
        buf.writeInt(id);
        buf.writeInt(Item.getIdFromItem(meta.getType()));
        buf.writeInt(meta.getDamage());
        buf.writeBoolean(meta.hasTag());

        if (meta.hasTag()) {
            ByteBufUtils.writeTag(buf, meta.getTag());
        }

        buf.writeInt(quantity);
    }

    public ItemGroupMeta getMeta() {
        return meta;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public ItemGroup copy() {
        return copy(quantity);
    }

    public ItemGroup copy(int newQuantity) {
        return new ItemGroup(meta, newQuantity);
    }

    public ItemStack toStack() {
        ItemStack stack = new ItemStack(meta.getType(), quantity, meta.getDamage());

        stack.setTagCompound(meta.getTag());

        return stack;
    }

    public boolean compare(ItemGroup other, int flags) {
        if ((flags & RefinedStorageUtils.COMPARE_QUANTITY) == RefinedStorageUtils.COMPARE_QUANTITY && other.getQuantity() != quantity) {
            return false;
        }

        return meta.compare(other.getMeta(), flags);
    }

    public boolean compare(ItemStack stack, int flags) {
        if ((flags & RefinedStorageUtils.COMPARE_QUANTITY) == RefinedStorageUtils.COMPARE_QUANTITY && stack.stackSize != quantity) {
            return false;
        }

        return meta.compare(stack, flags);
    }

    public boolean compareNoQuantity(ItemGroup other) {
        return compare(other, RefinedStorageUtils.COMPARE_NBT | RefinedStorageUtils.COMPARE_DAMAGE);
    }

    public boolean compareNoQuantity(ItemStack stack) {
        return compare(stack, RefinedStorageUtils.COMPARE_NBT | RefinedStorageUtils.COMPARE_DAMAGE);
    }
}
