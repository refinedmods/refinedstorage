package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridCraftingTransfer extends MessageHandlerPlayerToServer<MessageGridCraftingTransfer> implements IMessage {
    private NBTTagCompound recipe;

    public MessageGridCraftingTransfer() {
    }

    public MessageGridCraftingTransfer(NBTTagCompound recipe) {
        this.recipe = recipe;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        recipe = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, recipe);
    }

    @Override
    public void handle(MessageGridCraftingTransfer message, EntityPlayerMP player) {
        // @TODO: Make it do something
    }
}
