package com.refinedmods.refinedstorage.network.craftingmonitor;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.grid.IGridTab;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.ICraftingMonitor;
import com.refinedmods.refinedstorage.network.ClientProxy;
import com.refinedmods.refinedstorage.screen.CraftingMonitorScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CraftingMonitorUpdateMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "crafting_monitor_update");

    private static final Logger LOGGER = LogManager.getLogger(CraftingMonitorUpdateMessage.class);

    private final List<CraftingMonitorSyncTask> tasks;

    public CraftingMonitorUpdateMessage(List<CraftingMonitorSyncTask> tasks) {
        this.tasks = tasks;
    }

    public static CraftingMonitorUpdateMessage decode(FriendlyByteBuf buf) {
        int size = buf.readInt();

        List<CraftingMonitorSyncTask> tasks = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            UUID id = buf.readUUID();

            ICraftingRequestInfo requested = null;
            try {
                requested = API.instance().createCraftingRequestInfo(buf.readNbt());
            } catch (CraftingTaskReadException e) {
                LOGGER.error("Could not create crafting request info", e);
            }

            int qty = buf.readInt();
            long executionStarted = buf.readLong();
            int percentage = buf.readInt();

            List<ICraftingMonitorElement> elements = new ArrayList<>();

            int elementCount = buf.readInt();

            for (int j = 0; j < elementCount; ++j) {
                Function<FriendlyByteBuf, ICraftingMonitorElement> factory =
                    API.instance().getCraftingMonitorElementRegistry().get(buf.readResourceLocation());

                if (factory != null) {
                    elements.add(factory.apply(buf));
                }
            }

            tasks.add(new CraftingMonitorSyncTask(id, requested, qty, executionStarted, percentage, elements));
        }

        return new CraftingMonitorUpdateMessage(tasks);
    }

    public static void handle(CraftingMonitorUpdateMessage message, PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> ClientProxy.onReceivedCraftingMonitorUpdateMessage(message));
    }

    public List<CraftingMonitorSyncTask> getTasks() {
        return tasks;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(tasks.size());

        for (CraftingMonitorSyncTask task : tasks) {
            buf.writeUUID(task.id());
            buf.writeNbt(task.requestInfo().writeToNbt());
            buf.writeInt(task.quantity());
            buf.writeLong(task.startTime());
            buf.writeInt(task.completionPercentage());

            List<ICraftingMonitorElement> elements = task.elements();

            buf.writeInt(elements.size());

            for (ICraftingMonitorElement element : elements) {
                buf.writeResourceLocation(element.getId());

                element.write(buf);
            }
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
