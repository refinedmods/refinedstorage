package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TileCraftingMonitor extends TileNode {
    public static final TileDataParameter<List<ICraftingMonitorElement>> ELEMENTS = new TileDataParameter<>(RSSerializers.CRAFTING_MONITOR_ELEMENT_SERIALIZER, Collections.emptyList(), new ITileDataProducer<List<ICraftingMonitorElement>, TileCraftingMonitor>() {
        @Override
        public List<ICraftingMonitorElement> getValue(TileCraftingMonitor tile) {
            if (tile.connected) {
                return tile.network.getCraftingTasks().stream().flatMap(t -> t.getCraftingMonitorElements().stream()).collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        }
    });

    public TileCraftingMonitor() {
        dataManager.addParameter(ELEMENTS);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.craftingMonitorUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
