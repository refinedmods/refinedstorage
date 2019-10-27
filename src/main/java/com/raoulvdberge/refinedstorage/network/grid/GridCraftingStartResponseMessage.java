package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.screen.grid.CraftingSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GridCraftingStartResponseMessage {
    public static GridCraftingStartResponseMessage decode(PacketBuffer buf) {
        return new GridCraftingStartResponseMessage();
    }

    public static void encode(GridCraftingStartResponseMessage message, PacketBuffer buf) {
    }

    public static void handle(GridCraftingStartResponseMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Screen screen = Minecraft.getInstance().currentScreen;

            if (screen instanceof CraftingSettingsScreen) {
                ((CraftingSettingsScreen) screen).close();
            }
        });

        ctx.get().setPacketHandled(true);
    }
}