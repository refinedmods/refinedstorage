package com.refinedmods.refinedstorage.setup

import RegistryEvent.Register
import com.refinedmods.refinedstorage.*
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.container.*
import com.refinedmods.refinedstorage.container.slot.CrafterManagerSlot
import com.refinedmods.refinedstorage.item.property.ControllerItemPropertyGetter
import com.refinedmods.refinedstorage.item.property.NetworkItemPropertyGetter
import com.refinedmods.refinedstorage.item.property.SecurityCardItemPropertyGetter
import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry
import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry.BakedModelOverrideFactory
import com.refinedmods.refinedstorage.render.color.PatternItemColor
import com.refinedmods.refinedstorage.render.model.*
import com.refinedmods.refinedstorage.render.resourcepack.ResourcePackListener
import com.refinedmods.refinedstorage.screen.KeyInputListener
import com.refinedmods.refinedstorage.screen.factory.CrafterManagerScreenFactory
import com.refinedmods.refinedstorage.screen.factory.GridScreenFactory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScreenManager
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemModelsProperties
import net.minecraft.item.ItemStack
import net.minecraft.resources.IReloadableResourceManager
import net.minecraft.resources.IResourceManager
import net.minecraft.util.Identifier
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

class ClientSetup {
    private val bakedModelOverrideRegistry = BakedModelOverrideRegistry()
    @SubscribeEvent
    fun onClientSetup(e: FMLClientSetupEvent) {
        MinecraftForge.EVENT_BUS.register(KeyInputListener())
        ScreenManager.registerFactory(RSContainers.FILTER, { container: FilterContainer, inventory: PlayerInventory?, title: ? -> FilterScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.CONTROLLER, { container: ControllerContainer, inventory: PlayerInventory?, title: ? -> ControllerScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.DISK_DRIVE, { container: DiskDriveContainer, inventory: PlayerInventory?, title: ? -> DiskDriveScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.GRID, GridScreenFactory())
        ScreenManager.registerFactory(RSContainers.STORAGE_BLOCK, { container: StorageContainer, inventory: PlayerInventory?, title: ? -> StorageBlockScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.FLUID_STORAGE_BLOCK, { container: FluidStorageContainer, inventory: PlayerInventory?, title: ? -> FluidStorageBlockScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.EXTERNAL_STORAGE, { container: ExternalStorageContainer, inventory: PlayerInventory?, title: ? -> ExternalStorageScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.IMPORTER, { container: ImporterContainer, inventory: PlayerInventory?, title: ? -> ImporterScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.EXPORTER, { container: ExporterContainer, playerInventory: PlayerInventory?, title: ? -> ExporterScreen(container, playerInventory, title) })
        ScreenManager.registerFactory(RSContainers.NETWORK_TRANSMITTER, { container: NetworkTransmitterContainer, inventory: PlayerInventory?, title: ? -> NetworkTransmitterScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.RELAY, { container: RelayContainer, inventory: PlayerInventory?, title: ? -> RelayScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.DETECTOR, { container: DetectorContainer, inventory: PlayerInventory?, title: ? -> DetectorScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.SECURITY_MANAGER, { container: SecurityManagerContainer, inventory: PlayerInventory?, title: ? -> SecurityManagerScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.INTERFACE, { container: InterfaceContainer, inventory: PlayerInventory?, title: ? -> InterfaceScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.FLUID_INTERFACE, { container: FluidInterfaceContainer, inventory: PlayerInventory?, title: ? -> FluidInterfaceScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.WIRELESS_TRANSMITTER, { container: WirelessTransmitterContainer, inventory: PlayerInventory?, title: ? -> WirelessTransmitterScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.STORAGE_MONITOR, { container: StorageMonitorContainer, inventory: PlayerInventory?, title: ? -> StorageMonitorScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.CONSTRUCTOR, { container: ConstructorContainer, inventory: PlayerInventory?, title: ? -> ConstructorScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.DESTRUCTOR, { container: DestructorContainer, playerInventory: PlayerInventory?, title: ? -> DestructorScreen(container, playerInventory, title) })
        ScreenManager.registerFactory(RSContainers.DISK_MANIPULATOR, { container: DiskManipulatorContainer, playerInventory: PlayerInventory?, title: ? -> DiskManipulatorScreen(container, playerInventory, title) })
        ScreenManager.registerFactory(RSContainers.CRAFTER, { container: CrafterContainer, inventory: PlayerInventory?, title: ? -> CrafterScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.CRAFTER_MANAGER, CrafterManagerScreenFactory())
        ScreenManager.registerFactory(RSContainers.CRAFTING_MONITOR, { container: CraftingMonitorContainer, inventory: PlayerInventory?, title: ? -> CraftingMonitorScreen(container, inventory, title) })
        ScreenManager.registerFactory(RSContainers.WIRELESS_CRAFTING_MONITOR, { container: CraftingMonitorContainer, inventory: PlayerInventory?, title: ? -> CraftingMonitorScreen(container, inventory, title) })
        ClientRegistry.registerKeyBinding(RSKeyBindings.FOCUS_SEARCH_BAR)
        ClientRegistry.registerKeyBinding(RSKeyBindings.CLEAR_GRID_CRAFTING_MATRIX)
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_GRID)
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_FLUID_GRID)
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR)
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_PORTABLE_GRID)
        RenderTypeLookup.setRenderLayer(RSBlocks.CONTROLLER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.CREATIVE_CONTROLLER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.CABLE, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.CRAFTER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.CRAFTER_MANAGER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.CRAFTING_MONITOR, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.DETECTOR, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.DISK_MANIPULATOR, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.GRID, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.CRAFTING_GRID, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.PATTERN_GRID, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.FLUID_GRID, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.NETWORK_RECEIVER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.NETWORK_TRANSMITTER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.RELAY, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.SECURITY_MANAGER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.WIRELESS_TRANSMITTER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.IMPORTER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.EXPORTER, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.EXTERNAL_STORAGE, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.CONSTRUCTOR, RenderType.getCutout())
        RenderTypeLookup.setRenderLayer(RSBlocks.DESTRUCTOR, RenderType.getCutout())
        ClientRegistry.bindBlockEntityRenderer(RSTiles.STORAGE_MONITOR, { dispatcher: ? -> StorageMonitorTileRenderer(dispatcher) })
        e.getMinecraftSupplier().get().getItemColors().register(PatternItemColor(), RSItems.PATTERN)
        ItemModelsProperties.func_239418_a_(RSItems.SECURITY_CARD, Identifier("active"), SecurityCardItemPropertyGetter())
        ItemModelsProperties.func_239418_a_(RSItems.CONTROLLER, Identifier("energy_type"), ControllerItemPropertyGetter())
        ItemModelsProperties.func_239418_a_(RSItems.CREATIVE_CONTROLLER, Identifier("energy_type"), ControllerItemPropertyGetter())
        ItemModelsProperties.func_239418_a_(RSItems.WIRELESS_CRAFTING_MONITOR, Identifier("connected"), NetworkItemPropertyGetter())
        ItemModelsProperties.func_239418_a_(RSItems.CREATIVE_WIRELESS_CRAFTING_MONITOR, Identifier("connected"), NetworkItemPropertyGetter())
        ItemModelsProperties.func_239418_a_(RSItems.WIRELESS_GRID, Identifier("connected"), NetworkItemPropertyGetter())
        ItemModelsProperties.func_239418_a_(RSItems.CREATIVE_WIRELESS_GRID, Identifier("connected"), NetworkItemPropertyGetter())
        ItemModelsProperties.func_239418_a_(RSItems.WIRELESS_FLUID_GRID, Identifier("connected"), NetworkItemPropertyGetter())
        ItemModelsProperties.func_239418_a_(RSItems.CREATIVE_WIRELESS_FLUID_GRID, Identifier("connected"), NetworkItemPropertyGetter())
    }

    @SubscribeEvent
    fun onModelBake(e: ModelBakeEvent) {
        for (id in e.getModelRegistry().keySet()) {
            val factory = bakedModelOverrideRegistry[Identifier(id.getNamespace(), id.getPath())]
            if (factory != null) {
                e.getModelRegistry().put(id, factory.create(e.getModelRegistry().get(id), e.getModelRegistry()))
            }
        }
    }

    init {
        val minecraft: Minecraft = Minecraft.getInstance()
        if (minecraft != null) { // This is null in a runData environment.
            val resourceManager: IResourceManager = minecraft.getResourceManager()
            if (resourceManager is IReloadableResourceManager) {
                (resourceManager as IReloadableResourceManager).addReloadListener(ResourcePackListener())
            }
        }
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "controller"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? ->
            FullbrightBakedModel(
                    base,
                    true,
                    Identifier(RS.ID, "block/controller/cutouts/nearly_off"),
                    Identifier(RS.ID, "block/controller/cutouts/nearly_on"),
                    Identifier(RS.ID, "block/controller/cutouts/on")
            )
        })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "creative_controller"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? ->
            FullbrightBakedModel(
                    base,
                    true,
                    Identifier(RS.ID, "block/controller/cutouts/nearly_off"),
                    Identifier(RS.ID, "block/controller/cutouts/nearly_on"),
                    Identifier(RS.ID, "block/controller/cutouts/on")
            )
        })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "grid"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/grid/cutouts/front_connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "crafting_grid"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/grid/cutouts/crafting_front_connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "pattern_grid"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/grid/cutouts/pattern_front_connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "fluid_grid"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/grid/cutouts/fluid_front_connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "network_receiver"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/network_receiver/cutouts/connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "network_transmitter"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/network_transmitter/cutouts/connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "relay"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/relay/cutouts/connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "detector"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/detector/cutouts/on")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "security_manager"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? ->
            FullbrightBakedModel(
                    base,
                    true,
                    Identifier(RS.ID, "block/security_manager/cutouts/top_connected"),
                    Identifier(RS.ID, "block/security_manager/cutouts/front_connected"),
                    Identifier(RS.ID, "block/security_manager/cutouts/left_connected"),
                    Identifier(RS.ID, "block/security_manager/cutouts/back_connected"),
                    Identifier(RS.ID, "block/security_manager/cutouts/right_connected")
            )
        })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "wireless_transmitter"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/wireless_transmitter/cutouts/connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "constructor"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/constructor/cutouts/connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "destructor"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/destructor/cutouts/connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "disk_drive"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?> ->
            FullbrightBakedModel(
                    DiskDriveBakedModel(
                            base,
                            registry[Identifier(RS.ID + ":block/disks/disk")],
                            registry[Identifier(RS.ID + ":block/disks/disk_near_capacity")],
                            registry[Identifier(RS.ID + ":block/disks/disk_full")],
                            registry[Identifier(RS.ID + ":block/disks/disk_disconnected")]
                    ),
                    false,
                    Identifier(RS.ID, "block/disks/leds")
            )
        })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "disk_manipulator"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?> ->
            FullbrightBakedModel(
                    DiskManipulatorBakedModel(
                            registry[Identifier(RS.ID + ":block/disk_manipulator_connected")],
                            registry[Identifier(RS.ID + ":block/disk_manipulator_disconnected")],
                            registry[Identifier(RS.ID + ":block/disks/disk")],
                            registry[Identifier(RS.ID + ":block/disks/disk_near_capacity")],
                            registry[Identifier(RS.ID + ":block/disks/disk_full")],
                            registry[Identifier(RS.ID + ":block/disks/disk_disconnected")]
                    ),
                    false,
                    Identifier(RS.ID, "block/disk_manipulator/cutouts/connected"),
                    Identifier(RS.ID, "block/disks/leds")
            )
        })
        for (portableGridName in arrayOf("portable_grid", "creative_portable_grid")) {
            bakedModelOverrideRegistry.add(Identifier(RS.ID, portableGridName), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?> ->
                FullbrightBakedModel(
                        PortableGridBakedModel(
                                registry[Identifier(RS.ID + ":block/portable_grid_connected")],
                                registry[Identifier(RS.ID + ":block/portable_grid_disconnected")],
                                registry[Identifier(RS.ID + ":block/disks/portable_grid_disk")],
                                registry[Identifier(RS.ID + ":block/disks/portable_grid_disk_near_capacity")],
                                registry[Identifier(RS.ID + ":block/disks/portable_grid_disk_full")],
                                registry[Identifier(RS.ID + ":block/disks/portable_grid_disk_disconnected")]
                        ),
                        false,
                        Identifier(RS.ID + ":block/disks/leds")
                )
            })
        }
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "crafter"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? ->
            FullbrightBakedModel(
                    base,
                    true,
                    Identifier(RS.ID, "block/crafter/cutouts/side_connected"),
                    Identifier(RS.ID, "block/crafter/cutouts/side_connected_90"),
                    Identifier(RS.ID, "block/crafter/cutouts/side_connected_180"),
                    Identifier(RS.ID, "block/crafter/cutouts/side_connected_270"),
                    Identifier(RS.ID, "block/crafter/cutouts/front_connected")
            )
        })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "crafter_manager"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/crafter_manager/cutouts/front_connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "crafting_monitor"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> FullbrightBakedModel(base, true, Identifier(RS.ID, "block/crafting_monitor/cutouts/front_connected")) })
        bakedModelOverrideRegistry.add(Identifier(RS.ID, "pattern"), BakedModelOverrideFactory { base: error.NonExistentClass?, registry: Map<error.NonExistentClass?, error.NonExistentClass?>? -> PatternBakedModel(base) })
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disks/disk"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disks/disk_near_capacity"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disks/disk_full"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disks/disk_disconnected"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disk_manipulator_disconnected"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disk_manipulator_connected"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/portable_grid_connected"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/portable_grid_disconnected"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disks/portable_grid_disk"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disks/portable_grid_disk_near_capacity"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disks/portable_grid_disk_full"))
        ModelLoader.addSpecialModel(Identifier(RS.ID + ":block/disks/portable_grid_disk_disconnected"))
        FMLJavaModLoadingContext.get().getModEventBus().addListener({ e: FMLClientSetupEvent -> onClientSetup(e) })
        FMLJavaModLoadingContext.get().getModEventBus().addListener({ e: ModelBakeEvent -> onModelBake(e) })
        instance().addPatternRenderHandler(ICraftingPatternRenderHandler { pattern: ItemStack? -> Screen.hasShiftDown() })
        instance().addPatternRenderHandler(ICraftingPatternRenderHandler { pattern: ItemStack ->
            val container: Container = Minecraft.getInstance().player.openContainer
            if (container is CrafterManagerContainer) {
                for (slot in container.inventorySlots) {
                    if (slot is CrafterManagerSlot && slot.getStack() === pattern) {
                        return@addPatternRenderHandler true
                    }
                }
            }
            false
        })
        instance().addPatternRenderHandler(ICraftingPatternRenderHandler { pattern: ItemStack ->
            val container: Container = Minecraft.getInstance().player.openContainer
            if (container is CrafterContainer) {
                for (i in 0..8) {
                    if (container.getSlot(i).getStack() === pattern) {
                        return@addPatternRenderHandler true
                    }
                }
            }
            false
        })
    }
}