package com.refinedmods.refinedstorage.apiimpl.storage.externalstorage

import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.util.Action
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fluids.capability.IFluidHandlerimport
import java.util.*
import java.util.function.Supplier

class FluidExternalStorage(context: IExternalStorageContext?, handlerSupplier: Supplier<IFluidHandler>, connectedToInterface: Boolean) : IExternalStorage<FluidInstance?> {
    private val context: IExternalStorageContext?
    private val handlerSupplier: Supplier<IFluidHandler>
    val isConnectedToInterface: Boolean
    private val cache = FluidExternalStorageCache()
    override fun update(network: INetwork?) {
        if (accessType === AccessType.INSERT) {
            return
        }
        cache.update(network, handlerSupplier.get())
    }

    val capacity: Long
        get() {
            val fluidHandler: IFluidHandler = handlerSupplier.get()
            if (fluidHandler != null) {
                var cap: Long = 0
                for (i in 0 until fluidHandler.getTanks()) {
                    cap += fluidHandler.getTankCapacity(i)
                }
                return cap
            }
            return 0
        }
    val stacks: Collection<Any>?
        get() {
            val fluidHandler: IFluidHandler = handlerSupplier.get()
            if (fluidHandler != null) {
                val fluids: MutableList<FluidInstance> = ArrayList<FluidInstance>()
                for (i in 0 until fluidHandler.getTanks()) {
                    fluids.add(fluidHandler.getFluidInTank(i))
                }
                return fluids
            }
            return emptyList<FluidInstance>()
        }

    @Nonnull
    override fun insert(@Nonnull stack: FluidInstance, size: Int, action: Action?): FluidInstance {
        if (stack.isEmpty()) {
            return stack
        }
        if (context.acceptsFluid(stack)) {
            val filled: Int = handlerSupplier.get().fill(StackUtils.copy(stack, size), if (action === Action.PERFORM) IFluidHandler.FluidAction.EXECUTE else IFluidHandler.FluidAction.SIMULATE)
            return if (filled == size) {
                FluidInstance.EMPTY
            } else StackUtils.copy(stack, size - filled)
        }
        return StackUtils.copy(stack, size)
    }

    @Nonnull
    override fun extract(@Nonnull stack: FluidInstance, size: Int, flags: Int, action: Action?): FluidInstance {
        if (stack.isEmpty()) {
            return stack
        }
        val handler: IFluidHandler = handlerSupplier.get() ?: return FluidInstance.EMPTY
        return handler.drain(StackUtils.copy(stack, size), if (action === Action.PERFORM) IFluidHandler.FluidAction.EXECUTE else IFluidHandler.FluidAction.SIMULATE)
    }

    val stored: Int
        get() {
            val fluidHandler: IFluidHandler = handlerSupplier.get()
            if (fluidHandler != null) {
                var stored = 0
                for (i in 0 until fluidHandler.getTanks()) {
                    stored += fluidHandler.getFluidInTank(i).getAmount()
                }
                return stored
            }
            return 0
        }
    val priority: Int
        get() = context.getPriority()
    val accessType: AccessType?
        get() = context.getAccessType()

    override fun getCacheDelta(storedPreInsertion: Int, size: Int, @Nullable remainder: FluidInstance): Int {
        if (accessType === AccessType.INSERT) {
            return 0
        }
        return if (remainder == null) size else size - remainder.getAmount()
    }

    init {
        this.context = context
        this.handlerSupplier = handlerSupplier
        isConnectedToInterface = connectedToInterface
    }
}