package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GridProcessingTransferMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_processing_transfer");

    private final Collection<ItemStack> inputs;
    private final Collection<ItemStack> outputs;
    private final Collection<FluidStack> fluidInputs;
    private final Collection<FluidStack> fluidOutputs;

    public GridProcessingTransferMessage(Collection<ItemStack> inputs, Collection<ItemStack> outputs, Collection<FluidStack> fluidInputs, Collection<FluidStack> fluidOutputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
    }

    public static GridProcessingTransferMessage decode(FriendlyByteBuf buf) {
        int size = buf.readInt();

        List<ItemStack> inputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            inputs.add(StackUtils.readItemStack(buf));
        }

        size = buf.readInt();

        List<ItemStack> outputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            outputs.add(StackUtils.readItemStack(buf));
        }

        size = buf.readInt();

        List<FluidStack> fluidInputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            fluidInputs.add(FluidStack.readFromPacket(buf));
        }

        size = buf.readInt();

        List<FluidStack> fluidOutputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            fluidOutputs.add(FluidStack.readFromPacket(buf));
        }

        return new GridProcessingTransferMessage(inputs, outputs, fluidInputs, fluidOutputs);
    }

    public static void handle(GridProcessingTransferMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof GridContainerMenu) {
                IGrid grid = ((GridContainerMenu) player.containerMenu).getGrid();

                if (grid.getGridType() == GridType.PATTERN) {
                    BaseItemHandler handler = ((GridNetworkNode) grid).getProcessingMatrix();
                    FluidInventory handlerFluid = ((GridNetworkNode) grid).getProcessingMatrixFluids();

                    clearInputsAndOutputs(handler);
                    clearInputsAndOutputs(handlerFluid);

                    setInputs(handler, message.inputs, handlerFluid, message.fluidInputs);
                    setOutputs(handler, message.outputs, handlerFluid, message.fluidOutputs);


                    ((GridNetworkNode) grid).setProcessingPattern(true);
                    ((GridNetworkNode) grid).markDirty();
                }
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
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
            stack.writeToPacket(buf);
        }

        buf.writeInt(fluidOutputs.size());

        for (FluidStack stack : fluidOutputs) {
            stack.writeToPacket(buf);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    private static void clearInputsAndOutputs(BaseItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    private static void clearInputsAndOutputs(FluidInventory handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            handler.setFluid(i, FluidStack.EMPTY);
        }
    }

    private static void setInputs(BaseItemHandler handler, Collection<ItemStack> stacks, FluidInventory fluidHandler, Collection<FluidStack> fluidStacks) {
        setSlots(handler, stacks, fluidHandler, fluidStacks, 0, handler.getSlots() / 2);
    }

    private static void setOutputs(BaseItemHandler handler, Collection<ItemStack> stacks, FluidInventory fluidHandler, Collection<FluidStack> fluidStacks) {
        setSlots(handler, stacks, fluidHandler, fluidStacks, handler.getSlots() / 2, handler.getSlots());
    }

    private static void setSlots(BaseItemHandler handler, Collection<ItemStack> stacks, FluidInventory fluidHandler, Collection<FluidStack> fluidStacks, int begin, int end) {
        for (ItemStack stack : stacks) {
            handler.setStackInSlot(begin, stack);

            begin++;

            if (begin >= end) {
                break;
            }
        }
        for (FluidStack stack : fluidStacks) {

            fluidHandler.setFluid(begin, stack.copy());

            begin++;

            if (begin >= end) {
                break;
            }
        }
    }
}
