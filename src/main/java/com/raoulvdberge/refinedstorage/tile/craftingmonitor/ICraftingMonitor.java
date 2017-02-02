package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.item.filter.Filter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public interface ICraftingMonitor {
    String getGuiTitle();

    void onCancelled(EntityPlayerMP player, int id);

    TileDataParameter<Integer> getRedstoneModeParameter();

    @Nullable
    BlockPos getNetworkPosition();

    List<ICraftingTask> getTasks();

    List<Filter> getFilters();

    ItemHandlerBasic getFilter();

    boolean canViewAutomated();

    void onViewAutomatedChanged(boolean viewAutomated);

    boolean isActive();
}
