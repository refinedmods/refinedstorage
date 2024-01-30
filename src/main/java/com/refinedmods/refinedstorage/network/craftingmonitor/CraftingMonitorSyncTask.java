package com.refinedmods.refinedstorage.network.craftingmonitor;

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;

import java.util.List;
import java.util.UUID;

public record CraftingMonitorSyncTask(UUID id,
                                      ICraftingRequestInfo requestInfo,
                                      int quantity,
                                      long startTime,
                                      int completionPercentage,
                                      List<ICraftingMonitorElement> elements) {
}
