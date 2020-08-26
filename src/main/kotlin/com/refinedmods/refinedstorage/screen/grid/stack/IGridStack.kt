package com.refinedmods.refinedstorage.screen.grid.stack

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.util.text.Text
import java.util.*

interface IGridStack {
    val id: UUID?

    @get:Nullable
    val otherId: UUID?
    fun updateOtherId(@Nullable otherId: UUID?)
    val name: String?
    val modId: String
    val modName: String
    val tags: Set<String?>
    val tooltip: List<Any>?
    val quantity: Int
    val formattedFullQuantity: String?
    fun draw(matrixStack: MatrixStack?, screen: BaseScreen<*>?, x: Int, y: Int)
    val ingredient: Any

    @get:Nullable
    var trackerEntry: StorageTrackerEntry?
    val isCraftable: Boolean
}