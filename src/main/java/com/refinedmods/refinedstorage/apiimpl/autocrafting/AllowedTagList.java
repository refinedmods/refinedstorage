package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllowedTagList {
    private static final String NBT_ALLOWED_ITEM_TAGS = "AllowedItemTags";
    private static final String NBT_ALLOWED_FLUID_TAGS = "AllowedFluidTags";
    @Nullable
    private final Runnable listener;
    private List<Set<ResourceLocation>> allowedItemTags = new ArrayList<>();
    private List<Set<ResourceLocation>> allowedFluidTags = new ArrayList<>();

    public AllowedTagList(@Nullable Runnable listener, int size) {
        for (int i = 0; i < size; ++i) {
            allowedItemTags.add(new HashSet<>());
            allowedFluidTags.add(new HashSet<>());
        }

        this.listener = listener;
    }

    public CompoundTag writeToNbt() {
        CompoundTag tag = new CompoundTag();

        tag.put(NBT_ALLOWED_ITEM_TAGS, getList(allowedItemTags));
        tag.put(NBT_ALLOWED_FLUID_TAGS, getList(allowedFluidTags));

        return tag;
    }

    public void readFromNbt(CompoundTag tag) {
        if (tag.contains(NBT_ALLOWED_ITEM_TAGS)) {
            applyList(allowedItemTags, tag.getList(NBT_ALLOWED_ITEM_TAGS, Tag.TAG_LIST));
        }

        if (tag.contains(NBT_ALLOWED_FLUID_TAGS)) {
            applyList(allowedFluidTags, tag.getList(NBT_ALLOWED_FLUID_TAGS, Tag.TAG_LIST));
        }
    }

    private ListTag getList(List<Set<ResourceLocation>> tagsPerSlot) {
        ListTag list = new ListTag();

        for (Set<ResourceLocation> tags : tagsPerSlot) {
            ListTag subList = new ListTag();

            tags.forEach(t -> subList.add(StringTag.valueOf(t.toString())));

            list.add(subList);
        }

        return list;
    }

    private void applyList(List<Set<ResourceLocation>> list, ListTag tagList) {
        for (int i = 0; i < tagList.size(); ++i) {
            ListTag subList = tagList.getList(i);

            for (int j = 0; j < subList.size(); ++j) {
                list.get(i).add(new ResourceLocation(subList.getString(j)));
            }
        }
    }

    public List<Set<ResourceLocation>> getAllowedItemTags() {
        return allowedItemTags;
    }

    public void setAllowedItemTags(List<Set<ResourceLocation>> allowedItemTags) {
        this.allowedItemTags = allowedItemTags;

        notifyListener();
    }

    public List<Set<ResourceLocation>> getAllowedFluidTags() {
        return allowedFluidTags;
    }

    public void setAllowedFluidTags(List<Set<ResourceLocation>> allowedFluidTags) {
        this.allowedFluidTags = allowedFluidTags;

        notifyListener();
    }

    public void setAllowedItemTags(int slot, Set<ResourceLocation> allowedItemTags) {
        this.allowedItemTags.set(slot, allowedItemTags);

        notifyListener();
    }

    public void setAllowedFluidTags(int slot, Set<ResourceLocation> allowedFluidTags) {
        this.allowedFluidTags.set(slot, allowedFluidTags);

        notifyListener();
    }

    public void clearItemTags(int slot) {
        this.allowedItemTags.get(slot).clear();

        notifyListener();
    }

    public void clearFluidTags(int slot) {
        this.allowedFluidTags.get(slot).clear();

        notifyListener();
    }

    private void notifyListener() {
        if (listener != null) {
            listener.run();
        }
    }
}
