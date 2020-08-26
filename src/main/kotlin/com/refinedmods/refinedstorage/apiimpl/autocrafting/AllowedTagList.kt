package com.refinedmods.refinedstorage.apiimpl.autocrafting

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringNBT
import net.minecraft.util.Identifier
import net.minecraftforge.common.util.Constants
import java.util.*
import java.util.function.Consumer


class AllowedTagList(@Nullable listener: Runnable?) {
    private var allowedItemTags: MutableList<MutableSet<Identifier>> = ArrayList<MutableSet<Identifier>>()
    private var allowedFluidTags: MutableList<MutableSet<Identifier>> = ArrayList<MutableSet<Identifier>>()

    @Nullable
    private val listener: Runnable?
    fun writeToNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.put(NBT_ALLOWED_ITEM_TAGS, getList(allowedItemTags))
        tag.put(NBT_ALLOWED_FLUID_TAGS, getList(allowedFluidTags))
        return tag
    }

    fun readFromNbt(tag: CompoundTag) {
        if (tag.contains(NBT_ALLOWED_ITEM_TAGS)) {
            applyList(allowedItemTags, tag.getList(NBT_ALLOWED_ITEM_TAGS, Constants.NBT.TAG_LIST))
        }
        if (tag.contains(NBT_ALLOWED_FLUID_TAGS)) {
            applyList(allowedFluidTags, tag.getList(NBT_ALLOWED_FLUID_TAGS, Constants.NBT.TAG_LIST))
        }
    }

    private fun getList(tagsPerSlot: List<MutableSet<Identifier>>): ListTag {
        val list = ListTag()
        for (tags in tagsPerSlot) {
            val subList = ListTag()
            tags.forEach(Consumer<Identifier> { t: Identifier -> subList.add(StringNBT.valueOf(t.toString())) })
            list.add(subList)
        }
        return list
    }

    private fun applyList(list: List<MutableSet<Identifier>>, tagList: ListTag) {
        for (i in tagList.indices) {
            val subList = tagList.getList(i)
            for (j in subList.indices) {
                list[i].add(Identifier(subList.getString(j)))
            }
        }
    }

    fun getAllowedItemTags(): List<MutableSet<Identifier>> {
        return allowedItemTags
    }

    fun getAllowedFluidTags(): List<MutableSet<Identifier>> {
        return allowedFluidTags
    }

    fun setAllowedItemTags(allowedItemTags: MutableList<MutableSet<Identifier>>) {
        this.allowedItemTags = allowedItemTags
        notifyListener()
    }

    fun setAllowedFluidTags(allowedFluidTags: MutableList<MutableSet<Identifier>>) {
        this.allowedFluidTags = allowedFluidTags
        notifyListener()
    }

    fun clearItemTags(slot: Int) {
        allowedItemTags[slot].clear()
        notifyListener()
    }

    fun clearFluidTags(slot: Int) {
        allowedFluidTags[slot].clear()
        notifyListener()
    }

    private fun notifyListener() {
        listener?.run()
    }

    companion object {
        private const val NBT_ALLOWED_ITEM_TAGS = "AllowedItemTags"
        private const val NBT_ALLOWED_FLUID_TAGS = "AllowedFluidTags"
    }

    init {
        for (i in 0..8) {
            allowedItemTags.add(HashSet<Identifier>())
            allowedFluidTags.add(HashSet<Identifier>())
        }
        this.listener = listener
    }
}