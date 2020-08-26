package com.refinedmods.refinedstorage.integration.craftingtweaks

import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.container.slot.grid.CraftingGridSlot
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.fml.InterModComms
import net.minecraftforge.fml.ModList
import java.util.function.Function
import java.util.function.Predicate

object CraftingTweaksIntegration {
    private const val ID = "craftingtweaks"
    val isLoaded: Boolean
        get() = ModList.get().isLoaded(ID)

    @JvmStatic
    fun isCraftingTweaksClass(clazz: Class<*>): Boolean {
        return clazz.name.startsWith("net.blay09.mods.craftingtweaks")
    }

    @JvmStatic
    fun register() {
        val tag = CompoundTag()
        tag.putString("ContainerClass", GridContainer::class.java.name)
        tag.putString("ValidContainerPredicate", ValidContainerPredicate::class.java.name)
        tag.putString("GetGridStartFunction", GetGridStartFunction::class.java.name)
        tag.putString("AlignToGrid", "left")
        InterModComms.sendTo(ID, "RegisterProvider", { tag })
    }

    class ValidContainerPredicate : Predicate<GridContainer> {
        override fun test(containerGrid: GridContainer): Boolean {
            return containerGrid.grid!!.gridType === GridType.CRAFTING
        }
    }

    class GetGridStartFunction : Function<GridContainer, Int> {
        override fun apply(containerGrid: GridContainer): Int {
            for (i in 0 until containerGrid.inventorySlots.size()) {
                if (containerGrid.inventorySlots.get(i) is CraftingGridSlot) {
                    return i
                }
            }
            return 0
        }
    }
}