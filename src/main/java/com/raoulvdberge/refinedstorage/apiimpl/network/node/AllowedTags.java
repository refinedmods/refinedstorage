package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllowedTags {
    private List<Set<ResourceLocation>> allowedItemTags = new ArrayList<>();
    private List<Set<ResourceLocation>> allowedFluidTags = new ArrayList<>();

    private Runnable listener;

    public AllowedTags(Runnable listener) {
        for (int i = 0; i < 9; ++i) {
            allowedItemTags.add(new HashSet<>());
            allowedFluidTags.add(new HashSet<>());
        }
        this.listener = listener;
    }

    public List<Set<ResourceLocation>> getAllowedItemTags() {
        return allowedItemTags;
    }

    public List<Set<ResourceLocation>> getAllowedFluidTags() {
        return allowedFluidTags;
    }

    public void setAllowedItemTags(List<Set<ResourceLocation>> allowedItemTags) {
        this.allowedItemTags = allowedItemTags;
        this.listener.run();
    }

    public void setAllowedFluidTags(List<Set<ResourceLocation>> allowedFluidTags) {
        this.allowedFluidTags = allowedFluidTags;
        this.listener.run();
    }

    public void clearItemTags(int slot) {
        this.allowedItemTags.get(slot).clear();
        this.listener.run();
    }

    public void clearFluidTags(int slot) {
        this.allowedFluidTags.get(slot).clear();
        this.listener.run();
    }
}
