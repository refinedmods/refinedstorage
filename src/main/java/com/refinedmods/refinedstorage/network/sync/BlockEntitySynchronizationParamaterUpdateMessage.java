package com.refinedmods.refinedstorage.network.sync;

import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BlockEntitySynchronizationParamaterUpdateMessage {
    private final BlockEntitySynchronizationParameter parameter;
    private final Object value;

    public BlockEntitySynchronizationParamaterUpdateMessage(BlockEntitySynchronizationParameter parameter, Object value) {
        this.parameter = parameter;
        this.value = value;
    }

    public static BlockEntitySynchronizationParamaterUpdateMessage decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();

        BlockEntitySynchronizationParameter parameter = BlockEntitySynchronizationManager.getParameter(id);
        Object value = null;

        if (parameter != null) {
            try {
                value = parameter.getSerializer().read(buf);
            } catch (Exception e) {
                // NO OP
            }
        }

        return new BlockEntitySynchronizationParamaterUpdateMessage(parameter, value);
    }

    public static void encode(BlockEntitySynchronizationParamaterUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.parameter.getId());

        message.parameter.getSerializer().write(buf, message.value);
    }

    public static void handle(BlockEntitySynchronizationParamaterUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            AbstractContainerMenu c = ctx.get().getSender().containerMenu;

            if (c instanceof BaseContainerMenu) {
                BiConsumer consumer = message.parameter.getValueConsumer();

                if (consumer != null) {
                    consumer.accept(((BaseContainerMenu) c).getBlockEntity(), message.value);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
