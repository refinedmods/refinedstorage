package com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.StackListEntry
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.ProxyItemHandler
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler
import com.refinedmods.refinedstorage.inventory.item.validator.StorageDiskItemValidator
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener
import com.refinedmods.refinedstorage.item.UpgradeItem
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.StackUtils.createStorages
import com.refinedmods.refinedstorage.util.StackUtils.readItems
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import reborncore.common.fluid.container.FluidInstance
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors


class DiskManipulatorNetworkNode(world: World, pos: BlockPos?) : NetworkNode(world, pos!!), IComparable, IWhitelistBlacklist, IType, IStorageDiskContainerContext {
    private var compare = IComparer.COMPARE_NBT
    private var mode = IWhitelistBlacklist.BLACKLIST
    private var type = IType.ITEMS
    var ioMode = IO_MODE_INSERT
    private val itemDisks: Array<IStorageDisk<ItemStack?>?> = arrayOfNulls<IStorageDisk<*>?>(6)
    private val fluidDisks: Array<IStorageDisk<FluidInstance?>?> = arrayOfNulls<IStorageDisk<*>?>(6)
    private val upgrades = object : UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.STACK) {
        override fun getStackInteractCount(): Int {
            var count = super.stackInteractCount
            if (type == IType.FLUIDS) {
                count *= FluidAttributes.BUCKET_VOLUME
            }
            return count
        }
    }.addListener(NetworkNodeInventoryListener(this)) as UpgradeItemHandler
    private val inputDisks = BaseItemHandler(3)
            .addValidator(StorageDiskItemValidator())
            .addListener(NetworkNodeInventoryListener(this))
            .addListener { handler: BaseItemHandler, slot: Int, reading: Boolean ->
                if (!world.isClient) {
                    createStorages(
                            world as ServerWorld,
                            handler.getStackInSlot(slot),
                            slot,
                            itemDisks,
                            fluidDisks,
                            Function<IStorageDisk<ItemStack?>?, IStorageDisk<*>?> { s: IStorageDisk<ItemStack?> -> StorageDiskItemManipulatorWrapper(this@DiskManipulatorNetworkNode, s) },
                            Function<IStorageDisk<FluidInstance?>?, IStorageDisk<*>?> { s: IStorageDisk<FluidInstance?> -> StorageDiskFluidManipulatorWrapper(this@DiskManipulatorNetworkNode, s) }
                    )
                    if (!reading) {
                        WorldUtils.updateBlock(world, pos)
                    }
                }
            }
    private val outputDisks = BaseItemHandler(3)
            .addValidator(StorageDiskItemValidator())
            .addListener(NetworkNodeInventoryListener(this))
            .addListener { handler: BaseItemHandler, slot: Int, reading: Boolean ->
                if (!world.isClient) {
                    createStorages(
                            world as ServerWorld,
                            handler.getStackInSlot(slot),
                            3 + slot,
                            itemDisks,
                            fluidDisks,
                            Function<IStorageDisk<ItemStack?>?, IStorageDisk<*>?> { s: IStorageDisk<ItemStack?> -> StorageDiskItemManipulatorWrapper(this@DiskManipulatorNetworkNode, s) },
                            Function<IStorageDisk<FluidInstance?>?, IStorageDisk<*>?> { s: IStorageDisk<FluidInstance?> -> StorageDiskFluidManipulatorWrapper(this@DiskManipulatorNetworkNode, s) }
                    )
                    if (!reading) {
                        WorldUtils.updateBlock(world, pos)
                    }
                }
            }
    val disks = ProxyItemHandler(inputDisks, outputDisks)
    private val itemFilters = BaseItemHandler(9).addListener(NetworkNodeInventoryListener(this))
    private val fluidFilters = FluidInventory(9).addListener(NetworkNodeFluidInventoryListener(this))
    override val energyUsage: Int
        get() = RS.SERVER_CONFIG.diskManipulator.usage + upgrades.energyUsage

    override fun update() {
        super.update()
        if (!canUpdate() || ticks % upgrades.speed != 0) {
            return
        }
        var slot = 0
        if (type == IType.ITEMS) {
            while (slot < 3 && (itemDisks[slot] == null || isItemDiskDone(itemDisks[slot], slot))) {
                slot++
            }
            if (slot == 3) {
                return
            }
            val storage = itemDisks[slot]
            if (ioMode == IO_MODE_INSERT) {
                insertItemIntoNetwork(storage)
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractItemFromNetwork(storage, slot)
            }
        } else if (type == IType.FLUIDS) {
            while (slot < 3 && (fluidDisks[slot] == null || isFluidDiskDone(fluidDisks[slot], slot))) {
                slot++
            }
            if (slot == 3) {
                return
            }
            val storage: IStorageDisk<FluidInstance?>? = fluidDisks[slot]
            if (ioMode == IO_MODE_INSERT) {
                insertFluidIntoNetwork(storage, slot)
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractFluidFromNetwork(storage, slot)
            }
        }
    }

    private fun insertItemIntoNetwork(storage: IStorageDisk<ItemStack?>?) {
        val stacks: List<ItemStack?> = ArrayList(storage!!.getStacks())
        for (i in stacks.indices) {
            val stack = stacks[i]
            val extracted = storage.extract(stack, upgrades.stackInteractCount, compare, Action.PERFORM)
            if (extracted!!.isEmpty) {
                continue
            }
            val remainder = network!!.insertItem(extracted, extracted.count, Action.PERFORM)
            if (remainder!!.isEmpty) {
                break
            }

            // We need to check if the stack was inserted
            storage.insert(if (extracted == remainder) remainder.copy() else remainder, remainder.count, Action.PERFORM)
        }
    }

    // Iterate through disk stacks, if none can be inserted, return that it is done processing and can be output.
    private fun isItemDiskDone(storage: IStorageDisk<ItemStack?>?, slot: Int): Boolean {
        if (ioMode == IO_MODE_INSERT && storage!!.getStored() == 0) {
            moveDriveToOutput(slot)
            return true
        }

        // In Extract mode, we just need to check if the disk is full or not.
        if (ioMode == IO_MODE_EXTRACT) return if (storage!!.getStored() == storage.capacity) {
            moveDriveToOutput(slot)
            true
        } else {
            false
        }
        val stacks: List<ItemStack?> = ArrayList(storage!!.getStacks())
        for (i in stacks.indices) {
            val stack = stacks[i]
            val extracted = storage.extract(stack, upgrades.stackInteractCount, compare, Action.SIMULATE)
            if (extracted!!.isEmpty) {
                continue
            }
            val remainder = network!!.insertItem(extracted, extracted.count, Action.SIMULATE)
            if (remainder!!.isEmpty) { // An item could be inserted (no remainders when trying to). This disk isn't done.
                return false
            }
        }
        return true
    }

    private fun extractItemFromNetwork(storage: IStorageDisk<ItemStack?>?, slot: Int) {
        var extracted = ItemStack.EMPTY
        var i = 0
        if (itemFilters.isEmpty) {
            var toExtract: ItemStack? = null
            val networkItems: List<ItemStack> = network!!.itemStorageCache!!.getList().getStacks().stream().map(StackListEntry::stack).collect(Collectors.toList())
            var j = 0
            while ((toExtract == null || toExtract.isEmpty) && j < networkItems.size) {
                toExtract = networkItems[j++]
            }
            if (toExtract != null) {
                extracted = network!!.extractItem(toExtract, upgrades.stackInteractCount, compare, Action.PERFORM)
            }
        } else {
            while (itemFilters.getSlots() > i && extracted!!.isEmpty) {
                var filterStack = ItemStack.EMPTY
                while (itemFilters.getSlots() > i && filterStack.isEmpty) {
                    filterStack = itemFilters.getStackInSlot(i++)
                }
                if (!filterStack.isEmpty) {
                    extracted = network!!.extractItem(filterStack, upgrades.stackInteractCount, compare, Action.PERFORM)
                }
            }
        }
        if (extracted!!.isEmpty) {
            moveDriveToOutput(slot)
            return
        }
        val remainder = storage!!.insert(extracted, extracted.count, Action.PERFORM)
        network!!.insertItem(remainder, remainder!!.count, Action.PERFORM)
    }

    private fun insertFluidIntoNetwork(storage: IStorageDisk<FluidInstance?>?, slot: Int) {
        val stacks: List<FluidInstance?> = ArrayList<Any?>(storage!!.getStacks())
        var extracted: FluidInstance? = FluidInstance.EMPTY
        var i = 0
        while (extracted.isEmpty() && stacks.size > i) {
            val stack: FluidInstance? = stacks[i++]
            extracted = storage.extract(stack, upgrades.stackInteractCount, compare, Action.PERFORM)
        }
        if (extracted.isEmpty()) {
            moveDriveToOutput(slot)
            return
        }
        val remainder: FluidInstance? = network!!.insertFluid(extracted, extracted.getAmount(), Action.PERFORM)
        storage.insert(remainder, remainder.getAmount(), Action.PERFORM)
    }

    private fun isFluidDiskDone(storage: IStorageDisk<FluidInstance?>?, slot: Int): Boolean {
        if (ioMode == IO_MODE_INSERT && storage!!.getStored() == 0) {
            moveDriveToOutput(slot)
            return true
        }

        //In Extract mode, we just need to check if the disk is full or not.
        if (ioMode == IO_MODE_EXTRACT) return if (storage!!.getStored() == storage.capacity) {
            moveDriveToOutput(slot)
            true
        } else {
            false
        }
        val stacks: List<FluidInstance?> = ArrayList<Any?>(storage!!.getStacks())
        for (i in stacks.indices) {
            val stack: FluidInstance? = stacks[i]
            val extracted: FluidInstance? = storage.extract(stack, upgrades.stackInteractCount, compare, Action.SIMULATE)
            if (extracted.isEmpty()) {
                continue
            }
            val remainder: FluidInstance? = network!!.insertFluid(extracted, extracted.getAmount(), Action.SIMULATE)
            if (remainder.isEmpty()) { // A fluid could be inserted (no remainders when trying to). This disk isn't done.
                return false
            }
        }
        return true
    }

    private fun extractFluidFromNetwork(storage: IStorageDisk<FluidInstance?>?, slot: Int) {
        var extracted: FluidInstance? = FluidInstance.EMPTY
        var i = 0
        if (fluidFilters.isEmpty) {
            var toExtract: FluidInstance? = null
            val networkFluids: List<FluidInstance> = network!!.fluidStorageCache!!.getList().getStacks().stream().map(StackListEntry::stack).collect(Collectors.toList())
            var j = 0
            while ((toExtract == null || toExtract.getAmount() === 0) && j < networkFluids.size) {
                toExtract = networkFluids[j++]
            }
            if (toExtract != null) {
                extracted = network!!.extractFluid(toExtract, upgrades.stackInteractCount, compare, Action.PERFORM)
            }
        } else {
            while (fluidFilters.slots > i && extracted.isEmpty()) {
                var filterStack: FluidInstance = FluidInstance.EMPTY
                while (fluidFilters.slots > i && filterStack.isEmpty()) {
                    filterStack = fluidFilters.getFluid(i++)
                }
                if (!filterStack.isEmpty()) {
                    extracted = network!!.extractFluid(filterStack, upgrades.stackInteractCount, compare, Action.PERFORM)
                }
            }
        }
        if (extracted.isEmpty()) {
            moveDriveToOutput(slot)
            return
        }
        val remainder: FluidInstance? = storage!!.insert(extracted, extracted.getAmount(), Action.PERFORM)
        network!!.insertFluid(remainder, remainder.getAmount(), Action.PERFORM)
    }

    private fun moveDriveToOutput(slot: Int) {
        val disk: ItemStack = inputDisks.getStackInSlot(slot)
        if (!disk.isEmpty) {
            var i = 0
            while (i < 3 && !outputDisks.getStackInSlot(i).isEmpty()) {
                i++
            }
            if (i == 3) {
                return
            }
            inputDisks.extractItem(slot, 1, false)
            outputDisks.insertItem(i, disk, false)
        }
    }

    val diskState: Array<DiskState?>
        get() {
            val diskStates = arrayOfNulls<DiskState>(6)
            for (i in 0..5) {
                var state = DiskState.NONE
                if (itemDisks[i] != null || fluidDisks[i] != null) {
                    state = if (!canUpdate()) {
                        DiskState.DISCONNECTED
                    } else {
                        DiskState.Companion.get(
                                if (itemDisks[i] != null) itemDisks[i]!!.getStored() else fluidDisks[i]!!.getStored(),
                                if (itemDisks[i] != null) itemDisks[i]!!.capacity else fluidDisks[i]!!.capacity
                        )
                    }
                }
                diskStates[i] = state
            }
            return diskStates
        }

    override fun getCompare(): Int {
        return compare
    }

    override fun setCompare(compare: Int) {
        this.compare = compare
    }

    override fun getType(): Int {
        return if (world.isClient) DiskManipulatorTile.TYPE.value else type
    }

    override fun setType(type: Int) {
        this.type = type
    }

    override fun getItemFilters(): IItemHandlerModifiable {
        return itemFilters
    }

    override fun getFluidFilters(): FluidInventory {
        return fluidFilters
    }

    override fun setWhitelistBlacklistMode(mode: Int) {
        this.mode = mode
    }

    override fun getWhitelistBlacklistMode(): Int {
        return mode
    }

    fun getInputDisks(): IItemHandler {
        return inputDisks
    }

    fun getOutputDisks(): IItemHandler {
        return outputDisks
    }

    fun getUpgrades(): IItemHandler {
        return upgrades
    }

    override fun read(tag: CompoundTag) {
        super.read(tag)
        readItems(upgrades, 3, tag)
        readItems(inputDisks, 4, tag)
        readItems(outputDisks, 5, tag)
    }

    override val id: Identifier
        get() = ID

    override fun write(tag: CompoundTag): CompoundTag {
        super.write(tag)
        StackUtils.writeItems(upgrades, 3, tag)
        StackUtils.writeItems(inputDisks, 4, tag)
        StackUtils.writeItems(outputDisks, 5, tag)
        return tag
    }

    override fun writeConfiguration(tag: CompoundTag): CompoundTag {
        super.writeConfiguration(tag)
        StackUtils.writeItems(itemFilters, 1, tag)
        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt())
        tag.putInt(NBT_COMPARE, compare)
        tag.putInt(NBT_MODE, mode)
        tag.putInt(NBT_TYPE, type)
        tag.putInt(NBT_IO_MODE, ioMode)
        return tag
    }

    override fun readConfiguration(tag: CompoundTag) {
        super.readConfiguration(tag)
        readItems(itemFilters, 1, tag)
        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS))
        }
        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE)
        }
        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE)
        }
        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE)
        }
        if (tag.contains(NBT_IO_MODE)) {
            ioMode = tag.getInt(NBT_IO_MODE)
        }
    }

    override val drops: IItemHandler
        get() = CombinedInvWrapper(inputDisks, outputDisks, upgrades)

    override fun getAccessType(): AccessType? {
        return AccessType.INSERT_EXTRACT
    }

    companion object {
        @kotlin.jvm.JvmField
        val ID: Identifier = Identifier(RS.ID, "disk_manipulator")
        const val IO_MODE_INSERT = 0
        const val IO_MODE_EXTRACT = 1
        private const val NBT_COMPARE = "Compare"
        private const val NBT_MODE = "Mode"
        private const val NBT_TYPE = "Type"
        private const val NBT_IO_MODE = "IOMode"
        private const val NBT_FLUID_FILTERS = "FluidFilters"
    }
}