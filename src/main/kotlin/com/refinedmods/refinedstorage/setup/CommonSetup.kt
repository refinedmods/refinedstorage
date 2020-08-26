package com.refinedmods.refinedstorage.setup

import RegistryEvent.Register
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSBlocks
import com.refinedmods.refinedstorage.RSLootFunctions
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import com.refinedmods.refinedstorage.api.network.NetworkType
import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeFactory
import com.refinedmods.refinedstorage.api.storage.StorageType
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ErrorCraftingMonitorElement
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.FluidCraftingMonitorElement
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ItemCraftingMonitorElement
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ErrorCraftingPreviewElement
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.FluidCraftingPreviewElement
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ItemCraftingPreviewElement
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.CraftingTaskFactory
import com.refinedmods.refinedstorage.apiimpl.network.NetworkListener
import com.refinedmods.refinedstorage.apiimpl.network.NetworkNodeListener
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.*
import com.refinedmods.refinedstorage.apiimpl.network.node.*
import com.refinedmods.refinedstorage.apiimpl.network.node.diskdrive.DiskDriveNetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.FluidStorageDiskFactory
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.ItemStorageDiskFactory
import com.refinedmods.refinedstorage.apiimpl.storage.externalstorage.FluidExternalStorageProvider
import com.refinedmods.refinedstorage.apiimpl.storage.externalstorage.ItemExternalStorageProvider
import com.refinedmods.refinedstorage.block.*
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability
import com.refinedmods.refinedstorage.container.*
import com.refinedmods.refinedstorage.container.factory.*
import com.refinedmods.refinedstorage.integration.craftingtweaks.CraftingTweaksIntegration
import com.refinedmods.refinedstorage.integration.inventorysorter.InventorySorterIntegration
import com.refinedmods.refinedstorage.item.*
import com.refinedmods.refinedstorage.item.blockitem.*
import com.refinedmods.refinedstorage.recipe.UpgradeWithEnchantedBookRecipeSerializer
import com.refinedmods.refinedstorage.tile.*
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.tile.grid.GridTile
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile
import com.refinedmods.refinedstorage.util.BlockUtils
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.nbt.CompoundTag
import net.minecraft.tileentity.BlockEntity
import net.minecraft.tileentity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import java.util.function.Function

