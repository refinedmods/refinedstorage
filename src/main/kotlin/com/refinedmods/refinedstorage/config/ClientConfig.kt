package com.refinedmods.refinedstorage.config

import net.minecraftforge.common.ForgeConfigSpec

class ClientConfig {
    private val builder: ForgeConfigSpec.Builder = Builder()
    private val spec: ForgeConfigSpec
    val grid: Grid
    val crafterManager: CrafterManager
    fun getSpec(): ForgeConfigSpec {
        return spec
    }

    inner class Grid {
        private val maxRowsStretch: ForgeConfigSpec.IntValue
        private val detailedTooltip: ForgeConfigSpec.BooleanValue
        private val largeFont: ForgeConfigSpec.BooleanValue
        private val preventSortingWhileShiftIsDown: ForgeConfigSpec.BooleanValue
        fun getMaxRowsStretch(): Int {
            return maxRowsStretch.get()
        }

        fun getDetailedTooltip(): Boolean {
            return detailedTooltip.get()
        }

        fun getLargeFont(): Boolean {
            return largeFont.get()
        }

        fun getPreventSortingWhileShiftIsDown(): Boolean {
            return preventSortingWhileShiftIsDown.get()
        }

        init {
            builder.push("grid")
            maxRowsStretch = builder.comment("The maximum amount of rows that the Grid can show when stretched").defineInRange("maxRowsStretch", Int.MAX_VALUE, 3, Int.MAX_VALUE)
            detailedTooltip = builder.comment("Whether the Grid should display a detailed tooltip when hovering over an item or fluid").define("detailedTooltip", true)
            largeFont = builder.comment("Whether the Grid should use a large font for stack quantity display").define("largeFont", false)
            preventSortingWhileShiftIsDown = builder.comment("Whether the Grid should prevent sorting while the shift key is held down").define("preventSortingWhileShiftIsDown", true)
            builder.pop()
        }
    }

    inner class CrafterManager {
        private val maxRowsStretch: ForgeConfigSpec.IntValue
        fun getMaxRowsStretch(): Int {
            return maxRowsStretch.get()
        }

        init {
            builder.push("crafterManager")
            maxRowsStretch = builder.comment("The maximum amount of rows that the Crafter Manager can show when stretched").defineInRange("maxRowsStretch", Int.MAX_VALUE, 3, Int.MAX_VALUE)
            builder.pop()
        }
    }

    init {
        grid = Grid()
        crafterManager = CrafterManager()
        spec = builder.build()
    }
}