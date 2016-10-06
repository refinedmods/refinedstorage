package refinedstorage.api.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;
import refinedstorage.gui.GuiBase;

public interface ICraftingMonitorElement {
    void draw(GuiBase gui, int x, int y);

    int getTaskId();

    String getId();

    void write(ByteBuf buf);
}
