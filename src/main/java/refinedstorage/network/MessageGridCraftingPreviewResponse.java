package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.apiimpl.autocrafting.AutoCraftInfoStack;
import refinedstorage.gui.GuiCraftingPreview;
import refinedstorage.gui.grid.GuiCraftingStart;

import java.util.Collection;
import java.util.LinkedList;

public class MessageGridCraftingPreviewResponse implements IMessage, IMessageHandler<MessageGridCraftingPreviewResponse, IMessage> {

    private Collection<AutoCraftInfoStack> stacks;
    private int hash, quantity;

    public MessageGridCraftingPreviewResponse() {

    }

    public MessageGridCraftingPreviewResponse(Collection<AutoCraftInfoStack> stacks, int hash, int quantity) {
        this.stacks = stacks;
        this.hash = hash;
        this.quantity = quantity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.hash = buf.readInt();
        this.quantity = buf.readInt();

        this.stacks = new LinkedList<>();
        int size = buf.readInt();
        for (int i = 0 ; i < size; i++) {
            this.stacks.add(AutoCraftInfoStack.fromByteBuf(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.hash);
        buf.writeInt(this.quantity);

        buf.writeInt(stacks.size());
        for (AutoCraftInfoStack stack : stacks) {
            stack.writeToByteBuf(buf);
        }
    }

    @Override
    public IMessage onMessage(MessageGridCraftingPreviewResponse message, MessageContext ctx) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiCraftingStart) {
            screen = ((GuiCraftingStart) screen).getGui();
        }
        FMLCommonHandler.instance().showGuiScreen(new GuiCraftingPreview(screen, message.stacks, message.hash, message.quantity));
        return null;
    }
}
