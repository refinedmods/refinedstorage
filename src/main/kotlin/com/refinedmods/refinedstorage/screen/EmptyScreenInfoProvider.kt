package com.refinedmods.refinedstorage.screen

class EmptyScreenInfoProvider : IScreenInfoProvider {
    override fun getVisibleRows(): Int {
        return 3
    }

    override fun getRows(): Int {
        return 0
    }

    override fun getCurrentOffset(): Int {
        return 0
    }

    override fun getSearchFieldText(): String? {
        return ""
    }

    override fun getTopHeight(): Int {
        return 0
    }

    override fun getBottomHeight(): Int {
        return 0
    }

    override fun getYPlayerInventory(): Int {
        return 0
    }
}