package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.container.ContainerProcessingPatternEncoder;

import java.util.ArrayList;
import java.util.Collection;

public class MessageProcessingPatternEncoderTransfer extends MessageHandlerPlayerToServer<MessageProcessingPatternEncoderTransfer> implements IMessage {

    private Collection<ItemStack> inputs, outputs;

    public MessageProcessingPatternEncoderTransfer() {

    }

    public MessageProcessingPatternEncoderTransfer(Collection<ItemStack> inputs, Collection<ItemStack> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        this.inputs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.inputs.add(ByteBufUtils.readItemStack(buf));
        }
        size = buf.readInt();
        this.outputs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.outputs.add(ByteBufUtils.readItemStack(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(inputs.size());
        for (ItemStack stack : inputs) {
            ByteBufUtils.writeItemStack(buf, stack);
        }
        buf.writeInt(outputs.size());
        for (ItemStack stack : outputs) {
            ByteBufUtils.writeItemStack(buf, stack);
        }
    }

    @Override
    public void handle(MessageProcessingPatternEncoderTransfer message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerProcessingPatternEncoder) {
            ContainerProcessingPatternEncoder encoder = (ContainerProcessingPatternEncoder) player.openContainer;

            encoder.setInputs(message.inputs);
            encoder.setOutputs(message.outputs);
        }
    }
}
