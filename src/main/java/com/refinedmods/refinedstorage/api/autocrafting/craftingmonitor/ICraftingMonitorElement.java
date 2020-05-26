package com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor;

import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
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
     * Returns the id for the base of this element, used for sorting in the {@link CraftingMonitorElementList}
     *
     * @return the id
     */

    ResourceLocation getBaseId();

    /**
     * Returns the id of this element, used for serialization and deserialization over the network.
     *
     * @return the id
     */
    ResourceLocation getId();

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
     * @return the hash code for the underlying base item/fluid element
     */
    int baseElementHashCode();

    /**
     * @return the hash code for the underlying element
     */
    int elementHashCode();
}
