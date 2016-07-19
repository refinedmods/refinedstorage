package refinedstorage.apiimpl.storage;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.network.NetworkUtils;

public class ClientStack {
    private int id;
    private ItemStack stack;
    private boolean craftable;

    public ClientStack(int id, ItemStack stack, boolean craftable) {
        this.id = id;
        this.stack = stack;
        this.craftable = craftable;
    }

    public ClientStack(ByteBuf buf) {
        stack = new ItemStack(Item.getItemById(buf.readInt()), buf.readInt(), buf.readInt());
        stack.setTagCompound(ByteBufUtils.readTag(buf));
        id = buf.readInt();
        craftable = buf.readBoolean();
    }

    public int getId() {
        return id;
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean isCraftable() {
        return craftable;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientStack && ((ClientStack) obj).getId() == id;
    }

    public static void write(ByteBuf buf, INetworkMaster network, ItemStack stack) {
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.stackSize);
        buf.writeInt(stack.getItemDamage());
        ByteBufUtils.writeTag(buf, stack.getTagCompound());
        buf.writeInt(NetworkUtils.getItemStackHashCode(stack));
        buf.writeBoolean(NetworkUtils.hasPattern(network, stack));
    }
}
