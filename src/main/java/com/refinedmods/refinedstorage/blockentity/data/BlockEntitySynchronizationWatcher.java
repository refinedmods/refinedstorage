package com.refinedmods.refinedstorage.blockentity.data;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.network.sync.BlockEntitySynchronizationParameterMessage;
import net.minecraft.server.level.ServerPlayer;

public class BlockEntitySynchronizationWatcher {
    private final ServerPlayer player;
    private final BlockEntitySynchronizationManager manager;
    private boolean sentInitial;
    private Object[] cache;

    public BlockEntitySynchronizationWatcher(ServerPlayer player, BlockEntitySynchronizationManager manager) {
        this.player = player;
        this.manager = manager;

        if (manager != null) {
            this.manager.addWatcher(this);
            this.cache = new Object[manager.getWatchedParameters().size()];
        }
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void onClosed() {
        this.manager.removeWatcher(this);
    }

    public void detectAndSendChanges() {
        if (!sentInitial) {
            manager.getParameters().forEach(p -> sendParameter(true, p));

            sentInitial = true;
        } else {
            for (int i = 0; i < manager.getWatchedParameters().size(); ++i) {
                BlockEntitySynchronizationParameter parameter = manager.getWatchedParameters().get(i);

                Object real = parameter.getValueProducer().apply(manager.getBlockEntity());
                Object cached = cache[i];

                if (!real.equals(cached)) {
                    cache[i] = real;

                    // Avoid sending watched parameter twice (after initial packet)
                    if (cached != null) {
                        sendParameter(false, parameter);
                    }
                }
            }
        }
    }

    public void sendParameter(boolean initial, BlockEntitySynchronizationParameter parameter) {
        RS.NETWORK_HANDLER.sendTo(player, new BlockEntitySynchronizationParameterMessage(manager.getBlockEntity(), parameter, initial));
    }
}
