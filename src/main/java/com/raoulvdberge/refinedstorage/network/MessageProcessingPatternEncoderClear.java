package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.tile.TileProcessingPatternEncoder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageProcessingPatternEncoderClear extends MessageHandlerPlayerToServer<MessageProcessingPatternEncoderClear> implements IMessage {
    private int x;
    private int y;
    private int z;

    public MessageProcessingPatternEncoderClear() {
    }

    public MessageProcessingPatternEncoderClear(TileProcessingPatternEncoder encoder) {
        this.x = encoder.getPos().getX();
        this.y = encoder.getPos().getY();
        this.z = encoder.getPos().getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void handle(MessageProcessingPatternEncoderClear message, EntityPlayerMP player) {
        TileEntity tile = player.getEntityWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileProcessingPatternEncoder) {
            TileProcessingPatternEncoder encoder = (TileProcessingPatternEncoder) tile;

            for (int i = 0; i < encoder.getConfiguration().getSlots(); ++i) {
                encoder.getConfiguration().setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }
}

