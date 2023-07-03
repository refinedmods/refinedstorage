package com.refinedmods.refinedstorage.network.sync;

import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BlockEntitySynchronizationParameterMessage {
    private final BlockEntity blockEntity;
    private final BlockEntitySynchronizationParameter parameter;
    private final boolean initial;

    public BlockEntitySynchronizationParameterMessage(BlockEntity blockEntity, BlockEntitySynchronizationParameter parameter, boolean initial) {
        this.blockEntity = blockEntity;
        this.parameter = parameter;
        this.initial = initial;
    }

    public static BlockEntitySynchronizationParameterMessage decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        boolean initial = buf.readBoolean();

        BlockEntitySynchronizationParameter parameter = BlockEntitySynchronizationManager.getParameter(id);

        if (parameter != null) {
            try {
                parameter.setValue(initial, parameter.getSerializer().read(buf));
            } catch (Exception e) {
                // NO OP
            }
        }

        return new BlockEntitySynchronizationParameterMessage(null, null, initial);
    }

    public static void encode(BlockEntitySynchronizationParameterMessage message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.parameter.getId());
        buf.writeBoolean(message.initial);

        message.parameter.getSerializer().write(buf, message.parameter.getValueProducer().apply(message.blockEntity));
    }

    public static void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
