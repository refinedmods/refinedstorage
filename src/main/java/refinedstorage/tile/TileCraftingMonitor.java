package refinedstorage.tile;

import refinedstorage.RefinedStorage;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.RefinedStorageSerializers;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TileCraftingMonitor extends TileNode {
    public static final TileDataParameter<List<ClientCraftingTask>> TASKS = TileDataManager.createParameter(RefinedStorageSerializers.CLIENT_CRAFTING_TASK_SERIALIZER, new ArrayList<>(), new ITileDataProducer<List<ClientCraftingTask>, TileCraftingMonitor>() {
        @Override
        public List<ClientCraftingTask> getValue(TileCraftingMonitor tile) {
            if (tile.connected) {
                List<ClientCraftingTask> tasks = tile.network.getCraftingTasks().stream().map(t -> new ClientCraftingTask(
                    t.getInfo(),
                    t.getPattern().getOutputs()
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
        return RefinedStorage.INSTANCE.craftingMonitorUsage;
    }

    @Override
    public void updateNode() {
    }
}
