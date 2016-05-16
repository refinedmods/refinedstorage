package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.proxy.ClientProxy;
import refinedstorage.tile.solderer.TileSolderer;

public class MessageSoldererWorkingUpdate implements IMessage, IMessageHandler<MessageSoldererWorkingUpdate, IMessage> {
    private int x;
    private int y;
    private int z;
    private boolean working;

    public MessageSoldererWorkingUpdate() {
    }

    public MessageSoldererWorkingUpdate(TileSolderer solderer) {
        this.x = solderer.getPos().getX();
        this.y = solderer.getPos().getY();
        this.z = solderer.getPos().getZ();
        this.working = solderer.isWorking();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        working = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(working);
    }

    @Override
    public IMessage onMessage(MessageSoldererWorkingUpdate message, MessageContext ctx) {
        BlockPos pos = new BlockPos(message.x, message.y, message.z);

        World world = ClientProxy.getWorld();

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileSolderer) {
            boolean wasWorking = ((TileSolderer) tile).isWorking();

            ((TileSolderer) tile).setWorking(message.working);

            if (wasWorking != message.working) {
                RefinedStorageUtils.reRenderBlock(world, pos);
            }
        }

        return null;
    }
}
