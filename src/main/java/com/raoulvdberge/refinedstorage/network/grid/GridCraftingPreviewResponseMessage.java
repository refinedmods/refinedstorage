package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.screen.grid.CraftingPreviewScreen;
import com.raoulvdberge.refinedstorage.screen.grid.CraftingSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class GridCraftingPreviewResponseMessage {
    private List<ICraftingPreviewElement> stacks;
    private UUID id;
    private int quantity;
    private boolean fluids;

    public GridCraftingPreviewResponseMessage(List<ICraftingPreviewElement> stacks, UUID id, int quantity, boolean fluids) {
        this.stacks = stacks;
        this.id = id;
        this.quantity = quantity;
        this.fluids = fluids;
    }

    public static GridCraftingPreviewResponseMessage decode(PacketBuffer buf) {
        UUID id = buf.readUniqueId();
        int quantity = buf.readInt();
        boolean fluids = buf.readBoolean();

        List<ICraftingPreviewElement> stacks = new LinkedList<>();

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            ResourceLocation type = buf.readResourceLocation();
            stacks.add(API.instance().getCraftingPreviewElementRegistry().get(type).apply(buf));
        }

        return new GridCraftingPreviewResponseMessage(stacks, id, quantity, fluids);
    }

    public static void encode(GridCraftingPreviewResponseMessage message, PacketBuffer buf) {
        buf.writeUniqueId(message.id);
        buf.writeInt(message.quantity);
        buf.writeBoolean(message.fluids);
        buf.writeInt(message.stacks.size());

        for (ICraftingPreviewElement stack : message.stacks) {
            buf.writeResourceLocation(stack.getId());
            stack.write(buf);
        }
    }

    public static void handle(GridCraftingPreviewResponseMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Screen screen = Minecraft.getInstance().currentScreen;

            if (screen instanceof CraftingSettingsScreen) {
                screen = ((CraftingSettingsScreen) screen).getParent();
            }

            Minecraft.getInstance().displayGuiScreen(new CraftingPreviewScreen(screen, message.stacks, message.id, message.quantity, message.fluids, new TranslationTextComponent("gui.refinedstorage.crafting_preview")));
        });

        ctx.get().setPacketHandled(true);
    }
}