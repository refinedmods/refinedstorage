package com.refinedmods.refinedstorage.network.sync;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.container.BaseContainerMenu;

import java.util.function.BiConsumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class BlockEntitySynchronizationParameterUpdateMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "sync_param_update");

    private final BlockEntitySynchronizationParameter parameter;
    private final Object value;

    public BlockEntitySynchronizationParameterUpdateMessage(BlockEntitySynchronizationParameter parameter, Object value) {
        this.parameter = parameter;
        this.value = value;
    }

    public static BlockEntitySynchronizationParameterUpdateMessage decode(FriendlyByteBuf buf) {
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

        return new BlockEntitySynchronizationParameterUpdateMessage(parameter, value);
    }

    public static void handle(BlockEntitySynchronizationParameterUpdateMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof BaseContainerMenu baseContainerMenu) {
                BiConsumer consumer = message.parameter.getValueConsumer();

                if (consumer != null) {
                    consumer.accept(baseContainerMenu.getBlockEntity(), message.value);
                }
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(parameter.getId());

        parameter.getSerializer().write(buf, value);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
