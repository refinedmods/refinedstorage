package refinedstorage.tile.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerBase;
import refinedstorage.network.MessageTileDataParameter;
import refinedstorage.network.MessageTileDataParameterUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileDataManager {
    private static int PARAMETER_ID = 0;
    private static Map<Integer, TileDataParameter> PARAMETER_MAP = new HashMap<Integer, TileDataParameter>();

    private List<TileDataParameter> parameters = new ArrayList<TileDataParameter>();

    private List<TileDataParameter> watchedParameters = new ArrayList<TileDataParameter>();
    private List<Object> watchedParametersCache = new ArrayList<Object>();

    public static TileDataParameter createParameter(DataSerializer serializer, ITileDataProducer producer) {
        return createParameter(serializer, producer, null);
    }

    public static TileDataParameter createParameter(DataSerializer serializer, ITileDataProducer producer, ITileDataConsumer consumer) {
        TileDataParameter parameter = new TileDataParameter(PARAMETER_ID++, serializer, producer, consumer);

        PARAMETER_MAP.put(parameter.getId(), parameter);

        return parameter;
    }

    public static TileDataParameter getParameter(int id) {
        return PARAMETER_MAP.get(id);
    }

    public static <T> void setParameter(TileDataParameter<T> parameter, T value) {
        RefinedStorage.INSTANCE.network.sendToServer(new MessageTileDataParameterUpdate(parameter, value));
    }

    private TileEntity tile;

    public TileDataManager(TileEntity tile) {
        this.tile = tile;
    }

    public void addParameter(TileDataParameter parameter) {
        parameters.add(parameter);
    }

    public void addWatchedParameter(TileDataParameter parameter) {
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

    public void sendParameter(EntityPlayerMP player, TileDataParameter parameter) {
        RefinedStorage.INSTANCE.network.sendTo(new MessageTileDataParameter(tile, parameter), player);
    }

    public void sendParameterToWatchers(TileDataParameter parameter) {
        for (EntityPlayer player : tile.getWorld().playerEntities) {
            Container container = player.openContainer;

            if (container instanceof ContainerBase && ((ContainerBase) container).getTile().getPos().equals(tile.getPos())) {
                sendParameter((EntityPlayerMP) player, parameter);
            }
        }
    }
}
