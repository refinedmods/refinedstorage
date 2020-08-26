package com.refinedmods.refinedstorage.screen.grid.stack

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.render.RenderSettings
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.tags.ItemTags
import net.minecraft.util.Identifier
import net.minecraft.util.text.Text
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModList
import org.apache.logging.log4j.LogManager
import java.util.*

class ItemGridStack : IGridStack {
    private val logger = LogManager.getLogger(javaClass)
    override var id: UUID? = null
        private set

    @get:Nullable
    @Nullable
    override var otherId: UUID? = null
        private set
    val stack: ItemStack
    override var isCraftable = false
        private set

    @get:Nullable
    @Nullable
    override var trackerEntry: StorageTrackerEntry? = null
    private var zeroed = false
    private var cachedTags: MutableSet<String?>? = null
    private var cachedName: String? = null
    private var cachedModId: String? = null
    private var cachedModName: String? = null
    private var cachedTooltip: MutableList<Text>? = null

    constructor(stack: ItemStack) {
        this.stack = stack
    }

    constructor(id: UUID?, @Nullable otherId: UUID?, stack: ItemStack, craftable: Boolean, entry: StorageTrackerEntry?) {
        this.id = id
        this.otherId = otherId
        this.stack = stack
        isCraftable = craftable
        trackerEntry = entry
    }

    fun setZeroed(zeroed: Boolean) {
        this.zeroed = zeroed
    }

    override fun updateOtherId(@Nullable otherId: UUID?) {
        this.otherId = otherId
    }

    override val name: String?
        get() {
            if (cachedName == null) {
                cachedName = try {
                    stack.getDisplayName().getString()
                } catch (t: Throwable) {
                    logger.warn("Could not retrieve item name of " + stack.item.toString(), t)
                    "<Error>"
                }
            }
            return cachedName
        }
    override val modId: String
        get() {
            if (cachedModId == null) {
                cachedModId = stack.item.getCreatorModId(stack)
                if (cachedModId == null) {
                    cachedModId = "<Error>"
                }
            }
            return cachedModId!!
        }
    override val modName: String
        get() {
            if (cachedModName == null) {
                cachedModName = getModNameByModId(modId)
                if (cachedModName == null) {
                    cachedModName = "<Error>"
                }
            }
            return cachedModName!!
        }
    override val tags: Set<String?>
        get() {
            if (cachedTags == null) {
                cachedTags = HashSet()
                for (owningTag in ItemTags.getCollection().getOwningTags(stack.item)) {
                    cachedTags.add(owningTag.getPath())
                }
            }
            return cachedTags!!
        }
    override val tooltip: List<Any>?
        get() {
            if (cachedTooltip == null) {
                try {
                    cachedTooltip = RenderUtils.getTooltipFromItem(stack)
                } catch (t: Throwable) {
                    logger.warn("Could not retrieve item tooltip of " + stack.item.toString(), t)
                    cachedTooltip = ArrayList<Text>()
                    cachedTooltip.add(StringTextComponent("<Error>"))
                }
            }
            return cachedTooltip
        }

    // The isCraftable check is needed so sorting is applied correctly
    override val quantity: Int
        get() =// The isCraftable check is needed so sorting is applied correctly
            if (isCraftable) 0 else stack.count
    override val formattedFullQuantity: String?
        get() = if (zeroed) {
            "0"
        } else instance().getQuantityFormatter()!!.format(quantity)

    override fun draw(matrixStack: MatrixStack?, screen: BaseScreen<*>, x: Int, y: Int) {
        var text: String? = null
        var color = RenderSettings.INSTANCE.secondaryColor
        if (zeroed) {
            text = "0"
            color = 16733525
        } else if (isCraftable) {
            text = I18n.format("gui.refinedstorage.grid.craft")
        } else if (stack.count > 1) {
            text = instance().getQuantityFormatter()!!.formatWithUnits(quantity)
        }
        screen.renderItem(matrixStack, x, y, stack, true, text, color)
    }

    override val ingredient: Any
        get() = stack

    companion object {
        @Nullable
        fun getModNameByModId(modId: String?): String? {
            val modContainer: Optional<out ModContainer?> = ModList.get().getModContainerById(modId)
            return modContainer.map<Any?> { container: ModContainer? -> container.getModInfo().getDisplayName() }.orElse(null)
        }
    }
}