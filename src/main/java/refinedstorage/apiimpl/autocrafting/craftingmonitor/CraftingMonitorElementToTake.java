package refinedstorage.apiimpl.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.gui.GuiBase;

public class CraftingMonitorElementToTake implements ICraftingMonitorElement {
    public static final String ID = "to_take";

    private ItemStack toTake;
    private int remaining;

    public CraftingMonitorElementToTake(ItemStack toTake, int remaining) {
        this.toTake = toTake;
        this.remaining = remaining;
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.drawItem(x + 2, y + 1, toTake);

        float scale = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        gui.drawString(gui.calculateOffsetOnScale(x + 21, scale), gui.calculateOffsetOnScale(y + 7, scale), remaining + " " + toTake.getDisplayName());

        GlStateManager.popMatrix();
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
}
