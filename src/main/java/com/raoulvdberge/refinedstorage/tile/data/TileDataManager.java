package com.raoulvdberge.refinedstorage.tile.data;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.network.MessageTileDataParameter;
import com.raoulvdberge.refinedstorage.network.MessageTileDataParameterUpdate;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileDataManager {
    private static int LAST_ID = 0;
    private static Map<Integer, TileDataParameter> REGISTRY = new HashMap<>();

    private List<TileDataParameter> parameters = new ArrayList<>();

    private List<TileDataParameter> watchedParameters = new ArrayList<>();
    private List<Object> watchedParametersCache = new ArrayList<>();

    public static void registerParameter(TileDataParameter<?> parameter) {
        parameter.setId(LAST_ID);

        REGISTRY.put(LAST_ID++, parameter);
    }

    public static TileDataParameter<?> getParameter(int id) {
        return REGISTRY.get(id);
    }

    public static <T> void setParameter(TileDataParameter<T> parameter, T value) {
        RS.INSTANCE.network.sendToServer(new MessageTileDataParameterUpdate(parameter, value));
    }

    private TileEntity tile;

    public TileDataManager(TileEntity tile) {
        this.tile = tile;
    }

    public void addParameter(TileDataParameter<?> parameter) {
        parameters.add(parameter);
    }

    public List<TileDataParameter> getParameters() {
        return parameters;
    }

    public void addWatchedParameter(TileDataParameter<?> parameter) {
        addParameter(parameter);

        watchedParameters.add(parameter);
        watchedParametersCache.add(null);
    }

    public void detectAndSendChanges() {
        for (int i = 0; i < watchedParameters.size(); ++i) {
            TileDataParameter parameter = watchedParameters.get(i);

            Object real = parameter.getValueProducer().getValue(tile);
            Object cached = watchedParametersCache.get(i);

            if (!real.equals(cached)) {
                watchedParametersCache.set(i, real);

                sendParameterToWatchers(parameter);
            }
        }
    }

    public void sendParametersTo(EntityPlayerMP player) {
        for (TileDataParameter parameter : parameters) {
            sendParameter(player, parameter);
        }
    }

    public void sendParameter(EntityPlayerMP player, TileDataParameter<?> parameter) {
        RS.INSTANCE.network.sendTo(new MessageTileDataParameter(tile, parameter), player);
    }

    public void sendParameterToWatchers(TileDataParameter<?> parameter) {
        for (EntityPlayer player : tile.getWorld().playerEntities) {
            Container container = player.openContainer;

            if (container instanceof ContainerBase) {
                TileBase tile = ((ContainerBase) container).getTile();

                if (tile != null && tile.getPos().equals(this.tile.getPos())) {
                    sendParameter((EntityPlayerMP) player, parameter);
                }
            }
        }
    }
}
