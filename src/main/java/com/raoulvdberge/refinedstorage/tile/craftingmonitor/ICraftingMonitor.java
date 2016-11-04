package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;

import java.util.List;

public interface ICraftingMonitor {
    void onCancelled(ICraftingMonitorElement element);

    void onCancelledAll();

    List<ICraftingMonitorElement> getElements();
}