class CommonSetup {
    @SubscribeEvent
    fun onCommonSetup(e: FMLCommonSetupEvent?) {
        RS.NETWORK_HANDLER.register()
        NetworkNodeProxyCapability.register()
        MinecraftForge.EVENT_BUS.register(NetworkNodeListener())
        MinecraftForge.EVENT_BUS.register(NetworkListener())
        MinecraftForge.EVENT_BUS.register(BlockListener())
        RSLootFunctions.register()
        instance().getStorageDiskRegistry()!!.add(ItemStorageDiskFactory.ID, ItemStorageDiskFactory())
        instance().getStorageDiskRegistry()!!.add(FluidStorageDiskFactory.ID, FluidStorageDiskFactory())
        instance().getNetworkNodeRegistry()!!.add(DiskDriveNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, DiskDriveNetworkNode(world!!, pos)) })
        instance().getNetworkNodeRegistry()!!.add(CableNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, CableNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(GridNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, GridNetworkNode(world, pos, GridType.NORMAL)) })
        instance().getNetworkNodeRegistry()!!.add(GridNetworkNode.CRAFTING_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, GridNetworkNode(world, pos, GridType.CRAFTING)) })
        instance().getNetworkNodeRegistry()!!.add(GridNetworkNode.PATTERN_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, GridNetworkNode(world, pos, GridType.PATTERN)) })
        instance().getNetworkNodeRegistry()!!.add(GridNetworkNode.FLUID_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, GridNetworkNode(world, pos, GridType.FLUID)) })
        instance().getNetworkNodeRegistry()!!.add(StorageNetworkNode.ONE_K_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.ONE_K)) })
        instance().getNetworkNodeRegistry()!!.add(StorageNetworkNode.FOUR_K_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.FOUR_K)) })
        instance().getNetworkNodeRegistry()!!.add(StorageNetworkNode.SIXTEEN_K_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.SIXTEEN_K)) })
        instance().getNetworkNodeRegistry()!!.add(StorageNetworkNode.SIXTY_FOUR_K_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.SIXTY_FOUR_K)) })
        instance().getNetworkNodeRegistry()!!.add(StorageNetworkNode.CREATIVE_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.CREATIVE)) })
        instance().getNetworkNodeRegistry()!!.add(FluidStorageNetworkNode.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, FluidStorageNetworkNode(world, pos, FluidStorageType.SIXTY_FOUR_K)) })
        instance().getNetworkNodeRegistry()!!.add(FluidStorageNetworkNode.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, FluidStorageNetworkNode(world, pos, FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K)) })
        instance().getNetworkNodeRegistry()!!.add(FluidStorageNetworkNode.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, FluidStorageNetworkNode(world, pos, FluidStorageType.THOUSAND_TWENTY_FOUR_K)) })
        instance().getNetworkNodeRegistry()!!.add(FluidStorageNetworkNode.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, FluidStorageNetworkNode(world, pos, FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K)) })
        instance().getNetworkNodeRegistry()!!.add(FluidStorageNetworkNode.CREATIVE_FLUID_STORAGE_BLOCK_ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, FluidStorageNetworkNode(world, pos, FluidStorageType.CREATIVE)) })
        instance().getNetworkNodeRegistry()!!.add(ExternalStorageNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, ExternalStorageNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(ImporterNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, ImporterNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(ExporterNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, ExporterNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(NetworkReceiverNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, NetworkReceiverNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(NetworkTransmitterNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, NetworkTransmitterNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(RelayNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, RelayNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(DetectorNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, DetectorNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(SecurityManagerNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, SecurityManagerNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(InterfaceNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, InterfaceNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(FluidInterfaceNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, FluidInterfaceNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(WirelessTransmitterNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, WirelessTransmitterNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(StorageMonitorNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, StorageMonitorNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(ConstructorNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, ConstructorNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(DestructorNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, DestructorNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(DiskManipulatorNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, DiskManipulatorNetworkNode(world!!, pos)) })
        instance().getNetworkNodeRegistry()!!.add(CrafterNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, CrafterNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(CrafterManagerNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, CrafterManagerNetworkNode(world, pos)) })
        instance().getNetworkNodeRegistry()!!.add(CraftingMonitorNetworkNode.ID, INetworkNodeFactory { tag: CompoundTag, world: World?, pos: BlockPos? -> readAndReturn(tag, CraftingMonitorNetworkNode(world, pos)) })
        instance().getGridManager()!!.add(GridBlockGridFactory.ID, GridBlockGridFactory())
        instance().getGridManager()!!.add(WirelessGridGridFactory.ID, WirelessGridGridFactory())
        instance().getGridManager()!!.add(WirelessFluidGridGridFactory.ID, WirelessFluidGridGridFactory())
        instance().getGridManager()!!.add(PortableGridGridFactory.ID, PortableGridGridFactory())
        instance().getGridManager()!!.add(PortableGridBlockGridFactory.ID, PortableGridBlockGridFactory())
        instance().addExternalStorageProvider(StorageType.ITEM, ItemExternalStorageProvider())
        instance().addExternalStorageProvider(StorageType.FLUID, FluidExternalStorageProvider())
        instance().getCraftingPreviewElementRegistry()!!.add(ItemCraftingPreviewElement.ID, Function<error.NonExistentClass?, ICraftingPreviewElement<*>?> { obj: error.NonExistentClass? -> ItemCraftingPreviewElement.read() })
        instance().getCraftingPreviewElementRegistry()!!.add(FluidCraftingPreviewElement.ID, Function<error.NonExistentClass?, ICraftingPreviewElement<*>?> { obj: error.NonExistentClass? -> FluidCraftingPreviewElement.read() })
        instance().getCraftingPreviewElementRegistry()!!.add(ErrorCraftingPreviewElement.ID, Function<error.NonExistentClass?, ICraftingPreviewElement<*>?> { obj: error.NonExistentClass? -> ErrorCraftingPreviewElement.read() })
        instance().getCraftingMonitorElementRegistry()!!.add(ItemCraftingMonitorElement.ID, Function<error.NonExistentClass?, ICraftingMonitorElement?> { obj: error.NonExistentClass? -> ItemCraftingMonitorElement.read() })
        instance().getCraftingMonitorElementRegistry()!!.add(FluidCraftingMonitorElement.ID, Function<error.NonExistentClass?, ICraftingMonitorElement?> { obj: error.NonExistentClass? -> FluidCraftingMonitorElement.read() })
        instance().getCraftingMonitorElementRegistry()!!.add(ErrorCraftingMonitorElement.ID, Function<error.NonExistentClass?, ICraftingMonitorElement?> { obj: error.NonExistentClass? -> ErrorCraftingMonitorElement.read() })
        instance().getCraftingTaskRegistry()!!.add(CraftingTaskFactory.ID, CraftingTaskFactory())
        if (CraftingTweaksIntegration.isLoaded) {
            CraftingTweaksIntegration.register()
        }
        if (InventorySorterIntegration.isLoaded) {
            InventorySorterIntegration.register()
        }
    }

    private fun readAndReturn(tag: CompoundTag, node: NetworkNode): INetworkNode {
        node.read(tag)
        return node
    }

    @SubscribeEvent
    fun onRegisterRecipeSerializers(e: Register<IRecipeSerializer<*>?>) {
        e.getRegistry().register(UpgradeWithEnchantedBookRecipeSerializer().setRegistryName(RS.ID, "upgrade_with_enchanted_book"))
    }

    @SubscribeEvent
    fun onRegisterBlocks(e: Register<Block?>) {
        e.getRegistry().register(QuartzEnrichedIronBlock())
        e.getRegistry().register(ControllerBlock(NetworkType.NORMAL))
        e.getRegistry().register(ControllerBlock(NetworkType.CREATIVE))
        e.getRegistry().register(MachineCasingBlock())
        e.getRegistry().register(CableBlock())
        e.getRegistry().register(DiskDriveBlock())
        e.getRegistry().register(GridBlock(GridType.NORMAL))
        e.getRegistry().register(GridBlock(GridType.CRAFTING))
        e.getRegistry().register(GridBlock(GridType.PATTERN))
        e.getRegistry().register(GridBlock(GridType.FLUID))
        for (type in ItemStorageType.values()) {
            e.getRegistry().register(StorageBlock(type))
        }
        for (type in FluidStorageType.values()) {
            e.getRegistry().register(FluidStorageBlock(type))
        }
        e.getRegistry().register(ExternalStorageBlock())
        e.getRegistry().register(ImporterBlock())
        e.getRegistry().register(ExporterBlock())
        e.getRegistry().register(NetworkReceiverBlock())
        e.getRegistry().register(NetworkTransmitterBlock())
        e.getRegistry().register(RelayBlock())
        e.getRegistry().register(DetectorBlock())
        e.getRegistry().register(SecurityManagerBlock())
        e.getRegistry().register(InterfaceBlock())
        e.getRegistry().register(FluidInterfaceBlock())
        e.getRegistry().register(WirelessTransmitterBlock())
        e.getRegistry().register(StorageMonitorBlock())
        e.getRegistry().register(ConstructorBlock())
        e.getRegistry().register(DestructorBlock())
        e.getRegistry().register(DiskManipulatorBlock())
        e.getRegistry().register(PortableGridBlock(PortableGridBlockItem.Type.NORMAL))
        e.getRegistry().register(PortableGridBlock(PortableGridBlockItem.Type.CREATIVE))
        e.getRegistry().register(CrafterBlock())
        e.getRegistry().register(CrafterManagerBlock())
        e.getRegistry().register(CraftingMonitorBlock())
    }

    @SubscribeEvent
    fun onRegisterTiles(e: Register<BlockEntityType<*>?>) {
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ ControllerTile(NetworkType.NORMAL) }, RSBlocks.CONTROLLER).build(null).setRegistryName(RS.ID, "controller")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ ControllerTile(NetworkType.CREATIVE) }, RSBlocks.CREATIVE_CONTROLLER).build(null).setRegistryName(RS.ID, "creative_controller")))
        e.getRegistry().register(BlockEntityType.Builder.create({ CableTile() }, RSBlocks.CABLE).build(null).setRegistryName(RS.ID, "cable"))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ DiskDriveTile() }, RSBlocks.DISK_DRIVE).build(null).setRegistryName(RS.ID, "disk_drive")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ GridTile(GridType.NORMAL) }, RSBlocks.GRID).build(null).setRegistryName(RS.ID, "grid")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ GridTile(GridType.CRAFTING) }, RSBlocks.CRAFTING_GRID).build(null).setRegistryName(RS.ID, "crafting_grid")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ GridTile(GridType.PATTERN) }, RSBlocks.PATTERN_GRID).build(null).setRegistryName(RS.ID, "pattern_grid")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ GridTile(GridType.FLUID) }, RSBlocks.FLUID_GRID).build(null).setRegistryName(RS.ID, "fluid_grid")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ StorageTile(ItemStorageType.ONE_K) }, RSBlocks.ONE_K_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "1k_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ StorageTile(ItemStorageType.FOUR_K) }, RSBlocks.FOUR_K_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "4k_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ StorageTile(ItemStorageType.SIXTEEN_K) }, RSBlocks.SIXTEEN_K_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "16k_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ StorageTile(ItemStorageType.SIXTY_FOUR_K) }, RSBlocks.SIXTY_FOUR_K_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "64k_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ StorageTile(ItemStorageType.CREATIVE) }, RSBlocks.CREATIVE_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "creative_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ FluidStorageTile(FluidStorageType.SIXTY_FOUR_K) }, RSBlocks.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "64k_fluid_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ FluidStorageTile(FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K) }, RSBlocks.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "256k_fluid_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ FluidStorageTile(FluidStorageType.THOUSAND_TWENTY_FOUR_K) }, RSBlocks.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "1024k_fluid_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ FluidStorageTile(FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K) }, RSBlocks.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "4096k_fluid_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ FluidStorageTile(FluidStorageType.CREATIVE) }, RSBlocks.CREATIVE_FLUID_STORAGE_BLOCK).build(null).setRegistryName(RS.ID, "creative_fluid_storage_block")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ ExternalStorageTile() }, RSBlocks.EXTERNAL_STORAGE).build(null).setRegistryName(RS.ID, "external_storage")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ ImporterTile() }, RSBlocks.IMPORTER).build(null).setRegistryName(RS.ID, "importer")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ ExporterTile() }, RSBlocks.EXPORTER).build(null).setRegistryName(RS.ID, "exporter")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ NetworkReceiverTile() }, RSBlocks.NETWORK_RECEIVER).build(null).setRegistryName(RS.ID, "network_receiver")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ NetworkTransmitterTile() }, RSBlocks.NETWORK_TRANSMITTER).build(null).setRegistryName(RS.ID, "network_transmitter")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ RelayTile() }, RSBlocks.RELAY).build(null).setRegistryName(RS.ID, "relay")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ DetectorTile() }, RSBlocks.DETECTOR).build(null).setRegistryName(RS.ID, "detector")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ SecurityManagerTile() }, RSBlocks.SECURITY_MANAGER).build(null).setRegistryName(RS.ID, "security_manager")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ InterfaceTile() }, RSBlocks.INTERFACE).build(null).setRegistryName(RS.ID, "interface")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ FluidInterfaceTile() }, RSBlocks.FLUID_INTERFACE).build(null).setRegistryName(RS.ID, "fluid_interface")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ WirelessTransmitterTile() }, RSBlocks.WIRELESS_TRANSMITTER).build(null).setRegistryName(RS.ID, "wireless_transmitter")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ StorageMonitorTile() }, RSBlocks.STORAGE_MONITOR).build(null).setRegistryName(RS.ID, "storage_monitor")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ ConstructorTile() }, RSBlocks.CONSTRUCTOR).build(null).setRegistryName(RS.ID, "constructor")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ DestructorTile() }, RSBlocks.DESTRUCTOR).build(null).setRegistryName(RS.ID, "destructor")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ DiskManipulatorTile() }, RSBlocks.DISK_MANIPULATOR).build(null).setRegistryName(RS.ID, "disk_manipulator")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ CrafterTile() }, RSBlocks.CRAFTER).build(null).setRegistryName(RS.ID, "crafter")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ CrafterManagerTile() }, RSBlocks.CRAFTER_MANAGER).build(null).setRegistryName(RS.ID, "crafter_manager")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ CraftingMonitorTile() }, RSBlocks.CRAFTING_MONITOR).build(null).setRegistryName(RS.ID, "crafting_monitor")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ PortableGridTile(PortableGridBlockItem.Type.CREATIVE) }, RSBlocks.CREATIVE_PORTABLE_GRID).build(null).setRegistryName(RS.ID, "creative_portable_grid")))
        e.getRegistry().register(registerTileDataParameters<T>(BlockEntityType.Builder.create({ PortableGridTile(PortableGridBlockItem.Type.NORMAL) }, RSBlocks.PORTABLE_GRID).build(null).setRegistryName(RS.ID, "portable_grid")))
    }

    private fun <T : BlockEntity?> registerTileDataParameters(t: BlockEntityType<T>): BlockEntityType<T> {
        val tile = t.create() as BaseTile
        tile.getDataManager().getParameters().forEach { parameter: TileDataParameter<*, *>? -> TileDataManager.registerParameter(parameter) }
        return t
    }

    @SubscribeEvent
    fun onRegisterContainers(e: Register<ContainerType<*>?>) {
        e.getRegistry().register(IForgeContainerType.create({ windowId, inv, data -> FilterContainer(inv.player, inv.getCurrentItem(), windowId) }).setRegistryName(RS.ID, "filter"))
        e.getRegistry().register(IForgeContainerType.create({ windowId, inv, data -> ControllerContainer(null, inv.player, windowId) }).setRegistryName(RS.ID, "controller"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<DiskDriveContainer, DiskDriveTile>(PositionalTileContainerFactory.Factory<DiskDriveContainer, DiskDriveTile> { windowId: Int, inv: PlayerInventory, tile: DiskDriveTile? -> DiskDriveContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "disk_drive"))
        e.getRegistry().register(IForgeContainerType.create(GridContainerFactory()).setRegistryName(RS.ID, "grid"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<StorageContainer, StorageTile>(PositionalTileContainerFactory.Factory<StorageContainer, StorageTile> { windowId: Int, inv: PlayerInventory, tile: StorageTile? -> StorageContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "storage_block"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<FluidStorageContainer, FluidStorageTile>(PositionalTileContainerFactory.Factory<FluidStorageContainer, FluidStorageTile> { windowId: Int, inv: PlayerInventory, tile: FluidStorageTile? -> FluidStorageContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "fluid_storage_block"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<ExternalStorageContainer, ExternalStorageTile>(PositionalTileContainerFactory.Factory<ExternalStorageContainer, ExternalStorageTile> { windowId: Int, inv: PlayerInventory, tile: ExternalStorageTile? -> ExternalStorageContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "external_storage"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<ImporterContainer, ImporterTile>(PositionalTileContainerFactory.Factory<ImporterContainer, ImporterTile> { windowId: Int, inv: PlayerInventory, tile: ImporterTile? -> ImporterContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "importer"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<ExporterContainer, ExporterTile>(PositionalTileContainerFactory.Factory<ExporterContainer, ExporterTile> { windowId: Int, inv: PlayerInventory, tile: ExporterTile? -> ExporterContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "exporter"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<NetworkTransmitterContainer, NetworkTransmitterTile>(PositionalTileContainerFactory.Factory<NetworkTransmitterContainer, NetworkTransmitterTile> { windowId: Int, inv: PlayerInventory, tile: NetworkTransmitterTile? -> NetworkTransmitterContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "network_transmitter"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<RelayContainer, RelayTile>(PositionalTileContainerFactory.Factory<RelayContainer, RelayTile> { windowId: Int, inv: PlayerInventory, tile: RelayTile? -> RelayContainer(tile, inv.player, windowId) })).setRegistryName(RS.ID, "relay"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<DetectorContainer, DetectorTile>(PositionalTileContainerFactory.Factory<DetectorContainer, DetectorTile> { windowId: Int, inv: PlayerInventory, tile: DetectorTile? -> DetectorContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "detector"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<SecurityManagerContainer, SecurityManagerTile>(PositionalTileContainerFactory.Factory<SecurityManagerContainer, SecurityManagerTile> { windowId: Int, inv: PlayerInventory, tile: SecurityManagerTile? -> SecurityManagerContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "security_manager"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<InterfaceContainer, InterfaceTile>(PositionalTileContainerFactory.Factory<InterfaceContainer, InterfaceTile> { windowId: Int, inv: PlayerInventory, tile: InterfaceTile? -> InterfaceContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "interface"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<FluidInterfaceContainer, FluidInterfaceTile>(PositionalTileContainerFactory.Factory<FluidInterfaceContainer, FluidInterfaceTile> { windowId: Int, inv: PlayerInventory, tile: FluidInterfaceTile? -> FluidInterfaceContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "fluid_interface"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<WirelessTransmitterContainer, WirelessTransmitterTile>(PositionalTileContainerFactory.Factory<WirelessTransmitterContainer, WirelessTransmitterTile> { windowId: Int, inv: PlayerInventory, tile: WirelessTransmitterTile? -> WirelessTransmitterContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "wireless_transmitter"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<StorageMonitorContainer, StorageMonitorTile>(PositionalTileContainerFactory.Factory<StorageMonitorContainer, StorageMonitorTile> { windowId: Int, inv: PlayerInventory, tile: StorageMonitorTile? -> StorageMonitorContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "storage_monitor"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<ConstructorContainer, ConstructorTile>(PositionalTileContainerFactory.Factory<ConstructorContainer, ConstructorTile> { windowId: Int, inv: PlayerInventory, tile: ConstructorTile? -> ConstructorContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "constructor"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<DestructorContainer, DestructorTile>(PositionalTileContainerFactory.Factory<DestructorContainer, DestructorTile> { windowId: Int, inv: PlayerInventory, tile: DestructorTile? -> DestructorContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "destructor"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<DiskManipulatorContainer, DiskManipulatorTile>(PositionalTileContainerFactory.Factory<DiskManipulatorContainer, DiskManipulatorTile> { windowId: Int, inv: PlayerInventory, tile: DiskManipulatorTile? -> DiskManipulatorContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "disk_manipulator"))
        e.getRegistry().register(IForgeContainerType.create(PositionalTileContainerFactory<CrafterContainer, CrafterTile>(PositionalTileContainerFactory.Factory<CrafterContainer, CrafterTile> { windowId: Int, inv: PlayerInventory, tile: CrafterTile? -> CrafterContainer(tile!!, inv.player, windowId) })).setRegistryName(RS.ID, "crafter"))
        e.getRegistry().register(IForgeContainerType.create(CrafterManagerContainerFactory()).setRegistryName(RS.ID, "crafter_manager"))
        e.getRegistry().register(IForgeContainerType.create(CraftingMonitorContainerFactory()).setRegistryName(RS.ID, "crafting_monitor"))
        e.getRegistry().register(IForgeContainerType.create(WirelessCraftingMonitorContainerFactory()).setRegistryName(RS.ID, "wireless_crafting_monitor"))
    }

    @SubscribeEvent
    fun onRegisterItems(e: Register<Item?>) {
        e.getRegistry().register(CoreItem(CoreItem.Type.CONSTRUCTION))
        e.getRegistry().register(CoreItem(CoreItem.Type.DESTRUCTION))
        e.getRegistry().register(QuartzEnrichedIronItem())
        e.getRegistry().register(ProcessorBindingItem())
        for (type in ProcessorItem.Type.values()) {
            e.getRegistry().register(ProcessorItem(type))
        }
        e.getRegistry().register(SiliconItem())
        e.getRegistry().register(SecurityCardItem())
        e.getRegistry().register(NetworkCardItem())
        for (type in ItemStorageType.values()) {
            if (type !== ItemStorageType.CREATIVE) {
                e.getRegistry().register(StoragePartItem(type))
            }
            e.getRegistry().register(StorageDiskItem(type))
        }
        for (type in FluidStorageType.values()) {
            if (type !== FluidStorageType.CREATIVE) {
                e.getRegistry().register(FluidStoragePartItem(type))
            }
            e.getRegistry().register(FluidStorageDiskItem(type))
        }
        e.getRegistry().register(StorageHousingItem())
        for (type in UpgradeItem.Type.values()) {
            e.getRegistry().register(UpgradeItem(type))
        }
        e.getRegistry().register(WrenchItem())
        e.getRegistry().register(PatternItem())
        e.getRegistry().register(FilterItem())
        e.getRegistry().register(PortableGridBlockItem(PortableGridBlockItem.Type.NORMAL))
        e.getRegistry().register(PortableGridBlockItem(PortableGridBlockItem.Type.CREATIVE))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.QUARTZ_ENRICHED_IRON))
        e.getRegistry().register(ControllerBlockItem(RSBlocks.CONTROLLER))
        e.getRegistry().register(ControllerBlockItem(RSBlocks.CREATIVE_CONTROLLER))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.MACHINE_CASING))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.CABLE))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.DISK_DRIVE))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.GRID))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.CRAFTING_GRID))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.PATTERN_GRID))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.FLUID_GRID))
        e.getRegistry().register(StorageBlockItem(RSBlocks.ONE_K_STORAGE_BLOCK))
        e.getRegistry().register(StorageBlockItem(RSBlocks.FOUR_K_STORAGE_BLOCK))
        e.getRegistry().register(StorageBlockItem(RSBlocks.SIXTEEN_K_STORAGE_BLOCK))
        e.getRegistry().register(StorageBlockItem(RSBlocks.SIXTY_FOUR_K_STORAGE_BLOCK))
        e.getRegistry().register(StorageBlockItem(RSBlocks.CREATIVE_STORAGE_BLOCK))
        e.getRegistry().register(FluidStorageBlockItem(RSBlocks.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK))
        e.getRegistry().register(FluidStorageBlockItem(RSBlocks.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK))
        e.getRegistry().register(FluidStorageBlockItem(RSBlocks.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK))
        e.getRegistry().register(FluidStorageBlockItem(RSBlocks.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK))
        e.getRegistry().register(FluidStorageBlockItem(RSBlocks.CREATIVE_FLUID_STORAGE_BLOCK))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.EXTERNAL_STORAGE))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.IMPORTER))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.EXPORTER))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.NETWORK_RECEIVER))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.NETWORK_TRANSMITTER))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.RELAY))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.DETECTOR))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.SECURITY_MANAGER))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.INTERFACE))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.FLUID_INTERFACE))
        e.getRegistry().register(WirelessTransmitterBlockItem())
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.STORAGE_MONITOR))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.CONSTRUCTOR))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.DESTRUCTOR))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.DISK_MANIPULATOR))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.CRAFTER))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.CRAFTER_MANAGER))
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.CRAFTING_MONITOR))
        e.getRegistry().register(WirelessGridItem(WirelessGridItem.Type.NORMAL))
        e.getRegistry().register(WirelessGridItem(WirelessGridItem.Type.CREATIVE))
        e.getRegistry().register(WirelessFluidGridItem(WirelessFluidGridItem.Type.NORMAL))
        e.getRegistry().register(WirelessFluidGridItem(WirelessFluidGridItem.Type.CREATIVE))
        e.getRegistry().register(WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.NORMAL))
        e.getRegistry().register(WirelessCraftingMonitorItem(WirelessCraftingMonitorItem.Type.CREATIVE))
    }
}