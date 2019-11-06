package com.raoulvdberge.refinedstorage.network.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.network.ClientProxy;
import com.raoulvdberge.refinedstorage.screen.CraftingMonitorScreen;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class CraftingMonitorUpdateMessage {
    private static final Logger LOGGER = LogManager.getLogger(CraftingMonitorUpdateMessage.class);

    private ICraftingMonitor craftingMonitor;

    private List<IGridTab> tasks = new ArrayList<>();

    public CraftingMonitorUpdateMessage(ICraftingMonitor craftingMonitor) {
        this.craftingMonitor = craftingMonitor;
    }

    public CraftingMonitorUpdateMessage(List<IGridTab> tasks) {
        this.tasks = tasks;
    }

    public List<IGridTab> getTasks() {
        return tasks;
    }

    public static CraftingMonitorUpdateMessage decode(PacketBuffer buf) {
        int size = buf.readInt();

        List<IGridTab> tasks = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            UUID id = buf.readUniqueId();

            ICraftingRequestInfo requested = null;
            try {
                requested = API.instance().createCraftingRequestInfo(buf.readCompoundTag());
            } catch (CraftingTaskReadException e) {
                LOGGER.error("Could not create crafting request info", e);
            }

            int qty = buf.readInt();
            long executionStarted = buf.readLong();
            int percentage = buf.readInt();

            List<ICraftingMonitorElement> elements = new ArrayList<>();

            int elementCount = buf.readInt();

            for (int j = 0; j < elementCount; ++j) {
                Function<PacketBuffer, ICraftingMonitorElement> factory = API.instance().getCraftingMonitorElementRegistry().get(buf.readResourceLocation());

                if (factory != null) {
                    elements.add(factory.apply(buf));
                }
            }

            tasks.add(new CraftingMonitorScreen.Task(id, requested, qty, executionStarted, percentage, elements));
        }

        return new CraftingMonitorUpdateMessage(tasks);
    }

    public static void encode(CraftingMonitorUpdateMessage message, PacketBuffer buf) {
        buf.writeInt(message.craftingMonitor.getTasks().size());

        for (ICraftingTask task : message.craftingMonitor.getTasks()) {
            buf.writeUniqueId(task.getId());
            buf.writeCompoundTag(task.getRequested().writeToNbt());
            buf.writeInt(task.getQuantity());
            buf.writeLong(task.getExecutionStarted());
            buf.writeInt(task.getCompletionPercentage());

            List<ICraftingMonitorElement> elements = task.getCraftingMonitorElements();

            buf.writeInt(elements.size());

            for (ICraftingMonitorElement element : elements) {
                buf.writeResourceLocation(element.getId());

                element.write(buf);
            }
        }
    }

    public static void handle(CraftingMonitorUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientProxy.onReceivedCraftingMonitorUpdateMessage(message));
        ctx.get().setPacketHandled(true);
    }
}
