package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.item.ItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.network.MessageWirelessCraftingMonitorViewAutomated;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
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

    private int networkDimension;
    private BlockPos network;

    private boolean viewAutomated;

    private List<IFilter> filters = new ArrayList<>();
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, new ArrayList<>(), null) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            StackUtils.writeItems(this, 0, stack.getTagCompound());

            INetwork network = getNetwork();

            if (network != null) {
                network.getCraftingManager().sendCraftingMonitorUpdate();
            }
        }
    };

    public WirelessCraftingMonitor(int networkDimension, ItemStack stack) {
        this.stack = stack;
        this.networkDimension = networkDimension;
        this.network = new BlockPos(ItemWirelessCraftingMonitor.getX(stack), ItemWirelessCraftingMonitor.getY(stack), ItemWirelessCraftingMonitor.getZ(stack));
        this.viewAutomated = ItemWirelessCraftingMonitor.canViewAutomated(stack);

        if (stack.hasTagCompound()) {
            StackUtils.readItems(filter, 0, stack.getTagCompound());
        }
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:wireless_crafting_monitor";
    }

    @Override
    public void onCancelled(EntityPlayerMP player, int id) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getItemGridHandler().onCraftingCancelRequested(player, id);
        }
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return null;
    }

    @Override
    public BlockPos getNetworkPosition() {
        return network;
    }

    @Override
    public List<ICraftingTask> getTasks() {
        INetwork network = getNetwork();

        if (network != null) {
            return network.getCraftingManager().getTasks();
        }

        return Collections.emptyList();
    }

    @Override
    public List<IFilter> getFilters() {
        return filters;
    }

    @Override
    public ItemHandlerBase getFilter() {
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

    private INetwork getNetwork() {
        World world = DimensionManager.getWorld(networkDimension);

        if (world != null) {
            TileEntity tile = world.getTileEntity(network);

            return tile instanceof INetwork ? (INetwork) tile : null;
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

    @Override
    public void onClosed(EntityPlayer player) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getNetworkItemHandler().onClose(player);
        }
    }
}
