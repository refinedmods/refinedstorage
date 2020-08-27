package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem

@RegisterBlock(RS.ID, MachineCasingBlock.ID)
@RegisterBlockItem(RS.ID, MachineCasingBlock.ID, "R_S_ITEM_GROUP")
class MachineCasingBlock : BaseBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES) {
    companion object{
        const val ID = "machine_casing"
    }
}