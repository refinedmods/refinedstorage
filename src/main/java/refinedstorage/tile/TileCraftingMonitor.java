package refinedstorage.tile;

import refinedstorage.RefinedStorage;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.RefinedStorageSerializers;
import refinedstorage.tile.data.TileDataParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TileCraftingMonitor extends TileNode {
    public static final TileDataParameter<List<ClientCraftingTask>> TASKS = new TileDataParameter<>(RefinedStorageSerializers.CLIENT_CRAFTING_TASK_SERIALIZER, new ArrayList<>(), new ITileDataProducer<List<ClientCraftingTask>, TileCraftingMonitor>() {
        @Override
        public List<ClientCraftingTask> getValue(TileCraftingMonitor tile) {
            if (tile.connected) {
                return tile.network.getCraftingTasks().stream().map(t -> new ClientCraftingTask(
                    t.getStatus(),
                    t.getPattern().getOutputs(),
                    t.getProgress(),
                    t.getChild()
                )).collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        }
    });

    public TileCraftingMonitor() {
        dataManager.addParameter(TASKS);
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.CONFIG.craftingMonitorUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
