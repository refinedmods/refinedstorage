package com.refinedmods.refinedstorage.screen.grid.stack

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.render.FluidRenderer
import com.refinedmods.refinedstorage.render.RenderSettings
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack
import net.minecraft.client.resources.I18n
import net.minecraft.tags.FluidTags
import net.minecraft.util.Identifier
import net.minecraft.util.text.Text
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fluids.FluidInstance
import org.apache.logging.log4j.LogManager
import java.util.*

class FluidGridStack(private override val id: UUID, @field:Nullable @get:Nullable
@param:Nullable override var otherId: UUID?, stack: FluidInstance, @Nullable entry: StorageTrackerEntry?, craftable: Boolean) : IGridStack {
    private val logger = LogManager.getLogger(javaClass)
    private val stack: FluidInstance

    @get:Nullable
    @Nullable
    override var trackerEntry: StorageTrackerEntry?
    override val isCraftable: Boolean
    private var zeroed = false
    private var cachedTags: MutableSet<String?>? = null
    private var cachedName: String? = null
    private var cachedTooltip: List<Text>? = null
    private var cachedModId: String? = null
    private var cachedModName: String? = null
    fun setZeroed(zeroed: Boolean) {
        this.zeroed = zeroed
    }

    fun getStack(): FluidInstance {
        return stack
    }

    override fun getId(): UUID? {
        return id
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
                    logger.warn("Could not retrieve fluid name of " + stack.getFluid().getRegistryName().toString(), t)
                    "<Error>"
                }
            }
            return cachedName
        }
    override val modId: String
        get() {
            if (cachedModId == null) {
                val registryName: Identifier = stack.getFluid().getRegistryName()
                cachedModId = if (registryName != null) {
                    registryName.getNamespace()
                } else {
                    "<Error>"
                }
            }
            return cachedModId!!
        }
    override val modName: String
        get() {
            if (cachedModName == null) {
                cachedModName = ItemGridStack.Companion.getModNameByModId(modId)
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
                for (owningTag in FluidTags.getCollection().getOwningTags(stack.getFluid())) {
                    cachedTags.add(owningTag.getPath())
                }
            }
            return cachedTags!!
        }
    override val tooltip: List<Any>?
        get() {
            if (cachedTooltip == null) {
                try {
                    cachedTooltip = Arrays.asList(stack.getDisplayName())
                } catch (t: Throwable) {
                    logger.warn("Could not retrieve fluid tooltip of " + stack.getFluid().getRegistryName().toString(), t)
                    cachedTooltip = Arrays.asList<Text>(StringTextComponent("<Error>"))
                }
            }
            return cachedTooltip
        }

    // The isCraftable check is needed so sorting is applied correctly
    override val quantity: Int
        get() =// The isCraftable check is needed so sorting is applied correctly
            if (isCraftable) 0 else stack.getAmount()
    override val formattedFullQuantity: String?
        get() = if (zeroed) {
            "0 mB"
        } else instance().getQuantityFormatter()!!.format(quantity) + " mB"

    override fun draw(matrixStack: MatrixStack?, screen: BaseScreen<*>, x: Int, y: Int) {
        FluidRenderer.INSTANCE.render(matrixStack, x, y, stack)
        val text: String?
        var color = RenderSettings.INSTANCE.secondaryColor
        if (zeroed) {
            text = "0"
            color = 16733525
        } else if (isCraftable) {
            text = I18n.format("gui.refinedstorage.grid.craft")
        } else {
            text = instance().getQuantityFormatter()!!.formatInBucketFormWithOnlyTrailingDigitsIfZero(quantity)
        }
        screen.renderQuantity(matrixStack, x, y, text, color)
    }

    override val ingredient: Any
        get() = stack

    init {
        this.stack = stack
        trackerEntry = entry
        isCraftable = craftable
    }
}