package com.refinedmods.refinedstorage.network.sync;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class BlockEntitySynchronizationParameterMessage<T> implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "sync_param");

    private final BlockEntitySynchronizationParameter<T, ?> parameter;
    private final T value;
    private final boolean initial;

    public BlockEntitySynchronizationParameterMessage(BlockEntitySynchronizationParameter parameter,
                                                      T value,
                                                      boolean initial) {
        this.parameter = parameter;
        this.value = value;
        this.initial = initial;
    }

    public static BlockEntitySynchronizationParameterMessage decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        boolean initial = buf.readBoolean();
        BlockEntitySynchronizationParameter parameter = BlockEntitySynchronizationManager.getParameter(id);
        return new BlockEntitySynchronizationParameterMessage(parameter, parameter.getSerializer().read(buf), initial);
    }

    public static <T> void handle(BlockEntitySynchronizationParameterMessage<T> msg, PlayPayloadContext ctx) {
        msg.parameter.setValue(msg.initial, msg.value);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(parameter.getId());
        buf.writeBoolean(initial);
        parameter.getSerializer().write(buf, value);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
