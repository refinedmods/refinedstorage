package com.refinedmods.refinedstorage.apiimpl.util

import com.refinedmods.refinedstorage.api.util.IFilter
import net.minecraft.item.ItemStack


class ItemFilter(override val stack: ItemStack, override val compare: Int, override val mode: Int, override val isModFilter: Boolean) : IFilter<ItemStack?>