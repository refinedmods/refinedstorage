package com.refinedmods.refinedstorage.tile.config

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.tileentity.BlockEntity
import net.minecraftforge.items.IItemHandlerModifiable
import java.util.function.BiConsumer
import java.util.function.Function

interface IType {
    var type: Int
    val itemFilters: IItemHandlerModifiable?
    val fluidFilters: FluidInventory?

    companion object {
        fun <T> createParameter(@Nullable clientListener: TileDataParameterClientListener<Int>?): TileDataParameter<Int, T> where T : BlockEntity?, T : INetworkNodeProxy<*>? {
            return TileDataParameter<Int, T>(DataSerializers.VARINT, ITEMS, Function { t: T -> (t!!.node as IType).type }, BiConsumer { t: T, v: Int ->
                if (v == ITEMS || v == FLUIDS) {
                    (t!!.node as IType).type = v
                }
            }, clientListener)
        }

        fun <T> createParameter(): TileDataParameter<Int, T> where T : BlockEntity?, T : INetworkNodeProxy<*>? {
            return createParameter(null)
        }

        const val ITEMS = 0
        const val FLUIDS = 1
    }
}