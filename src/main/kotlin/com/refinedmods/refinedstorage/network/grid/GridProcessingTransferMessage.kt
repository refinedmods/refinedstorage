package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.util.StackUtils.readItemStack
import com.refinedmods.refinedstorage.util.StackUtils.writeItemStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class GridProcessingTransferMessage(private val inputs: Collection<ItemStack>, private val outputs: Collection<ItemStack>, fluidInputs: Collection<FluidInstance>, fluidOutputs: Collection<FluidInstance>) {
    private val fluidInputs: Collection<FluidInstance>
    private val fluidOutputs: Collection<FluidInstance>

    companion object {
        fun decode(buf: PacketByteBuf): GridProcessingTransferMessage {
            var size: Int = buf.readInt()
            val inputs: MutableList<ItemStack> = ArrayList(size)
            for (i in 0 until size) {
                inputs.add(readItemStack(buf))
            }
            size = buf.readInt()
            val outputs: MutableList<ItemStack> = ArrayList(size)
            for (i in 0 until size) {
                outputs.add(readItemStack(buf))
            }
            size = buf.readInt()
            val fluidInputs: MutableList<FluidInstance> = ArrayList<FluidInstance>(size)
            for (i in 0 until size) {
                fluidInputs.add(FluidInstance.readFromPacket(buf))
            }
            size = buf.readInt()
            val fluidOutputs: MutableList<FluidInstance> = ArrayList<FluidInstance>(size)
            for (i in 0 until size) {
                fluidOutputs.add(FluidInstance.readFromPacket(buf))
            }
            return GridProcessingTransferMessage(inputs, outputs, fluidInputs, fluidOutputs)
        }

        fun encode(message: GridProcessingTransferMessage, buf: PacketByteBuf) {
            buf.writeInt(message.inputs.size)
            for (stack in message.inputs) {
                writeItemStack(buf, stack)
            }
            buf.writeInt(message.outputs.size)
            for (stack in message.outputs) {
                writeItemStack(buf, stack)
            }
            buf.writeInt(message.fluidInputs.size)
            for (stack in message.fluidInputs) {
                stack.writeToPacket(buf)
            }
            buf.writeInt(message.fluidOutputs.size)
            for (stack in message.fluidOutputs) {
                stack.writeToPacket(buf)
            }
        }

        fun handle(message: GridProcessingTransferMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: PlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    if (player.openContainer is GridContainer) {
                        val grid = (player.openContainer as GridContainer).grid
                        if (grid!!.gridType === GridType.PATTERN) {
                            val handler: BaseItemHandler = (grid as GridNetworkNode?)!!.getProcessingMatrix()
                            val handlerFluid: FluidInventory = (grid as GridNetworkNode?)!!.getProcessingMatrixFluids()
                            clearInputsAndOutputs(handler)
                            clearInputsAndOutputs(handlerFluid)
                            setInputs(handler, message.inputs)
                            setOutputs(handler, message.outputs)
                            setFluidInputs(handlerFluid, message.fluidInputs)
                            setFluidOutputs(handlerFluid, message.fluidOutputs)
                            (grid as GridNetworkNode?)!!.setProcessingPattern(true)
                            (grid as GridNetworkNode?)!!.markDirty()
                        }
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }

        private fun clearInputsAndOutputs(handler: BaseItemHandler) {
            for (i in 0 until 9 * 2) {
                handler.setStackInSlot(i, ItemStack.EMPTY)
            }
        }

        private fun clearInputsAndOutputs(handler: FluidInventory) {
            for (i in 0 until 9 * 2) {
                handler.setFluid(i, FluidInstance.EMPTY)
            }
        }

        private fun setInputs(handler: BaseItemHandler, stacks: Collection<ItemStack>) {
            setSlots(handler, stacks, 0, 9)
        }

        private fun setOutputs(handler: BaseItemHandler, stacks: Collection<ItemStack>) {
            setSlots(handler, stacks, 9, 18)
        }

        private fun setSlots(handler: BaseItemHandler, stacks: Collection<ItemStack>, begin: Int, end: Int) {
            var begin = begin
            for (stack in stacks) {
                handler.setStackInSlot(begin, stack)
                begin++
                if (begin >= end) {
                    break
                }
            }
        }

        private fun setFluidInputs(inventory: FluidInventory, stacks: Collection<FluidInstance>) {
            setFluidSlots(inventory, stacks, 0, 9)
        }

        private fun setFluidOutputs(inventory: FluidInventory, stacks: Collection<FluidInstance>) {
            setFluidSlots(inventory, stacks, 9, 18)
        }

        private fun setFluidSlots(inventory: FluidInventory, stacks: Collection<FluidInstance>, begin: Int, end: Int) {
            var begin = begin
            for (stack in stacks) {
                inventory.setFluid(begin, stack.copy())
                begin++
                if (begin >= end) {
                    break
                }
            }
        }
    }

    init {
        this.fluidInputs = fluidInputs
        this.fluidOutputs = fluidOutputs
    }
}