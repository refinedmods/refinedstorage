package com.refinedmods.refinedstorage.item.blockitem

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.NetworkType
import com.refinedmods.refinedstorage.block.ControllerBlock
import net.minecraft.item.Item
import java.util.function.Supplier

class ControllerBlockItem(block: ControllerBlock):
        EnergyBlockItem(
                block,
                Settings().group(RS.MAIN_GROUP).maxCount(1),
                block.type === NetworkType.CREATIVE,
                Supplier { RS.SERVER_CONFIG.controller.getCapacity() }
        )