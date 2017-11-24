package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeExporter;
import com.raoulvdberge.refinedstorage.container.ContainerExporter;
import com.raoulvdberge.refinedstorage.gui.GuiExporter;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileExporter extends TileNode<NetworkNodeExporter> {
    public static final TileDataParameter<Integer, TileExporter> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileExporter> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, TileExporter> REGULATOR = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isRegulator(), (t, v) -> {
        NetworkNodeExporter exporter = t.getNode();

        for (int i = 0; i < exporter.getItemFilters().getSlots() + exporter.getFluidFilters().getSlots(); ++i) {
            ItemStack slot = i >= exporter.getItemFilters().getSlots() ? exporter.getFluidFilters().getStackInSlot(i - exporter.getItemFilters().getSlots()) : exporter.getItemFilters().getStackInSlot(i);

            if (!slot.isEmpty()) {
                slot.setCount(1);
            }
        }

        exporter.setRegulator(v);
        exporter.markDirty();

        t.world.getMinecraftServer().getPlayerList().getPlayers().stream()
            .filter(player -> player.openContainer instanceof ContainerExporter && ((ContainerExporter) player.openContainer).getTile().getPos().equals(t.getPos()))
            .forEach(player -> {
                ((ContainerExporter) player.openContainer).initSlots();

                player.openContainer.detectAndSendChanges();
            });
    }, p -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiExporter) {
            ((ContainerExporter) ((GuiExporter) Minecraft.getMinecraft().currentScreen).inventorySlots).initSlots();
        }
    });

    public TileExporter() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(REGULATOR);
    }

    @Override
    @Nonnull
    public NetworkNodeExporter createNode(World world, BlockPos pos) {
        return new NetworkNodeExporter(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeExporter.ID;
    }
}
