package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.apiimpl.network.grid.GridOpenHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageGridOpen implements IMessage, IMessageHandler<MessageGridOpen, IMessage> {
    private int windowId;
    @Nullable
    private BlockPos pos;
    @Nullable
    private ItemStack stack;
    private int slotId;
    private int gridId;

    public MessageGridOpen() {
    }

    public MessageGridOpen(int windowId, @Nullable BlockPos pos, int gridId, @Nullable ItemStack stack, int slotId) {
        if (pos == null && stack == null) {
            throw new IllegalArgumentException("Can't be both null");
        }

        this.windowId = windowId;
        this.pos = pos;
        this.stack = stack;
        this.gridId = gridId;
        this.slotId = slotId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        windowId = buf.readInt();
        gridId = buf.readInt();
        slotId = buf.readInt();

        if (buf.readBoolean()) {
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        }

        if (buf.readBoolean()) {
            stack = ByteBufUtils.readItemStack(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(gridId);
        buf.writeInt(slotId);

        buf.writeBoolean(pos != null);
        if (pos != null) {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
        }

        buf.writeBoolean(stack != null);
        if (stack != null) {
            ByteBufUtils.writeItemStack(buf, stack);
        }
    }

    public int getWindowId() {
        return windowId;
    }

    @Nullable
    public BlockPos getPos() {
        return pos;
    }

    @Nullable
    public ItemStack getStack() {
        return stack;
    }

    public int getGridId() {
        return gridId;
    }

    public int getSlotId() {
        return slotId;
    }

    @Override
    public IMessage onMessage(MessageGridOpen message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new GridOpenHandler(message));

        return null;
    }
}
