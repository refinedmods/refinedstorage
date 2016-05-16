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
import refinedstorage.tile.TileMachine;

public class MessageMachineConnectedUpdate implements IMessage, IMessageHandler<MessageMachineConnectedUpdate, IMessage> {
    private int x;
    private int y;
    private int z;
    private boolean connected;

    public MessageMachineConnectedUpdate() {
    }

    public MessageMachineConnectedUpdate(TileMachine machine) {
        this.x = machine.getPos().getX();
        this.y = machine.getPos().getY();
        this.z = machine.getPos().getZ();
        this.connected = machine.isConnected() && machine.mayUpdate();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        connected = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(connected);
    }

    @Override
    public IMessage onMessage(MessageMachineConnectedUpdate message, MessageContext ctx) {
        BlockPos pos = new BlockPos(message.x, message.y, message.z);

        World world = ClientProxy.getWorld();

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileMachine) {
            boolean wasConnected = ((TileMachine) tile).isConnected();

            ((TileMachine) tile).setConnected(message.connected);

            if (wasConnected != message.connected) {
                RefinedStorageUtils.reRenderBlock(world, pos);
            }
        }

        return null;
    }
}
