package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTaskError;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class CraftingRequester {
    private static final String NBT_TASK_ID = "TaskId";
    private static final String NBT_ALLOW_REQUEST = "AllowRequest";

    private UUID taskId;
    private INetworkNode node;
    private boolean allowRequest = true;

    public CraftingRequester(INetworkNode node) {
        this.node = node;
    }

    public void request(ItemStack stack, int quantity) {
        INetwork network = node.getNetwork();

        if (network != null) {
            if (taskId != null && network.getCraftingManager().getTask(taskId) == null) {
                this.taskId = null;

                this.node.markDirty();
            }

            // Only allow a request if we have no current task running.
            // Only allow a request if we received all items from a previous request.
            // We can only check if we received all items with a flag, because there is a delay in finishing the task
            // and actually receiving the items.
            if (taskId == null && allowRequest) {
                ICraftingTask task = network.getCraftingManager().create(stack, quantity);

                if (task != null) {
                    ICraftingTaskError error = task.calculate();

                    if (error == null) {
                        network.getCraftingManager().add(task);

                        this.allowRequest = false;
                        this.taskId = task.getId();

                        this.node.markDirty();
                    }
                }
            }
        }
    }

    public void setAllowRequest() {
        if (!allowRequest) {
            this.allowRequest = true;

            this.node.markDirty();
        }
    }

    public void readFromNbt(NBTTagCompound tag) {
        if (tag.hasUniqueId(NBT_TASK_ID)) {
            taskId = tag.getUniqueId(NBT_TASK_ID);
        }

        allowRequest = tag.getBoolean(NBT_ALLOW_REQUEST);
    }

    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();

        if (taskId != null) {
            tag.setUniqueId(NBT_TASK_ID, taskId);
        }

        tag.setBoolean(NBT_ALLOW_REQUEST, allowRequest);

        return tag;
    }
}
