package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeExporter;
import com.raoulvdberge.refinedstorage.container.ContainerExporter;
import com.raoulvdberge.refinedstorage.gui.GuiExporter;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataListener;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;

public class TileExporter extends TileNode {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean> REGULATOR = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileExporter>() {
        @Override
        public Boolean getValue(TileExporter tile) {
            return ((NetworkNodeExporter) tile.getNode()).isRegulator();
        }
    }, new ITileDataConsumer<Boolean, TileExporter>() {
        @Override
        public void setValue(TileExporter tile, Boolean value) {
            NetworkNodeExporter exporter = (NetworkNodeExporter) tile.getNode();

            for (int i = 0; i < exporter.getItemFilters().getSlots() + exporter.getFluidFilters().getSlots(); ++i) {
                ItemStack slot = i >= exporter.getItemFilters().getSlots() ? exporter.getFluidFilters().getStackInSlot(i - exporter.getItemFilters().getSlots()) : exporter.getItemFilters().getStackInSlot(i);

                if (!slot.isEmpty()) {
                    slot.setCount(1);
                }
            }

            exporter.setRegulator(value);
            exporter.markDirty();

            tile.getWorld().getMinecraftServer().getPlayerList().getPlayers().stream()
                    .filter(player -> player.openContainer instanceof ContainerExporter && ((ContainerExporter) player.openContainer).getTile().getPos().equals(tile.getPos()))
                    .forEach(player -> {
                        ((ContainerExporter) player.openContainer).initSlots();

                        player.openContainer.detectAndSendChanges();
                    });
        }
    }, new ITileDataListener<Boolean>() {
        @Override
        public void onChanged(TileDataParameter<Boolean> parameter) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiExporter) {
                ((ContainerExporter) ((GuiExporter) Minecraft.getMinecraft().currentScreen).inventorySlots).initSlots();
            }
        }
    });

    public TileExporter() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(REGULATOR);
    }

    @Override
    public INetworkNode createNode() {
        return new NetworkNodeExporter(this);
    }
}
