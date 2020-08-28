package com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor

import com.refinedmods.refinedstorage.api.render.IElementDrawers
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * Represents a crafting monitor element.
 */
interface ICraftingMonitorElement {
    /**
     * @param matrixStack the matrix stack
     * @param x           position on the x axis to render
     * @param y           position on the y axis to render
     * @param drawers     the drawers that this element can use
     */
//    @OnlyIn(Dist.CLIENT) // TODO Figure out how to limit access
    fun draw(matrixStack: MatrixStack, x: Int, y: Int, drawers: IElementDrawers)

    /**
     * Returns the id for the base of this element, used for sorting in the [CraftingMonitorElementList]
     *
     * @return the id
     */
    fun getBaseId(): Identifier

    /**
     * Returns the id of this element, used for serialization and deserialization over the network.
     *
     * @return the id
     */
    fun getId(): Identifier

    /**
     * @return the tooltip of this element
     */
    fun getTooltip(): List<Text> {
        return emptyList()
    }

    /**
     * Writes the data to the network.
     *
     * @param buf the buffer
     */
    fun write(buf: PacketByteBuf)

    /**
     * Merge an element into the current element.
     *
     * @param element the element to merged with the current one
     * @return true if merge was successful, false otherwise
     */
    fun merge(element: ICraftingMonitorElement): Boolean

    /**
     * @return the hash code for the underlying base item/fluid element
     */
    fun baseElementHashCode(): Int

    /**
     * @return the hash code for the underlying element
     */
    fun elementHashCode(): Int
}