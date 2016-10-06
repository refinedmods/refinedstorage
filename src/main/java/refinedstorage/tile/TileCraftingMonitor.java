package refinedstorage.tile;

import refinedstorage.RefinedStorage;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.RefinedStorageSerializers;
import refinedstorage.tile.data.TileDataParameter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TileCraftingMonitor extends TileNode {
    public static final TileDataParameter<List<ClientCraftingTask>> TASKS = new TileDataParameter<>(RefinedStorageSerializers.CLIENT_CRAFTING_TASK_SERIALIZER, Collections.emptyList(), new ITileDataProducer<List<ClientCraftingTask>, TileCraftingMonitor>() {
        @Override
        public List<ClientCraftingTask> getValue(TileCraftingMonitor tile) {
            if (tile.connected) {
                List<ClientCraftingTask> tasks = tile.network.getCraftingTasks().stream().map(t -> new ClientCraftingTask(
                        t.getPattern().getOutputs().get(0),
                        t.getQuantity()
                )).collect(Collectors.toList());

                return tasks;
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
        return RefinedStorage.INSTANCE.config.craftingMonitorUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
