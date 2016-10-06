package refinedstorage.apiimpl.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.gui.GuiBase;

public class CraftingMonitorElementRoot implements ICraftingMonitorElement<GuiBase> {
    public static final String ID = "root";

    private int id;
    private ItemStack output;
    private int quantity;

    public CraftingMonitorElementRoot(int id, ItemStack output, int quantity) {
        this.id = id;
        this.output = output;
        this.quantity = quantity;
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.drawItem(x + 2, y + 1, output);

        float scale = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        gui.drawString(gui.calculateOffsetOnScale(x + 21, scale), gui.calculateOffsetOnScale(y + 7, scale), quantity + " " + output.getDisplayName());

        GlStateManager.popMatrix();
    }

    @Override
    public int getTaskId() {
        return id;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(id);
        ByteBufUtils.writeItemStack(buf, output);
        buf.writeInt(quantity);
    }
}
