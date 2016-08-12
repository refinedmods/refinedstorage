package refinedstorage.gui.grid.stack;

import io.netty.buffer.ByteBuf;
import joptsimple.internal.Strings;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.gui.GuiBase;

import java.util.Locale;

public class ClientStackItem implements IClientStack {
    private int hash;
    private ItemStack stack;
    private boolean craftable;

    public ClientStackItem(ByteBuf buf) {
        stack = new ItemStack(Item.getItemById(buf.readInt()), buf.readInt(), buf.readInt());
        stack.setTagCompound(ByteBufUtils.readTag(buf));
        hash = buf.readInt();
        craftable = buf.readBoolean();
    }

    public int getHash() {
        return hash;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isCraftable() {
        return craftable;
    }

    @Override
    public String getName() {
        return stack.getDisplayName();
    }

    @Override
    public String getModId() {
        return Item.REGISTRY.getNameForObject(stack.getItem()).getResourceDomain();
    }

    @Override
    public String getTooltip() {
        return Strings.join(stack.getTooltip(null, true), "\n");
    }

    @Override
    public int getQuantity() {
        return stack.stackSize;
    }

    private String getQuantityForDisplay(boolean advanced) {
        int qty = stack.stackSize;

        if (advanced && qty > 1) {
            return String.valueOf(qty);
        }

        if (qty >= 1000000) {
            return String.format(Locale.US, "%.1f", (float) qty / 1000000).replace(".0", "") + "M";
        } else if (qty >= 1000) {
            return String.format(Locale.US, "%.1f", (float) qty / 1000).replace(".0", "") + "K";
        } else if (qty == 1) {
            return null;
        } else if (qty == 0) {
            return I18n.format("gui.refinedstorage:grid.craft");
        }

        return String.valueOf(qty);
    }

    @Override
    public void draw(GuiBase gui, int x, int y, boolean isOverWithShift) {
        gui.drawItem(x, y, stack, true, getQuantityForDisplay(isOverWithShift));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IClientStack && ((ClientStackItem) obj).getHash() == hash;
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
