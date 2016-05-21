package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.ModeConstants;

public class MessageModeToggle extends MessageHandlerPlayerToServer<MessageModeToggle> implements IMessage {
    private int x;
    private int y;
    private int z;

    public MessageModeToggle() {
    }

    public MessageModeToggle(IModeConfig mode) {
        this.x = mode.getMachinePos().getX();
        this.y = mode.getMachinePos().getY();
        this.z = mode.getMachinePos().getZ();
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
    public void handle(MessageModeToggle message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof IModeConfig) {
            IModeConfig mode = (IModeConfig) tile;

            if (mode.getMode() == ModeConstants.WHITELIST) {
                mode.setMode(ModeConstants.BLACKLIST);
            } else {
                mode.setMode(ModeConstants.WHITELIST);
            }
        }
    }
}
