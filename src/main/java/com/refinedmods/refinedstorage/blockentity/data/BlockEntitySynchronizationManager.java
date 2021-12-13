package com.refinedmods.refinedstorage.blockentity.data;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.network.sync.BlockEntitySynchronizationParamaterUpdateMessage;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockEntitySynchronizationManager {
    private static final Map<Integer, BlockEntitySynchronizationParameter> REGISTRY = new HashMap<>();
    private static int lastId = 0;
    private final BlockEntity blockEntity;

    private final List<BlockEntitySynchronizationParameter> parameters = new ArrayList<>();
    private final List<BlockEntitySynchronizationParameter> watchedParameters = new ArrayList<>();

    private final List<BlockEntitySynchronizationWatcher> watchers = new CopyOnWriteArrayList<>();

    public BlockEntitySynchronizationManager(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public static void registerParameter(BlockEntitySynchronizationParameter parameter) {
        parameter.setId(lastId);

        REGISTRY.put(lastId++, parameter);
    }

    public static BlockEntitySynchronizationParameter getParameter(int id) {
        return REGISTRY.get(id);
    }

    public static void setParameter(BlockEntitySynchronizationParameter parameter, Object value) {
        RS.NETWORK_HANDLER.sendToServer(new BlockEntitySynchronizationParamaterUpdateMessage(parameter, value));
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public void addParameter(BlockEntitySynchronizationParameter parameter) {
        parameters.add(parameter);
    }

    public List<BlockEntitySynchronizationParameter> getParameters() {
        return parameters;
    }

    public void addWatchedParameter(BlockEntitySynchronizationParameter parameter) {
        addParameter(parameter);

        watchedParameters.add(parameter);
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
}
