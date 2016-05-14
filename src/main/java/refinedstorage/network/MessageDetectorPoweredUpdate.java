package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.tile.TileDetector;

public class MessageDetectorPoweredUpdate implements IMessage, IMessageHandler<MessageDetectorPoweredUpdate, IMessage> {
    private int x;
    private int y;
    private int z;
    private boolean powered;

    public MessageDetectorPoweredUpdate() {
    }

    public MessageDetectorPoweredUpdate(TileDetector detector) {
        this.x = detector.getPos().getX();
        this.y = detector.getPos().getY();
        this.z = detector.getPos().getZ();
        this.powered = detector.isPowered();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        powered = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(powered);
    }

    @Override
    public IMessage onMessage(MessageDetectorPoweredUpdate message, MessageContext ctx) {
        BlockPos pos = new BlockPos(message.x, message.y, message.z);

        World world = Minecraft.getMinecraft().theWorld;

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileDetector) {
            ((TileDetector) tile).setPowered(message.powered);

            RefinedStorageUtils.reRenderBlock(world, pos);
        }

        return null;
    }
}
