package refinedstorage.gui.craftingmonitor;

import io.netty.buffer.ByteBuf;
import refinedstorage.gui.GuiBase;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface ICraftingMonitorElement {
    Map<Integer, Function<ByteBuf, ICraftingMonitorElement>> REGISTRY = new HashMap<>();

    void draw(GuiBase gui, int x, int y);

    int getTaskId();

    int getType();

    void write(ByteBuf buf);
}
