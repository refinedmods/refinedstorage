package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock

@RegisterBlock(RS.ID, QuartzEnrichedIronBlock.ID)
class QuartzEnrichedIronBlock : BaseBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES) {
    companion object {
        const val ID = "quartz_enriched_iron_block"
    }
}