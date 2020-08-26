package com.refinedmods.refinedstorage.render.model

import com.google.common.collect.ImmutableList
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPattern
import com.refinedmods.refinedstorage.item.PatternItem.Companion.fromCache
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.ItemOverride
import net.minecraft.client.renderer.model.ItemOverrideList
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class PatternBakedModel(base: IBakedModel) : DelegateBakedModel(base) {
    override val overrides: ItemOverrideList
        get() = object : ItemOverrideList() {
            @Nullable
            fun func_239290_a_(model: IBakedModel?, stack: ItemStack?, @Nullable world: ClientWorld?, @Nullable entity: LivingEntity?): IBakedModel {
                if (entity != null) {
                    val pattern = fromCache(entity.world, stack!!)
                    if (canDisplayOutput(stack, pattern)) {
                        val outputToRender: ItemStack = pattern!!.getOutputs().get(0)
                        return Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(outputToRender, world, entity)
                    }
                }
                return super.func_239290_a_(model, stack, world, entity)
            }

            val overrides: ImmutableList<Any>
                get() = base.getOverrides().getOverrides()
        }

    companion object {
        fun canDisplayOutput(patternStack: ItemStack?, pattern: CraftingPattern?): Boolean {
            if (pattern!!.isValid() && pattern.getOutputs().size() === 1) {
                for (renderHandler in instance().getPatternRenderHandlers()!!) {
                    if (renderHandler!!.canRenderOutput(patternStack)) {
                        return true
                    }
                }
            }
            return false
        }
    }
}