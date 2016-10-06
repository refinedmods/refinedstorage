package refinedstorage.api.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ICraftingMonitorElementRegistry {
    void add(String id, Function<ByteBuf, ICraftingMonitorElement> factory);

    @Nullable
    Function<ByteBuf, ICraftingMonitorElement> getFactory(String id);
}
