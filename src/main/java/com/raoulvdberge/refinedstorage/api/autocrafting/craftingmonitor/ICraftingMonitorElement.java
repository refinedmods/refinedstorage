package com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Represents a crafting monitor element.
 */
public interface ICraftingMonitorElement {
    /**
     * @param x       position on the x axis to render
     * @param y       position on the y axis to render
     * @param drawers the drawers that this element can use
     */
    @OnlyIn(Dist.CLIENT)
    void draw(int x, int y, IElementDrawers drawers);

    /**
     * Returns the id of this element, used for serialization and deserialization over the network.
     *
     * @return the id
     */
    String getId();

    /**
     * @return the tooltip of this element, or null for no tooltip
     */
    @Nullable
    default String getTooltip() {
        return null;
    }

    /**
     * Writes the data to the network.
     *
     * @param buf the buffer
     */
    void write(PacketBuffer buf);

    /**
     * Merge an element into the current element.
     *
     * @param element the element to merged with the current one
     * @return true if merge was successful, false otherwise
     */
    boolean merge(ICraftingMonitorElement element);

    /**
     * @return the hash code for the underlying element
     */
    int elementHashCode();
}
