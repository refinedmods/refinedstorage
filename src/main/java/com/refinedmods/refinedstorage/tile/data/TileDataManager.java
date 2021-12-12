package com.refinedmods.refinedstorage.tile.data;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.network.tiledata.TileDataParameterUpdateMessage;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class TileDataManager {
    private static final Map<Integer, TileDataParameter> REGISTRY = new HashMap<>();
    private static int lastId = 0;
    private final BlockEntity tile;

    private final List<TileDataParameter> parameters = new ArrayList<>();
    private final List<TileDataParameter> watchedParameters = new ArrayList<>();

    private final List<TileDataWatcher> watchers = new CopyOnWriteArrayList<>();

    public TileDataManager(BlockEntity tile) {
        this.tile = tile;
    }

    public static void registerParameter(TileDataParameter parameter) {
        parameter.setId(lastId);

        REGISTRY.put(lastId++, parameter);
    }

    public static TileDataParameter getParameter(int id) {
        return REGISTRY.get(id);
    }

    public static void setParameter(TileDataParameter parameter, Object value) {
        RS.NETWORK_HANDLER.sendToServer(new TileDataParameterUpdateMessage(parameter, value));
    }

    public BlockEntity getTile() {
        return tile;
    }

    public void addParameter(TileDataParameter parameter) {
        parameters.add(parameter);
    }

    public List<TileDataParameter> getParameters() {
        return parameters;
    }

    public void addWatchedParameter(TileDataParameter parameter) {
        addParameter(parameter);

        watchedParameters.add(parameter);
    }

    public List<TileDataParameter> getWatchedParameters() {
        return watchedParameters;
    }

    public void addWatcher(TileDataWatcher listener) {
        watchers.add(listener);
    }

    public void removeWatcher(TileDataWatcher listener) {
        watchers.remove(listener);
    }

    public void sendParameterToWatchers(TileDataParameter parameter) {
        watchers.forEach(l -> l.sendParameter(false, parameter));
    }
}
