package com.refinedmods.refinedstorage.apiimpl

//import ModFileScanData.AnnotationData
import com.refinedmods.refinedstorage.api.IRSAPI
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskRegistry
import com.refinedmods.refinedstorage.api.network.INetworkManager
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridBehavior
import com.refinedmods.refinedstorage.api.network.grid.IGridManager
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeRegistry
import com.refinedmods.refinedstorage.api.storage.StorageType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskManager
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskRegistry
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskSync
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.IQuantityFormatter
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementList
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementRegistry
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementRegistry
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.CraftingRequestInfo
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.CraftingTaskRegistry
import com.refinedmods.refinedstorage.apiimpl.network.NetworkManager
import com.refinedmods.refinedstorage.apiimpl.network.NetworkNodeManager
import com.refinedmods.refinedstorage.apiimpl.network.NetworkNodeRegistry
import com.refinedmods.refinedstorage.apiimpl.network.grid.CraftingGridBehavior
import com.refinedmods.refinedstorage.apiimpl.network.grid.GridManager
import com.refinedmods.refinedstorage.apiimpl.storage.disk.*
import com.refinedmods.refinedstorage.apiimpl.util.Comparer
import com.refinedmods.refinedstorage.apiimpl.util.FluidInstanceList
import com.refinedmods.refinedstorage.apiimpl.util.ItemStackList
import com.refinedmods.refinedstorage.apiimpl.util.QuantityFormatter
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.server.world.ServerWorld
import org.apache.logging.log4j.LogManager
import reborncore.common.fluid.container.FluidInstance
import java.util.*

class API : IRSAPI {
    private val comparer: IComparer = Comparer()
    private val quantityFormatter: IQuantityFormatter = QuantityFormatter()
    private val networkNodeRegistry: INetworkNodeRegistry = NetworkNodeRegistry()
    private val craftingTaskRegistry: ICraftingTaskRegistry = CraftingTaskRegistry()
    private val craftingMonitorElementRegistry: ICraftingMonitorElementRegistry = CraftingMonitorElementRegistry()
    private val craftingPreviewElementRegistry: ICraftingPreviewElementRegistry = CraftingPreviewElementRegistry()
    private val gridManager: IGridManager = GridManager()
    private val craftingGridBehavior: ICraftingGridBehavior = CraftingGridBehavior()
    private val storageDiskRegistry: IStorageDiskRegistry = StorageDiskRegistry()
    private val storageDiskSync: IStorageDiskSync = StorageDiskSync()
    private val externalStorageProviders: MutableMap<StorageType, TreeSet<IExternalStorageProvider<*>>> = EnumMap(com.refinedmods.refinedstorage.api.storage.StorageType::class.java)
    private val patternRenderHandlers: MutableList<ICraftingPatternRenderHandler> = LinkedList()

    override fun getComparer(): IComparer {
        return comparer
    }


    override fun getQuantityFormatter(): IQuantityFormatter {
        return quantityFormatter
    }


    override fun getNetworkNodeRegistry(): INetworkNodeRegistry {
        return networkNodeRegistry
    }

    override fun getNetworkNodeManager(world: ServerWorld): INetworkNodeManager {

        return world.getSavedData()
                .getOrCreate(
                        { NetworkNodeManager(NetworkNodeManager.NAME, world) },
                        NetworkNodeManager.NAME
                )
    }

    override fun getNetworkManager(world: ServerWorld): INetworkManager {
        return world.chunkManager.getSavedData()
                .getOrCreate(
                        { NetworkManager(NetworkManager.NAME, world) },
                        NetworkManager.NAME
                )
    }


    override fun getCraftingTaskRegistry(): ICraftingTaskRegistry {
        return craftingTaskRegistry
    }


    override fun getCraftingMonitorElementRegistry(): ICraftingMonitorElementRegistry {
        return craftingMonitorElementRegistry
    }


    override fun getCraftingPreviewElementRegistry(): ICraftingPreviewElementRegistry {
        return craftingPreviewElementRegistry
    }


    override fun createItemStackList(): IStackList<ItemStack> {
        return ItemStackList()
    }


    override fun createFluidInstanceList(): IStackList<FluidInstance> {
        return FluidInstanceList()
    }


    override fun createCraftingMonitorElementList(): ICraftingMonitorElementList {
        return CraftingMonitorElementList()
    }


    override fun getGridManager(): IGridManager {
        return gridManager
    }


    override fun getCraftingGridBehavior(): ICraftingGridBehavior {
        return craftingGridBehavior
    }


    override fun getStorageDiskRegistry(): IStorageDiskRegistry {
        return storageDiskRegistry
    }


    override fun getStorageDiskManager(anyWorld: ServerWorld): IStorageDiskManager {
        val world: ServerWorld = anyWorld.server.overworld
        return world.getSavedData()
                .getOrCreate(
                        { StorageDiskManager(StorageDiskManager.NAME, world) },
                        StorageDiskManager.NAME
                )
    }


    override fun getStorageDiskSync(): IStorageDiskSync {
        return storageDiskSync
    }

    override fun addExternalStorageProvider(type: StorageType, provider: IExternalStorageProvider<*>) {
        externalStorageProviders.computeIfAbsent(type) { k: StorageType? -> TreeSet { a: IExternalStorageProvider<*>, b: IExternalStorageProvider<*> -> Integer.compare(b.priority, a.priority) } }.add(provider)
    }

