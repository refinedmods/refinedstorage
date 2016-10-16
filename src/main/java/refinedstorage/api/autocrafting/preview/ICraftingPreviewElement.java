package refinedstorage.api.autocrafting.preview;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import refinedstorage.api.render.IElementDrawer;

public interface ICraftingPreviewElement<T> {
    /**
     * @return the underlying element to display
     */
    T getElement();

    /**
     * @param x   position on the x axis to render
     * @param y   position on the y axis to render
     * @param itemDrawer a drawer for {@link ItemStack}s
     * @param fluidDrawer a drawer for {@link FluidStack}s
     * @param stringDrawer a drawer for {@link String}s
     */
    @SideOnly(Side.CLIENT)
    void draw(int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer, IElementDrawer<String> stringDrawer);

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
