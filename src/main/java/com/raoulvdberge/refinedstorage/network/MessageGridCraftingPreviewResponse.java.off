package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.GuiCraftingPreview;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGridCraftingSettings;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

public class MessageGridCraftingPreviewResponse implements IMessage, IMessageHandler<MessageGridCraftingPreviewResponse, IMessage> {
    private List<ICraftingPreviewElement> stacks;
    private int hash;
    private int quantity;
    private boolean fluids;

    public MessageGridCraftingPreviewResponse() {
    }

    public MessageGridCraftingPreviewResponse(List<ICraftingPreviewElement> stacks, int hash, int quantity, boolean fluids) {
        this.stacks = stacks;
        this.hash = hash;
        this.quantity = quantity;
        this.fluids = fluids;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.hash = buf.readInt();
        this.quantity = buf.readInt();
        this.fluids = buf.readBoolean();

        this.stacks = new LinkedList<>();

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            this.stacks.add(API.instance().getCraftingPreviewElementRegistry().get(ByteBufUtils.readUTF8String(buf)).apply(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hash);
        buf.writeInt(quantity);
        buf.writeBoolean(fluids);

        buf.writeInt(stacks.size());

        for (ICraftingPreviewElement stack : stacks) {
            ByteBufUtils.writeUTF8String(buf, stack.getId());
            stack.writeToByteBuf(buf);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageGridCraftingPreviewResponse message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (screen instanceof GuiGridCraftingSettings) {
                screen = ((GuiGridCraftingSettings) screen).getParent();
            }

            FMLCommonHandler.instance().showGuiScreen(new GuiCraftingPreview(screen, message.stacks, message.hash, message.quantity, message.fluids));
        });

        return null;
    }
}