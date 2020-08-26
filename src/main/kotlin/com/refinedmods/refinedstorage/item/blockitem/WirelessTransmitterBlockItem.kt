package com.refinedmods.refinedstorage.item.blockitem

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSBlocks
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World

class WirelessTransmitterBlockItem : BaseBlockItem(RSBlocks.WIRELESS_TRANSMITTER, Properties().group(RS.MAIN_GROUP)) {
    fun addInformation(stack: ItemStack?, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag?) {
        super.addInformation(stack, world, tooltip, flag)
        tooltip.add(TranslationTextComponent("block.refinedstorage.wireless_transmitter.tooltip", TranslationTextComponent("block.refinedstorage.cable")).setStyle(Styles.GRAY))
    }

    init {
        this.setRegistryName(RS.ID, "wireless_transmitter")
    }
}