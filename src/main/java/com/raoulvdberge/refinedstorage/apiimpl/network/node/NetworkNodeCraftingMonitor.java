package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.filter.Filter;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetworkNodeCraftingMonitor extends NetworkNode implements ICraftingMonitor {
    public static final String ID = "crafting_monitor";

    private static final String NBT_VIEW_AUTOMATED = "ViewAutomated";

    private boolean viewAutomated = true;
    private List<Filter> filters = new ArrayList<>();
    private ItemHandlerListenerNetworkNode filterListener = new ItemHandlerListenerNetworkNode(this);
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, new ArrayList<>(), slot -> {
        filterListener.accept(slot);

        if (network != null) {
            network.sendCraftingMonitorUpdate();
        }
    });

    public NetworkNodeCraftingMonitor(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.craftingMonitorUsage;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:crafting_monitor";
    }

    @Override
    public void onCancelled(EntityPlayerMP player, int id) {
        if (network != null) {
            network.getItemGridHandler().onCraftingCancelRequested(player, id);
        }
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeParameter() {
        return TileCraftingMonitor.REDSTONE_MODE;
    }

    @Nullable
    @Override
    public BlockPos getNetworkPosition() {
        return network != null ? network.getPosition() : null;
    }

    @Override
    public List<ICraftingTask> getTasks() {
        return network != null ? network.getCraftingManager().getTasks() : Collections.emptyList();
    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }

    public void onOpened(EntityPlayer player) {
        if (network != null) {
            network.sendCraftingMonitorUpdate((EntityPlayerMP) player);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItems(filter, 0, tag);

        tag.setBoolean(NBT_VIEW_AUTOMATED, viewAutomated);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(filter, 0, tag);

        if (tag.hasKey(NBT_VIEW_AUTOMATED)) {
            viewAutomated = tag.getBoolean(NBT_VIEW_AUTOMATED);
        }
    }

    @Override
    public boolean canViewAutomated() {
        return holder.world().isRemote ? TileCraftingMonitor.VIEW_AUTOMATED.getValue() : viewAutomated;
    }

    @Override
    public void onViewAutomatedChanged(boolean viewAutomated) {
        TileDataManager.setParameter(TileCraftingMonitor.VIEW_AUTOMATED, viewAutomated);
    }

    public void setViewAutomated(boolean viewAutomated) {
        this.viewAutomated = viewAutomated;
    }

    public ItemHandlerFilter getFilter() {
        return filter;
    }

    @Override
    public IItemHandler getDrops() {
        return filter;
    }
}
