package refinedstorage.api.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import refinedstorage.api.render.IElementDrawer;

/**
 * Represents a crafting monitor element.
 */
public interface ICraftingMonitorElement<T> {
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
     * Returns the position where the corresponding task is in the crafting task list.
     * Used for cancelling tasks.
     *
     * @return the id, or -1 if no task is associated with this element
     */
    int getTaskId();

    /**
     * Returns the id of this element, used for serialization and deserialization over the network.
     *
     * @return the id
     */
    String getId();

    /**
     * Writes the data to the network.
     *
     * @param buf the buffer
     */
    void write(ByteBuf buf);
}
