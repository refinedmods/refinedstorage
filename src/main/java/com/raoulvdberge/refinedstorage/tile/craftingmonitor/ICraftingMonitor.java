package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.google.common.base.Optional;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface ICraftingMonitor {
    int TABS_PER_PAGE = 7;

    String getGuiTitle();

    void onCancelled(EntityPlayerMP player, @Nullable UUID id);

    TileDataParameter<Integer, ?> getRedstoneModeParameter();

    Collection<ICraftingTask> getTasks();

    @Nullable
    ICraftingManager getCraftingManager();

    boolean isActive();

    void onClosed(EntityPlayer player);

    Optional<UUID> getTabSelected();

    int getTabPage();

    void onTabSelectionChanged(Optional<UUID> taskId);

    void onTabPageChanged(int page);

    int getSlotId();
}
