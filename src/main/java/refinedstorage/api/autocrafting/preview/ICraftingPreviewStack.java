package refinedstorage.api.autocrafting.preview;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

public interface ICraftingPreviewStack {
    /**
     * @return the stack to display
     */
    ItemStack getStack();

    /**
     * @return available amount of the {@link #getStack()}
     */
    int getAvailable();

    /**
     * @return toCraft or missing (depends on {@link #hasMissing()} amount of the {@link #getStack()}
     */
    int getToCraft();

    /**
     * When this is true {@link #getToCraft()} will be the missing items
     *
     * @return true when items are missing
     */
    boolean hasMissing();

    /**
     * @param buf byte buf to write to
     */
    void writeToByteBuf(ByteBuf buf);
}
