package com.refinedmods.refinedstorage.apiimpl.network.node

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.nodeimport.ConnectivityStateChangeCause
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler
import com.refinedmods.refinedstorage.inventory.item.validator.PatternItemValidator
import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener
import com.refinedmods.refinedstorage.item.UpgradeItem
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

class CrafterNetworkNode(world: World, pos: BlockPos?) : NetworkNode(world, pos), ICraftingPatternContainer {
    enum class CrafterMode {
        IGNORE, SIGNAL_UNLOCKS_AUTOCRAFTING, SIGNAL_LOCKS_AUTOCRAFTING, PULSE_INSERTS_NEXT_SET;

        companion object {
            @kotlin.jvm.JvmStatic
            fun getById(id: Int): CrafterMode {
                return if (id >= 0 && id < values().size) {
                    values()[id]
                } else IGNORE
            }
        }
    }

    private val patternsInventory: BaseItemHandler = object : BaseItemHandler(9) {
        fun getSlotLimit(slot: Int): Int {
            return 1
        }
    }
            .addValidator(PatternItemValidator(world))
            .addListener(NetworkNodeInventoryListener(this))
            .addListener(InventoryListener<BaseItemHandler> { handler: BaseItemHandler?, slot: Int, reading: Boolean ->
                if (!reading) {
                    if (!world.isClient) {
                        invalidate()
                    }
                    if (network != null) {
                        network!!.craftingManager.invalidate()
                    }
                }
            })
    private val patterns: MutableList<ICraftingPattern?> = ArrayList<ICraftingPattern?>()
    private val upgrades: UpgradeItemHandler = UpgradeItemHandler(4, UpgradeItem.Type.SPEED)
            .addListener(NetworkNodeInventoryListener(this)) as UpgradeItemHandler

    // Used to prevent infinite recursion on getRootContainer() when there's e.g. two crafters facing each other.
    private var visited = false
    private var mode = CrafterMode.IGNORE
    private var locked = false
    private var wasPowered = false

    var displayName: Text? = null

    @Nullable
    private var uuid: UUID? = null
    private fun invalidate() {
        patterns.clear()
        for (i in 0 until patternsInventory.getSlots()) {
            val patternStack: ItemStack = patternsInventory.getStackInSlot(i)
            if (!patternStack.isEmpty()) {
                val pattern: ICraftingPattern = (patternStack.getItem() as ICraftingPatternProvider).create(world, patternStack, this)
                if (pattern.isValid()) {
                    patterns.add(pattern)
                }
            }
        }
    }

    override val energyUsage: Int
        get() = RS.SERVER_CONFIG.getCrafter().getUsage() + upgrades.getEnergyUsage() + RS.SERVER_CONFIG.getCrafter().getPatternUsage() * patterns.size

