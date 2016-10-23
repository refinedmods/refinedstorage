package com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Represents a crafting monitor element.
 */
public interface ICraftingMonitorElement {
    /**
     * @param x       position on the x axis to render
     * @param y       position on the y axis to render
     * @param drawers the drawers that this element can use
     */
    @SideOnly(Side.CLIENT)
    void draw(int x, int y, IElementDrawers drawers);

    /**
     * @return whether the crafting monitor can draw a grey background behind the element when selected
     */
    boolean canDrawSelection();

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
     * @return the tooltip of this element, or null for no tooltip
     */
    default String getTooltip() {
        return null;
    }

    /**
     * Writes the data to the network.
     *
     * @param buf the buffer
     */
    void write(ByteBuf buf);

    /**
     * Merge an element into the current element.
     *
     * @param element the element to merged with the current one
     * @return true if merge was successful
     */
    boolean merge(ICraftingMonitorElement element);

    /**
     * @return the hash code for the underlying element
     */
    int elementHashCode();
}
