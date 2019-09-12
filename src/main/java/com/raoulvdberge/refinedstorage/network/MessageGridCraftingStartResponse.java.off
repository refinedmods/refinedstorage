package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.gui.grid.GuiGridCraftingSettings;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageGridCraftingStartResponse implements IMessage, IMessageHandler<MessageGridCraftingStartResponse, IMessage> {
    public MessageGridCraftingStartResponse() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // NO OP
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // NO OP
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageGridCraftingStartResponse message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (screen instanceof GuiGridCraftingSettings) {
                ((GuiGridCraftingSettings) screen).close();
            }
        });

        return null;
    }
}