package com.refinedmods.refinedstorage.blockentity.data;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.network.sync.BlockEntitySynchronizationParameterUpdateMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockEntitySynchronizationManager {
    private static final Map<ResourceLocation, BlockEntitySynchronizationParameter> REGISTRY = new HashMap<>();

    private final BlockEntity blockEntity;
    private final List<BlockEntitySynchronizationParameter> parameters;
    private final List<BlockEntitySynchronizationParameter> watchedParameters;
    private final List<BlockEntitySynchronizationWatcher> watchers = new CopyOnWriteArrayList<>();

    public BlockEntitySynchronizationManager(BlockEntity blockEntity, BlockEntitySynchronizationSpec spec) {
        this.blockEntity = blockEntity;
        this.parameters = spec.getParameters();
        this.watchedParameters = spec.getWatchedParameters();
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public List<BlockEntitySynchronizationParameter> getParameters() {
        return parameters;
    }

    public List<BlockEntitySynchronizationParameter> getWatchedParameters() {
        return watchedParameters;
    }

    public void addWatcher(BlockEntitySynchronizationWatcher listener) {
        watchers.add(listener);
    }

    public void removeWatcher(BlockEntitySynchronizationWatcher listener) {
        watchers.remove(listener);
    }

    public void sendParameterToWatchers(BlockEntitySynchronizationParameter parameter) {
        watchers.forEach(l -> l.sendParameter(false, parameter));
    }

    // Synchronized so we don't conflict with addons that reuse this register method in parallel.
    public synchronized static void registerParameter(BlockEntitySynchronizationParameter parameter) {
        REGISTRY.put(parameter.getId(), parameter);
    }

    public static BlockEntitySynchronizationParameter getParameter(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static void setParameter(BlockEntitySynchronizationParameter parameter, Object value) {
        RS.NETWORK_HANDLER.sendToServer(new BlockEntitySynchronizationParameterUpdateMessage(parameter, value));
    }
}
