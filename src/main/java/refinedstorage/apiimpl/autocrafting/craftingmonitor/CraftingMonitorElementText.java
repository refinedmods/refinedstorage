package refinedstorage.apiimpl.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.gui.GuiBase;

public class CraftingMonitorElementText implements ICraftingMonitorElement<GuiBase> {
    public static final String ID = "text";

    private String text;
    private int offset;

    public CraftingMonitorElementText(String text, int offset) {
        this.text = text;
        this.offset = offset;
    }

    public String getText() {
        return text;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        float scale = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        gui.drawString(gui.calculateOffsetOnScale(x + offset, scale), gui.calculateOffsetOnScale(y + 7, scale), I18n.format(text));

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
        ByteBufUtils.writeUTF8String(buf, text);
        buf.writeInt(offset);
    }
}
