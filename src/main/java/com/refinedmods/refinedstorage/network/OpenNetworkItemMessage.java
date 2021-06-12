package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory;
import com.refinedmods.refinedstorage.item.NetworkItem;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;
import java.util.function.Supplier;

public class OpenNetworkItemMessage {
    private final int slotId;
    private final String curioSlot;

    public OpenNetworkItemMessage(int slotId, String curioSlot) {
        this.slotId = slotId;
        this.curioSlot = curioSlot;
    }

    public static OpenNetworkItemMessage decode(PacketBuffer buf) {
        return new OpenNetworkItemMessage(buf.readInt(), buf.readString(32767));
    }

    public static void encode(OpenNetworkItemMessage message, PacketBuffer buf) {
        buf.writeInt(message.slotId);
        buf.writeString(message.curioSlot);
    }

    public static void handle(OpenNetworkItemMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                ItemStack stack = null;
                if (message.curioSlot.isEmpty()) {
                    stack = player.inventory.getStackInSlot(message.slotId);
                } else {
                    LazyOptional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player);
                    if (curiosHandler.isPresent()) {
                        Optional<ICurioStacksHandler> stacksHandler = curiosHandler.resolve().get().getStacksHandler(message.curioSlot);
                        if (stacksHandler.isPresent()) {
                            stack = stacksHandler.get().getStacks().getStackInSlot(message.slotId);
                        }
                    }
                }

                if (stack == null) {
                    return;
                }

                if (stack.getItem() instanceof NetworkItem) {
                    ItemStack finalStack = stack;
                    ((NetworkItem) stack.getItem()).applyNetwork(player.getServer(), stack, n -> n.getNetworkItemManager().open(player, finalStack, message.slotId), err -> player.sendMessage(err, player.getUniqueID()));
                } else if (stack.getItem() instanceof PortableGridBlockItem) {
                    API.instance().getGridManager().openGrid(PortableGridGridFactory.ID, player, stack, message.slotId);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
