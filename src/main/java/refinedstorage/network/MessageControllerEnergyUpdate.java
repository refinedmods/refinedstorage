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
import refinedstorage.tile.TileController;

public class MessageControllerEnergyUpdate implements IMessage, IMessageHandler<MessageControllerEnergyUpdate, IMessage> {
    private int x;
    private int y;
    private int z;
    private int energy;

    public MessageControllerEnergyUpdate() {
    }

    public MessageControllerEnergyUpdate(TileController controller) {
        this.x = controller.getPos().getX();
        this.y = controller.getPos().getY();
        this.z = controller.getPos().getZ();
        this.energy = controller.getEnergyStored(null);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        energy = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(energy);
    }

    @Override
    public IMessage onMessage(MessageControllerEnergyUpdate message, MessageContext ctx) {
        BlockPos pos = new BlockPos(message.x, message.y, message.z);

        World world = Minecraft.getMinecraft().theWorld;

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileController) {
            ((TileController) tile).setEnergyStored(message.energy);

            RefinedStorageUtils.reRenderBlock(world, pos);
        }

        return null;
    }
}
