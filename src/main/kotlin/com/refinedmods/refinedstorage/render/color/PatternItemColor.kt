package com.refinedmods.refinedstorage.render.color

import com.refinedmods.refinedstorage.item.PatternItem.Companion.fromCache
import com.refinedmods.refinedstorage.render.model.PatternBakedModel
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.ItemStack

class PatternItemColor : IItemColor {
    fun getColor(stack: ItemStack?, tintIndex: Int): Int {
        val pattern = fromCache(Minecraft.getInstance().world, stack!!)
        if (PatternBakedModel.Companion.canDisplayOutput(stack, pattern)) {
            val color: Int = Minecraft.getInstance().getItemColors().getColor(pattern!!.getOutputs().get(0), tintIndex)
            if (color != -1) {
                return color
            }
        }
        return 0xFFFFFF
    }
}