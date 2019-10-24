package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.Collection;

public class MessageGridProcessingTransfer extends MessageHandlerPlayerToServer<MessageGridProcessingTransfer> implements IMessage {
    private Collection<ItemStack> inputs;
    private Collection<ItemStack> outputs;

    private Collection<FluidStack> fluidInputs;
    private Collection<FluidStack> fluidOutputs;

    public MessageGridProcessingTransfer() {
    }

    public MessageGridProcessingTransfer(Collection<ItemStack> inputs, Collection<ItemStack> outputs, Collection<FluidStack> fluidInputs, Collection<FluidStack> fluidOutputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        this.inputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.inputs.add(StackUtils.readItemStack(buf));
        }

        size = buf.readInt();

        this.outputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.outputs.add(StackUtils.readItemStack(buf));
        }

        size = buf.readInt();

        this.fluidInputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.fluidInputs.add(StackUtils.readFluidStack(buf));
        }

        size = buf.readInt();

        this.fluidOutputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.fluidOutputs.add(StackUtils.readFluidStack(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(inputs.size());

        for (ItemStack stack : inputs) {
            StackUtils.writeItemStack(buf, stack);
        }

        buf.writeInt(outputs.size());

        for (ItemStack stack : outputs) {
            StackUtils.writeItemStack(buf, stack);
        }

        buf.writeInt(fluidInputs.size());

        for (FluidStack stack : fluidInputs) {
            StackUtils.writeFluidStack(buf, stack);
        }

        buf.writeInt(fluidOutputs.size());

        for (FluidStack stack : fluidOutputs) {
            StackUtils.writeFluidStack(buf, stack);
        }
    }

    @Override
    public void handle(MessageGridProcessingTransfer message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid.getGridType() == GridType.PATTERN) {
                ItemHandlerBase handler = ((NetworkNodeGrid) grid).getProcessingMatrix();
                FluidInventory handlerFluid = ((NetworkNodeGrid) grid).getProcessingMatrixFluids();

                clearInputsAndOutputs(handler);
                clearInputsAndOutputs(handlerFluid);

                setInputs(handler, message.inputs);
                setOutputs(handler, message.outputs);

                setFluidInputs(handlerFluid, message.fluidInputs);
                setFluidOutputs(handlerFluid, message.fluidOutputs);
            }
        }
    }

    private void clearInputsAndOutputs(ItemHandlerBase handler) {
        for (int i = 0; i < 9 * 2; ++i) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    private void clearInputsAndOutputs(FluidInventory handler) {
        for (int i = 0; i < 9 * 2; ++i) {
            handler.setFluid(i, null);
        }
    }

    private void setInputs(ItemHandlerBase handler, Collection<ItemStack> stacks) {
        setSlots(handler, stacks, 0, 9);
    }

    private void setOutputs(ItemHandlerBase handler, Collection<ItemStack> stacks) {
        setSlots(handler, stacks, 9, 18);
    }

    private void setSlots(ItemHandlerBase handler, Collection<ItemStack> stacks, int begin, int end) {
        for (ItemStack stack : stacks) {
            handler.setStackInSlot(begin, stack);

            begin++;

            if (begin >= end) {
                break;
            }
        }
    }

    private void setFluidInputs(FluidInventory inventory, Collection<FluidStack> stacks) {
        setFluidSlots(inventory, stacks, 0, 9);
    }

    private void setFluidOutputs(FluidInventory inventory, Collection<FluidStack> stacks) {
        setFluidSlots(inventory, stacks, 9, 18);
    }

    private void setFluidSlots(FluidInventory inventory, Collection<FluidStack> stacks, int begin, int end) {
        for (FluidStack stack : stacks) {

            inventory.setFluid(begin, stack.copy());

            begin++;

            if (begin >= end) {
                break;
            }
        }
    }
}
