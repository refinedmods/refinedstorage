package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.google.common.base.Optional;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class NetworkNodeCraftingMonitor extends NetworkNode implements ICraftingMonitor {
    public static final String ID = "crafting_monitor";

    private static final String NBT_TAB_SELECTED = "TabSelected";
    private static final String NBT_TAB_PAGE = "TabPage";

    private Optional<UUID> tabSelected = Optional.absent();
    private int tabPage;

    public NetworkNodeCraftingMonitor(World world, BlockPos pos) {
        super(world, pos);
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
    public void onCancelled(EntityPlayerMP player, @Nullable UUID id) {
        if (network != null) {
            network.getItemGridHandler().onCraftingCancelRequested(player, id);
        }
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return TileCraftingMonitor.REDSTONE_MODE;
    }

    @Override
    public Collection<ICraftingTask> getTasks() {
        return network != null ? network.getCraftingManager().getTasks() : Collections.emptyList();
    }

    @Nullable
    @Override
    public ICraftingManager getCraftingManager() {
        return network != null ? network.getCraftingManager() : null;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_TAB_PAGE, tabPage);

        if (tabSelected.isPresent()) {
            tag.setUniqueId(NBT_TAB_SELECTED, tabSelected.get());
        }

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_TAB_PAGE)) {
            tabPage = tag.getInteger(NBT_TAB_PAGE);
        }

        if (tag.hasUniqueId(NBT_TAB_SELECTED)) {
            tabSelected = Optional.of(tag.getUniqueId(NBT_TAB_SELECTED));
        }
    }

    public void setTabSelected(Optional<UUID> tabSelected) {
        this.tabSelected = tabSelected;
    }

    public void setTabPage(int tabPage) {
        this.tabPage = tabPage;
    }

    @Override
    public void onClosed(EntityPlayer player) {
        // NO OP
    }

    @Override
    public Optional<UUID> getTabSelected() {
        return world.isRemote ? TileCraftingMonitor.TAB_SELECTED.getValue() : tabSelected;
    }

    @Override
    public int getTabPage() {
        return world.isRemote ? TileCraftingMonitor.TAB_PAGE.getValue() : tabPage;
    }

    @Override
    public void onTabSelectionChanged(Optional<UUID> tab) {
        TileDataManager.setParameter(TileCraftingMonitor.TAB_SELECTED, tab);
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0) {
            TileDataManager.setParameter(TileCraftingMonitor.TAB_PAGE, page);
        }
    }

    @Override
    public int getSlotId() {
        return -1;
    }
}
