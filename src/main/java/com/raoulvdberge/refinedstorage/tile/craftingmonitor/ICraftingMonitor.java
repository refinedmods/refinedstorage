package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.util.List;

public interface ICraftingMonitor {
    String getGuiTitle();

    void onCancelled(EntityPlayerMP player, int id);

    TileDataParameter<Integer, ?> getRedstoneModeParameter();

    List<ICraftingTask> getTasks();

    @Nullable
    ICraftingManager getCraftingManager();

    int getSize();

    void onSizeChanged(int size);

    boolean isActive();

    void onClosed(EntityPlayer player);
}