    override fun getExternalStorageProviders(type: StorageType): Set<IExternalStorageProvider<*>> {
        val providers = externalStorageProviders[type]
        return providers ?: emptySet()
    }


    override fun createDefaultItemDisk(world: ServerWorld, capacity: Int): IStorageDisk<ItemStack> {
        return ItemStorageDisk(world, capacity)
    }


    override fun createDefaultFluidDisk(world: ServerWorld, capacity: Int): IStorageDisk<FluidInstance> {
        return FluidStorageDisk(world, capacity)
    }

    override fun createCraftingRequestInfo(stack: ItemStack): ICraftingRequestInfo {
        return CraftingRequestInfo(stack)
    }

    override fun createCraftingRequestInfo(stack: FluidInstance): ICraftingRequestInfo {
        return CraftingRequestInfo(stack)
    }

    @Throws(CraftingTaskReadException::class)
    override fun createCraftingRequestInfo(tag: CompoundTag): ICraftingRequestInfo {
        return CraftingRequestInfo(tag)
    }

    override fun addPatternRenderHandler(renderHandler: ICraftingPatternRenderHandler) {
        patternRenderHandlers.add(renderHandler)
    }

    override fun getPatternRenderHandlers(): List<ICraftingPatternRenderHandler> {
        return patternRenderHandlers
    }

    override fun getItemStackHashCode(stack: ItemStack): Int {
        var result = stack.item.hashCode()
        if (stack.hasTag()) {
            result = getHashCode(stack.tag, result)
        }
        return result
    }

    private fun getHashCode(tag: Tag?, result: Int): Int {
        var r = result
        r = when (tag) {
            null -> r
            is CompoundTag -> { getHashCode(tag, r) }
            is ListTag -> { getHashCode(tag, r) }
            else -> { 31 * r + tag.hashCode() }
        }
        return r
    }

    private fun getHashCode(tag: CompoundTag?, result: Int): Int {
        var r = result
        tag?.let {
            for (key in it.keys) {
                r = 31 * r + key.hashCode()
                r = getHashCode(it.get(key), r)
            }
        }

        return r
    }

    private fun getHashCode(tag: ListTag, result: Int): Int {
        var r = result
        for (tagItem in tag) {
            r = getHashCode(tagItem, r)
        }
        return r
    }

    override fun getFluidInstanceHashCode(stack: FluidInstance): Int {
        var result: Int = stack.fluid.hashCode()
        if (stack.tag != null) {
            result = getHashCode(stack.tag, result)
        }
        return result
    }

    override fun getNetworkNodeHashCode(node: INetworkNode): Int {
        var result = node.pos.hashCode()
        result = 31 * result + node.world.dimension.hashCode()
        return result
    }

    override fun isNetworkNodeEqual(left: INetworkNode, right: Any): Boolean {
        return when {
            right !is INetworkNode -> false
            left === right -> true
            left.world.dimension !== right.world.dimension -> false
            else -> left.pos == right.pos
        }
    }

    companion object {
//        private val LOGGER = LogManager.getLogger(API::class.java)
        private val INSTANCE: IRSAPI = API()
        @JvmStatic
        fun instance(): IRSAPI {
            return INSTANCE
        }

        @JvmStatic
        fun deliver() {
            // TODO Annotation based API injection
//            val annotationType = Type.getType(RSAPIInject::class.java)
//            val annotations: List<AnnotationData> = ModList.get().getAllScanData().stream()
//                    .map(ModFileScanData::getAnnotations)
//                    .flatMap({ obj: Collection<*> -> obj.stream() })
//                    .filter({ a -> annotationType == a.getAnnotationType() })
//                    .collect(Collectors.toList())
//            LOGGER.info("Found {} RS API injection {}", annotations.size, if (annotations.size == 1) "point" else "points")
//            for (annotation in annotations) {
//                try {
//                    val clazz = Class.forName(annotation.getClassType().getClassName())
//                    val field = clazz.getField(annotation.getMemberName())
//                    if (field.type == IRSAPI::class.java) {
//                        field[null] = INSTANCE
//                    }
//                    LOGGER.info("Injected RS API in {} {}", annotation.getClassType().getClassName(), annotation.getMemberName())
//                } catch (e: ClassNotFoundException) {
//                    LOGGER.error("Could not inject RS API in {} {}", annotation.getClassType().getClassName(), annotation.getMemberName(), e)
//                } catch (e: IllegalAccessException) {
//                    LOGGER.error("Could not inject RS API in {} {}", annotation.getClassType().getClassName(), annotation.getMemberName(), e)
//                } catch (e: IllegalArgumentException) {
//                    LOGGER.error("Could not inject RS API in {} {}", annotation.getClassType().getClassName(), annotation.getMemberName(), e)
//                } catch (e: NoSuchFieldException) {
//                    LOGGER.error("Could not inject RS API in {} {}", annotation.getClassType().getClassName(), annotation.getMemberName(), e)
//                } catch (e: SecurityException) {
//                    LOGGER.error("Could not inject RS API in {} {}", annotation.getClassType().getClassName(), annotation.getMemberName(), e)
//                }
//            }
        }
    }
}