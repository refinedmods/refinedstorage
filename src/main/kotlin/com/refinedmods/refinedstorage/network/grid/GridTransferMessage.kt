package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.util.StackUtils.readItemStack
import com.refinedmods.refinedstorage.util.StackUtils.writeItemStack
import mezz.jei.api.gui.ingredient.IGuiIngredient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class GridTransferMessage {
    private var inputs: Map<Int, IGuiIngredient<ItemStack>?>? = null
    private var slots: List<Slot>? = null
    private val recipe = arrayOfNulls<Array<ItemStack?>?>(9)

    constructor() {}
    constructor(inputs: Map<Int?, IGuiIngredient<ItemStack?>?>, slots: List<Slot>?) {
        this.inputs = inputs
        this.slots = slots
    }

    companion object {
        fun decode(buf: PacketByteBuf): GridTransferMessage {
            val msg = GridTransferMessage()
            val slots: Int = buf.readInt()
            for (i in 0 until slots) {
                val ingredients: Int = buf.readInt()
                msg.recipe[i] = arrayOfNulls(ingredients)
                for (j in 0 until ingredients) {
                    msg.recipe[i]!![j] = readItemStack(buf)
                }
            }
            return msg
        }

        fun encode(message: GridTransferMessage, buf: PacketByteBuf) {
            buf.writeInt(message.slots!!.size)
            for (slot in message.slots!!) {
                val ingredient: IGuiIngredient<ItemStack>? = message.inputs!![slot.getSlotIndex() + 1]
                val ingredients: MutableList<ItemStack> = ArrayList()
                if (ingredient != null) {
                    for (possibleStack in ingredient.getAllIngredients()) {
                        if (possibleStack != null) {
                            ingredients.add(possibleStack)
                        }
                    }
                }
                buf.writeInt(ingredients.size)
                for (possibleStack in ingredients) {
                    writeItemStack(buf, possibleStack)
                }
            }
        }

        fun handle(message: GridTransferMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: PlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    if (player.openContainer is GridContainer) {
                        val grid = (player.openContainer as GridContainer).grid
                        if (grid!!.gridType === GridType.CRAFTING || grid!!.gridType === GridType.PATTERN) {
                            grid!!.onRecipeTransfer(player, message.recipe)
                        }
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}