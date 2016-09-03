package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.container.ContainerBase;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class MessageTileDataParameterUpdate extends MessageHandlerPlayerToServer<MessageTileDataParameterUpdate> implements IMessage {
    private TileDataParameter parameter;
    private Object value;

    public MessageTileDataParameterUpdate() {
    }

    public MessageTileDataParameterUpdate(TileDataParameter parameter, Object value) {
        this.parameter = parameter;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int id = buf.readInt();

        parameter = TileDataManager.getParameter(id);

        if (parameter != null) {
            try {
                value = parameter.getSerializer().read(new PacketBuffer(buf));
            } catch (Exception e) {
                // NO OP
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(parameter.getId());

        parameter.getSerializer().write((PacketBuffer) buf, value);
    }

    @Override
    public void handle(MessageTileDataParameterUpdate message, EntityPlayerMP player) {
        Container c = player.openContainer;

        if (c instanceof ContainerBase) {
            ITileDataConsumer consumer = message.parameter.getValueConsumer();

            if (consumer != null) {
                consumer.setValue(((ContainerBase) c).getTile(), message.value);
            }
        }
    }
}