    override fun update() {
        super.update()
        if (ticks == 1) {
            invalidate()
        }
        if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET && world.isBlockPresent(pos)) {
            if (world.isReceivingRedstonePower(pos)) {
                wasPowered = true
                markDirty()
            } else if (wasPowered) {
                wasPowered = false
                locked = false
                markDirty()
            }
        }
    }

    protected override fun onConnectedStateChange(network: INetwork?, state: Boolean, cause: ConnectivityStateChangeCause?) {
        super.onConnectedStateChange(network, state, cause)
        network.craftingManager.invalidate()
    }

    override fun onDisconnected(network: INetwork?) {
        super.onDisconnected(network)
        network.craftingManager.getTasks().stream()
                .filter(Predicate<ICraftingTask> { task: ICraftingTask -> task.getPattern().getContainer().getPosition() == pos })
                .forEach(Consumer<ICraftingTask> { task: ICraftingTask -> network.craftingManager.cancel(task.getId()) })
    }

    fun onDirectionChanged(direction: Direction?) {
        super.onDirectionChanged(direction)
        if (network != null) {
            network!!.craftingManager.invalidate()
        }
    }

    override fun read(tag: CompoundTag) {
        super.read(tag)
        StackUtils.readItems(patternsInventory, 0, tag)
        invalidate()
        StackUtils.readItems(upgrades, 1, tag)
        if (tag.contains(NBT_DISPLAY_NAME)) {
            displayName = Text.Serializer.func_240643_a_(tag.getString(NBT_DISPLAY_NAME))
        }
        if (tag.hasUniqueId(NBT_UUID)) {
            uuid = tag.getUniqueId(NBT_UUID)
        }
        if (tag.contains(NBT_MODE)) {
            mode = CrafterMode.getById(tag.getInt(NBT_MODE))
        }
        if (tag.contains(NBT_LOCKED)) {
            locked = tag.getBoolean(NBT_LOCKED)
        }
        if (tag.contains(NBT_WAS_POWERED)) {
            wasPowered = tag.getBoolean(NBT_WAS_POWERED)
        }
    }

    override val id: Identifier
        get() = ID

    override fun write(tag: CompoundTag): CompoundTag {
        super.write(tag)
        StackUtils.writeItems(patternsInventory, 0, tag)
        StackUtils.writeItems(upgrades, 1, tag)
        if (displayName != null) {
            tag.putString(NBT_DISPLAY_NAME, Text.Serializer.toJson(displayName))
        }
        if (uuid != null) {
            tag.putUniqueId(NBT_UUID, uuid)
        }
        tag.putInt(NBT_MODE, mode.ordinal)
        tag.putBoolean(NBT_LOCKED, locked)
        tag.putBoolean(NBT_WAS_POWERED, wasPowered)
        return tag
    }

    val updateInterval: Int
        get() = when (upgrades.getUpgradeCount(UpgradeItem.Type.SPEED)) {
            0 -> 10
            1 -> 8
            2 -> 6
            3 -> 4
            4 -> 2
            else -> 0
        }
    val maximumSuccessfulCraftingUpdates: Int
        get() = when (upgrades.getUpgradeCount(UpgradeItem.Type.SPEED)) {
            0 -> 1
            1 -> 2
            2 -> 3
            3 -> 4
            4 -> 5
            else -> 1
        }

    @get:Nullable
    val connectedInventory: IItemHandler?
        get() {
            val proxy: ICraftingPatternContainer = rootContainer ?: return null
            return WorldUtils.getItemHandler(proxy.getFacingTile(), proxy.getDirection().getOpposite())
        }

    @get:Nullable
    val connectedFluidInventory: IFluidHandler?
        get() {
            val proxy: ICraftingPatternContainer = rootContainer ?: return null
            return WorldUtils.getFluidHandler(proxy.getFacingTile(), proxy.getDirection().getOpposite())
        }

    @get:Nullable
    val connectedTile: BlockEntity?
        get() {
            val proxy: ICraftingPatternContainer = rootContainer ?: return null
            return proxy.getFacingTile()
        }

    @get:Nullable
    override val facingTile: BlockEntity?
        get() {
            val facingPos: BlockPos = pos.offset(direction)
            return if (!world.isBlockPresent(facingPos)) {
                null
            } else world.getBlockEntity(facingPos)
        }

    override fun getPatterns(): List<ICraftingPattern?>? {
        return patterns
    }

    @get:Nullable
    val patternInventory: IItemHandlerModifiable?
        get() = patternsInventory
    val name: Text?
        get() {
            if (displayName != null) {
                return displayName
            }
            val facing: BlockEntity? = connectedTile
            if (facing is INameable && (facing as INameable?).getName() != null) {
                return (facing as INameable?).getName()
            }
            return if (facing != null) {
                TranslationTextComponent(world.getBlockState(facing.getPos()).block.translationKey)
            } else DEFAULT_NAME
        }

    fun setDisplayName(displayName: Text?) {
        this.displayName = displayName
    }

    val position: BlockPos?
        get() = pos

    fun getMode(): CrafterMode {
        return mode
    }

    fun setMode(mode: CrafterMode) {
        this.mode = mode
        wasPowered = false
        locked = false
        markDirty()
    }

    val patternItems: IItemHandler
        get() = patternsInventory

    fun getUpgrades(): IItemHandler {
        return upgrades
    }

    override val drops: IItemHandler
        get() = CombinedInvWrapper(patternsInventory, upgrades)

    @get:Nullable
    val rootContainer: ICraftingPatternContainer?
        get() {
            if (visited) {
                return null
            }
            val facing: INetworkNode = API.instance().getNetworkNodeManager(world as ServerWorld).getNode(pos.offset(direction))
            if (facing !is ICraftingPatternContainer || facing.network !== network) {
                return this
            }
            visited = true
            val facingContainer: ICraftingPatternContainer = (facing as ICraftingPatternContainer).getRootContainer()
            visited = false
            return facingContainer
        }
    val rootContainerNotSelf: Optional<Any>
        get() {
            val root: ICraftingPatternContainer? = rootContainer
            return if (root != null && root !== this) {
                Optional.of(root)
            } else Optional.empty()
        }

    override fun getUuid(): UUID? {
        if (uuid == null) {
            uuid = UUID.randomUUID()
            markDirty()
        }
        return uuid
    }

    override fun isLocked(): Boolean {
        val root: Optional<ICraftingPatternContainer> = rootContainerNotSelf
        return if (root.isPresent()) {
            root.get().isLocked()
        } else when (mode) {
            CrafterMode.IGNORE -> false
            CrafterMode.SIGNAL_LOCKS_AUTOCRAFTING -> world.isReceivingRedstonePower(pos)
            CrafterMode.SIGNAL_UNLOCKS_AUTOCRAFTING -> !world.isReceivingRedstonePower(pos)
            CrafterMode.PULSE_INSERTS_NEXT_SET -> locked
            else -> false
        }
    }

    override fun unlock() {
        locked = false
    }

    override fun onUsedForProcessing() {
        val root: Optional<ICraftingPatternContainer> = rootContainerNotSelf
        if (root.isPresent()) {
            root.get().onUsedForProcessing()
            return
        }
        if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET) {
            locked = true
            markDirty()
        }
    }

    companion object {
        @kotlin.jvm.JvmField
        val ID: Identifier = Identifier(RS.ID, "crafter")
        private val DEFAULT_NAME: Text = TranslationTextComponent("gui.refinedstorage.crafter")
        private const val NBT_DISPLAY_NAME = "DisplayName"
        private const val NBT_UUID = "CrafterUuid"
        private const val NBT_MODE = "Mode"
        private const val NBT_LOCKED = "Locked"
        private const val NBT_WAS_POWERED = "WasPowered"
    }
}