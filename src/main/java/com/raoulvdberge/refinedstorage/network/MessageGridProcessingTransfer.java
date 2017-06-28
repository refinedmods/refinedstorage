package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.Collection;

public class MessageGridProcessingTransfer extends MessageHandlerPlayerToServer<MessageGridProcessingTransfer> implements IMessage {
    private Collection<ItemStack> inputs;
    private Collection<ItemStack> outputs;

    public MessageGridProcessingTransfer() {
    }

    public MessageGridProcessingTransfer(Collection<ItemStack> inputs, Collection<ItemStack> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        this.inputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.inputs.add(RSUtils.readItemStack(buf));
        }

        size = buf.readInt();

        this.outputs = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.outputs.add(RSUtils.readItemStack(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(inputs.size());

        for (ItemStack stack : inputs) {
            RSUtils.writeItemStack(buf, stack);
        }

        buf.writeInt(outputs.size());

        for (ItemStack stack : outputs) {
            RSUtils.writeItemStack(buf, stack);
        }
    }

    @Override
    public void handle(MessageGridProcessingTransfer message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid.getType() == GridType.PATTERN) {
                ItemHandlerBase handler = ((NetworkNodeGrid) grid).getProcessingMatrix();

                clearInputsAndOutputs(handler);

                setInputs(handler, message.inputs);
                setOutputs(handler, message.outputs);
            }
        }
    }

    private void clearInputsAndOutputs(ItemHandlerBase handler) {
        for (int i = 0; i < 9 * 2; ++i) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
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
}
