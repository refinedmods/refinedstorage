package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World

class UpgradeItem(val type: Type) : Item(Properties().group(RS.MAIN_GROUP)) {
    enum class Type(override val name: String) {
        NORMAL("normal"), SPEED("speed"), RANGE("range"), CRAFTING("crafting"), STACK("stack"), SILK_TOUCH("silk_touch"), FORTUNE_1("fortune_1"), FORTUNE_2("fortune_2"), FORTUNE_3("fortune_3"), REGULATOR("regulator");

        val energyUsage: Int
            get() = when (this) {
                NORMAL -> 0
                RANGE -> RS.SERVER_CONFIG.upgrades.getRangeUpgradeUsage()
                SPEED -> RS.SERVER_CONFIG.upgrades.getSpeedUpgradeUsage()
                CRAFTING -> RS.SERVER_CONFIG.upgrades.getCraftingUpgradeUsage()
                STACK -> RS.SERVER_CONFIG.upgrades.getStackUpgradeUsage()
                SILK_TOUCH -> RS.SERVER_CONFIG.upgrades.getSilkTouchUpgradeUsage()
                FORTUNE_1 -> RS.SERVER_CONFIG.upgrades.getFortune1UpgradeUsage()
                FORTUNE_2 -> RS.SERVER_CONFIG.upgrades.getFortune2UpgradeUsage()
                FORTUNE_3 -> RS.SERVER_CONFIG.upgrades.getFortune3UpgradeUsage()
                REGULATOR -> RS.SERVER_CONFIG.upgrades.getRegulatorUpgradeUsage()
                else -> throw IllegalStateException("What even am I?")
            }
        val fortuneLevel: Int
            get() = when (this) {
                FORTUNE_1 -> 1
                FORTUNE_2 -> 2
                FORTUNE_3 -> 3
                else -> 0
            }
    }

    fun addInformation(stack: ItemStack?, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag?) {
        super.addInformation(stack, world, tooltip, flag)
        if (type.fortuneLevel > 0) {
            tooltip.add(
                    TranslationTextComponent("enchantment.minecraft.fortune")
                            .appendString(" ")
                            .append(TranslationTextComponent("enchantment.level." + type.fortuneLevel))
                            .setStyle(Styles.GRAY)
            )
        }
    }

    fun hasEffect(stack: ItemStack?): Boolean {
        return type == Type.SILK_TOUCH || type == Type.FORTUNE_1 || type == Type.FORTUNE_2 || type == Type.FORTUNE_3
    }

    init {
        this.setRegistryName(RS.ID, if (type == Type.NORMAL) "upgrade" else type.name + "_upgrade")
    }
}