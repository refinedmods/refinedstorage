package com.raoulvdberge.refinedstorage.api.autocrafting.preview;

import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Represents a crafting preview element.
 */
public interface ICraftingPreviewElement<T> {
    /**
     * @return the underlying element to display
     */
    T getElement();

    /**
     * @param x       position on the x axis to render
     * @param y       position on the y axis to render
     * @param drawers the drawers this element can use
     */
    @SideOnly(Side.CLIENT)
    void draw(int x, int y, IElementDrawers drawers);

    /**
     * @return available amount of the {@link #getElement()}
     */
    int getAvailable();

    /**
     * @return the amount to craft or missing (depends on {@link #hasMissing()} amount of the {@link #getElement()}
     */
    int getToCraft();

    /**
     * When this is true {@link #getToCraft()} will be the missing items.
     *
     * @return true when items are missing, false otherwise
     */
    boolean hasMissing();

    /**
     * @param buf buffer to write to
     */
    void writeToByteBuf(ByteBuf buf);

    /**
     * Returns the id of this element, used for serialization and deserialization over the network.
     *
     * @return the id
     */
    String getId();
}
