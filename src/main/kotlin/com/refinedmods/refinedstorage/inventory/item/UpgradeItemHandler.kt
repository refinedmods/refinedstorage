package com.refinedmods.refinedstorage.inventory.item

import com.refinedmods.refinedstorage.inventory.item.validator.UpgradeItemValidator
import com.refinedmods.refinedstorage.item.UpgradeItem
import net.minecraft.item.ItemStack

open class UpgradeItemHandler(size: Int, vararg supportedUpgrades: UpgradeItem.Type) : BaseItemHandler(size) {
    val speed: Int
        get() = getSpeed(9, 2)

    fun getSpeed(speed: Int, speedIncrease: Int): Int {
        var speed = speed
        for (i in 0 until getSlots()) {
            val slot: ItemStack = getStackInSlot(i)
            if (slot.item is UpgradeItem && (slot.item as UpgradeItem).type == UpgradeItem.Type.SPEED) {
                speed -= speedIncrease
            }
        }
        return speed
    }

    fun hasUpgrade(type: UpgradeItem.Type): Boolean {
        for (i in 0 until getSlots()) {
            val slot: ItemStack = getStackInSlot(i)
            if (slot.item is UpgradeItem && (slot.item as UpgradeItem).type == type) {
                return true
            }
        }
        return false
    }

    fun getUpgradeCount(type: UpgradeItem.Type): Int {
        var upgrades = 0
        for (i in 0 until getSlots()) {
            val slot: ItemStack = getStackInSlot(i)
            if (slot.item is UpgradeItem && (slot.item as UpgradeItem).type == type) {
                upgrades++
            }
        }
        return upgrades
    }

    val energyUsage: Int
        get() {
            var usage = 0
            for (i in 0 until getSlots()) {
                val slot: ItemStack = getStackInSlot(i)
                if (slot.item is UpgradeItem) {
                    usage += (slot.item as UpgradeItem).type.energyUsage
                }
            }
            return usage
        }
    open val stackInteractCount: Int
        get() = if (hasUpgrade(UpgradeItem.Type.STACK)) 64 else 1

    fun getSlotLimit(slot: Int): Int {
        return 1
    }

    init {
        for (supportedUpgrade in supportedUpgrades) {
            addValidator(UpgradeItemValidator(supportedUpgrade))
        }
    }
}