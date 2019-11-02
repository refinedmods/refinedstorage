package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllowedTags {
    private static final String NBT_ITEM_TAGS = "ItemTags";
    private static final String NBT_FLUID_TAGS = "FluidTags";

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

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = new CompoundNBT();

        tag.put(NBT_ITEM_TAGS, getList(allowedItemTags));
        tag.put(NBT_FLUID_TAGS, getList(allowedFluidTags));

        return tag;
    }

    public void readFromNbt(CompoundNBT tag) {
        if (tag.contains(NBT_ITEM_TAGS)) {
            applyList(allowedItemTags, tag.getList(NBT_ITEM_TAGS, Constants.NBT.TAG_LIST));
        }

        if (tag.contains(NBT_FLUID_TAGS)) {
            applyList(allowedFluidTags, tag.getList(NBT_FLUID_TAGS, Constants.NBT.TAG_LIST));
        }
    }

    private ListNBT getList(List<Set<ResourceLocation>> tagsPerSlot) {
        ListNBT list = new ListNBT();

        for (Set<ResourceLocation> tags : tagsPerSlot) {
            ListNBT subList = new ListNBT();

            tags.forEach(t -> subList.add(new StringNBT(t.toString())));

            list.add(subList);
        }

        return list;
    }

    private void applyList(List<Set<ResourceLocation>> list, ListNBT tagList) {
        for (int i = 0; i < tagList.size(); ++i) {
            ListNBT subList = tagList.getList(i);

            for (int j = 0; j < subList.size(); ++j) {
                list.get(i).add(new ResourceLocation(subList.getString(j)));
            }
        }
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
