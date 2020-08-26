package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock

@RegisterBlock(RS.ID, MachineCasingBlock.ID)
class MachineCasingBlock : BaseBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES) {
    companion object{
        const val ID = "machine_casing"
    }
}