package com.refinedmods.refinedstorage.api.autocrafting.preview

import com.refinedmods.refinedstorage.api.render.IElementDrawers
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Represents a crafting preview element.
 */
interface ICraftingPreviewElement<T> {
    /**
     * @return the underlying element to display
     */
    fun getElement(): T

    /**
     * @param matrixStack the matrix stack
     * @param x           position on the x axis to render
     * @param y           position on the y axis to render
     * @param drawers     the drawers this element can use
     */
//    @OnlyIn(Dist.CLIENT) // TODO Limit access
    fun draw(matrixStack: MatrixStack, x: Int, y: Int, drawers: IElementDrawers)

    /**
     * @return available amount of the [.getElement]
     */
    var available: Int

    /**
     * @return the amount to craft or missing (depends on [.hasMissing] amount of the [.getElement]
     */
    var toCraft: Int

    /**
     * When this is true [.getToCraft] will be the missing items.
     *
     * @return true when items are missing, false otherwise
     */
    var missing: Boolean

    /**
     * @param buf buffer to write to
     */
    fun write(buf: PacketByteBuf)

    /**
     * Returns the id of this element, used for serialization and deserialization over the network.
     *
     * @return the id
     */
    fun getId(): Identifier
}