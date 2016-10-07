package refinedstorage.apiimpl.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.gui.GuiBase;

public class CraftingMonitorElementToTake extends CraftingMonitorElementItemRender {
    public static final String ID = "to_take";

    private ItemStack toTake;
    private int remaining;

    public CraftingMonitorElementToTake(ItemStack toTake, int remaining) {
        this.toTake = toTake;
        this.remaining = remaining;
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        super.draw(gui, x + 32, y);
    }

    @Override
    public int getTaskId() {
        return -1;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, toTake);
        buf.writeInt(remaining);
    }

    @Override
    protected ItemStack getItem() {
        return toTake;
    }

    @Override
    protected int getQuantity() {
        return remaining;
    }
}
