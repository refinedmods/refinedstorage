package com.refinedmods.refinedstorage.blockentity.craftingmonitor;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ICraftingMonitor {
    int TABS_PER_PAGE = 7;

    Component getTitle();

    void onCancelled(ServerPlayer player, @Nullable UUID id);

    BlockEntitySynchronizationParameter<Integer, ?> getRedstoneModeParameter();

    Collection<ICraftingTask> getTasks();

    @Nullable
    ICraftingManager getCraftingManager();

    boolean isActiveOnClient();

    void onClosed(Player player);

    Optional<UUID> getTabSelected();

    int getTabPage();

    void onTabSelectionChanged(Optional<UUID> taskId);

    void onTabPageChanged(int page);

    int getSlotId();
}
