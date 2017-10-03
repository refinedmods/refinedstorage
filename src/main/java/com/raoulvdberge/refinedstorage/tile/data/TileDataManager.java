package com.raoulvdberge.refinedstorage.tile.data;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.network.MessageTileDataParameterUpdate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class TileDataManager {
    private static int LAST_ID = 0;
    private static Map<Integer, TileDataParameter> REGISTRY = new HashMap<>();

    private TileEntity tile;

    private List<TileDataParameter> parameters = new ArrayList<>();
    private List<TileDataParameter> watchedParameters = new ArrayList<>();

    private List<TileDataWatcher> watchers = new CopyOnWriteArrayList<>();
    private List<EntityPlayer> playersWatching = new CopyOnWriteArrayList<>();

    public TileDataManager(TileEntity tile) {
        this.tile = tile;
    }

    public TileEntity getTile() {
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

    public List<EntityPlayer> getPlayersWatching() {
        return playersWatching;
    }

    public void addWatcher(TileDataWatcher listener) {
        watchers.add(listener);
        playersWatching.add(listener.getPlayer());
    }

    public void removeWatcher(TileDataWatcher listener) {
        watchers.remove(listener);
        playersWatching.remove(listener.getPlayer());
    }

    public void sendParameterToWatchers(TileDataParameter parameter) {
        watchers.forEach(l -> l.sendParameter(parameter));
    }

    public static void registerParameter(TileDataParameter parameter) {
        parameter.setId(LAST_ID);

        REGISTRY.put(LAST_ID++, parameter);
    }

    public static TileDataParameter getParameter(int id) {
        return REGISTRY.get(id);
    }

    public static void setParameter(TileDataParameter parameter, Object value) {
        RS.INSTANCE.network.sendToServer(new MessageTileDataParameterUpdate(parameter, value));
    }
}
