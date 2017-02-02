package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.item.ItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.item.filter.Filter;
import com.raoulvdberge.refinedstorage.network.MessageWirelessCraftingMonitorViewAutomated;
import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WirelessCraftingMonitor implements ICraftingMonitor {
    private ItemStack stack;
    private int controllerDimension;
    private BlockPos controller;
    private boolean viewAutomated;

    private List<Filter> filters = new ArrayList<>();
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, new ArrayList<>(), null) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            RSUtils.writeItems(this, slot, stack.getTagCompound());

            TileController controller = getController();

            if (controller != null) {
                controller.sendCraftingMonitorUpdate();
            }
        }
    };

    public WirelessCraftingMonitor(int controllerDimension, ItemStack stack) {
        this.stack = stack;
        this.controllerDimension = controllerDimension;
        this.controller = new BlockPos(ItemWirelessCraftingMonitor.getX(stack), ItemWirelessCraftingMonitor.getY(stack), ItemWirelessCraftingMonitor.getZ(stack));
        this.viewAutomated = ItemWirelessCraftingMonitor.canViewAutomated(stack);

        if (stack.hasTagCompound()) {
            for (int i = 0; i < 4; ++i) {
                RSUtils.readItems(filter, i, stack.getTagCompound());
            }
        }
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:wireless_crafting_monitor";
    }

    @Override
    public void onCancelled(EntityPlayerMP player, int id) {
        TileController controller = getController();

        if (controller != null) {
            controller.getItemGridHandler().onCraftingCancelRequested(player, id);
        }
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeParameter() {
        return null;
    }

    @Override
    public BlockPos getNetworkPosition() {
        return controller;
    }

    @Override
    public List<ICraftingTask> getTasks() {
        TileController controller = getController();

        if (controller != null) {
            return controller.getCraftingManager().getTasks();
        }

        return Collections.emptyList();
    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }

    @Override
    public ItemHandlerBasic getFilter() {
        return filter;
    }

    @Override
    public boolean canViewAutomated() {
        return viewAutomated;
    }

    @Override
    public void onViewAutomatedChanged(boolean viewAutomated) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessCraftingMonitorViewAutomated(viewAutomated));

        this.viewAutomated = viewAutomated;
    }

    private TileController getController() {
        World world = DimensionManager.getWorld(controllerDimension);

        if (world != null) {
            TileEntity tile = world.getTileEntity(controller);

            return tile instanceof TileController ? (TileController) tile : null;
        }

        return null;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
