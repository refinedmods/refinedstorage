package refinedstorage.api.autocrafting.preview;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import refinedstorage.api.render.IElementDrawer;
import refinedstorage.api.render.IElementDrawers;

public interface ICraftingPreviewElement<T> {
    /**
     * @return the underlying element to display
     */
    T getElement();

    /**
     * @param x   position on the x axis to render
     * @param y   position on the y axis to render
     * @param drawers the drawers this element can use
     */
    @SideOnly(Side.CLIENT)
    void draw(int x, int y, IElementDrawers drawers);

    /**
     * @return available amount of the {@link #getElement()}
     */
    int getAvailable();

    /**
     * @return toCraft or missing (depends on {@link #hasMissing()} amount of the {@link #getElement()}
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

    /**
     * Returns the id of this element, used for serialization and deserialization over the network.
     *
     * @return the id
     */
    String getId();
}
