package com.refinedmods.refinedstorage.config

import reborncore.common.config.Config

open class ClientConfig {
    companion object {
        // Grid
        @JvmField
        @Config(config = "client", category = "grid", key = "maxRowsStretch", comment = "The maximum amount of rows that the Grid can show when stretched")
        var gridMaxRowsStretch: Int = Int.MAX_VALUE

        @JvmField
        @Config(config = "client", category = "grid", key = "detailedTooltip", comment = "Whether the Grid should display a detailed tooltip when hovering over an item or fluid")
        var gridDetailedTooltip: Boolean = true

        @JvmField
        @Config(config = "client", category = "grid", key = "largeFont", comment = "Whether the Grid should use a large font for stack quantity display")
        var gridLargeFont: Boolean = false

        @JvmField
        @Config(config = "client", category = "grid", key = "preventSortingWhileShiftIsDown", comment = "Whether the Grid should prevent sorting while the shift key is held down")
        var gridPreventSortingWhileShiftIsDown: Boolean = true

        // Crafter Manager
        @JvmField
        @Config(config = "client", category = "crafterManager", key = "maxRowsStretch", comment = "The maximum amount of rows that the Crafter Manager can show when stretched")
        var crafterManagerMaxRowsStretch: Int = Int.MAX_VALUE
    }
}