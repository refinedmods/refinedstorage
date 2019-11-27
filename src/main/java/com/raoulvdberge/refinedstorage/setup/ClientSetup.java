package com.raoulvdberge.refinedstorage.setup;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.CrafterContainer;
import com.raoulvdberge.refinedstorage.container.CrafterManagerContainer;
import com.raoulvdberge.refinedstorage.container.slot.CrafterManagerSlot;
import com.raoulvdberge.refinedstorage.render.BakedModelOverrideRegistry;
import com.raoulvdberge.refinedstorage.render.color.PatternItemColor;
import com.raoulvdberge.refinedstorage.render.model.*;
import com.raoulvdberge.refinedstorage.render.resourcepack.ResourcePackListener;
import com.raoulvdberge.refinedstorage.render.tesr.StorageMonitorTileRenderer;
import com.raoulvdberge.refinedstorage.screen.*;
import com.raoulvdberge.refinedstorage.screen.factory.CrafterManagerScreenFactory;
import com.raoulvdberge.refinedstorage.screen.factory.GridScreenFactory;
import com.raoulvdberge.refinedstorage.tile.StorageMonitorTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup {
    private BakedModelOverrideRegistry bakedModelOverrideRegistry = new BakedModelOverrideRegistry();

    public ClientSetup() {
        IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) resourceManager).addReloadListener(new ResourcePackListener());
        }

        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "controller"), (base, registry) -> new FullbrightBakedModel(
            base,
            new ResourceLocation(RS.ID, "block/controller/cutouts/nearly_off"),
            new ResourceLocation(RS.ID, "block/controller/cutouts/nearly_on"),
            new ResourceLocation(RS.ID, "block/controller/cutouts/on")
        ));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "creative_controller"), (base, registry) -> new FullbrightBakedModel(
            base,
            new ResourceLocation(RS.ID, "block/controller/cutouts/nearly_off"),
            new ResourceLocation(RS.ID, "block/controller/cutouts/nearly_on"),
            new ResourceLocation(RS.ID, "block/controller/cutouts/on")
        ));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "grid"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/grid/cutouts/front_connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "crafting_grid"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/grid/cutouts/crafting_front_connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "pattern_grid"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/grid/cutouts/pattern_front_connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "fluid_grid"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/grid/cutouts/fluid_front_connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "network_receiver"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/network_receiver/cutouts/connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "network_transmitter"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/network_transmitter/cutouts/connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "relay"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/relay/cutouts/connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "detector"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/detector/cutouts/on")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "security_manager"), (base, registry) -> new FullbrightBakedModel(
            base,
            new ResourceLocation(RS.ID, "block/security_manager/cutouts/top_connected"),
            new ResourceLocation(RS.ID, "block/security_manager/cutouts/front_connected"),
            new ResourceLocation(RS.ID, "block/security_manager/cutouts/left_connected"),
            new ResourceLocation(RS.ID, "block/security_manager/cutouts/back_connected"),
            new ResourceLocation(RS.ID, "block/security_manager/cutouts/right_connected")
        ));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "wireless_transmitter"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/wireless_transmitter/cutouts/connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "constructor"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/constructor/cutouts/connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "destructor"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/destructor/cutouts/connected")));

        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "disk_drive"), (base, registry) -> new FullbrightBakedModel(
            new DiskDriveBakedModel(
                base,
                registry.get(new ResourceLocation(RS.ID + ":block/disks/disk")),
                registry.get(new ResourceLocation(RS.ID + ":block/disks/disk_near_capacity")),
                registry.get(new ResourceLocation(RS.ID + ":block/disks/disk_full")),
                registry.get(new ResourceLocation(RS.ID + ":block/disks/disk_disconnected"))
            ),
            new ResourceLocation(RS.ID, "block/disks/leds")
        ).disableCache());

        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "disk_manipulator"), (base, registry) -> new FullbrightBakedModel(
            new DiskManipulatorBakedModel(
                registry.get(new ResourceLocation(RS.ID + ":block/disk_manipulator_connected")),
                registry.get(new ResourceLocation(RS.ID + ":block/disk_manipulator_disconnected")),
                registry.get(new ResourceLocation(RS.ID + ":block/disks/disk")),
                registry.get(new ResourceLocation(RS.ID + ":block/disks/disk_near_capacity")),
                registry.get(new ResourceLocation(RS.ID + ":block/disks/disk_full")),
                registry.get(new ResourceLocation(RS.ID + ":block/disks/disk_disconnected"))
            ),
            new ResourceLocation(RS.ID, "block/disk_manipulator/cutouts/connected"),
            new ResourceLocation(RS.ID, "block/disks/leds")
        ).disableCache());

        for (String portableGridName : new String[]{"portable_grid", "creative_portable_grid"}) {
            bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, portableGridName), (base, registry) -> new FullbrightBakedModel(
                new PortableGridBakedModel(
                    registry.get(new ResourceLocation(RS.ID + ":block/portable_grid_connected")),
                    registry.get(new ResourceLocation(RS.ID + ":block/portable_grid_disconnected")),
                    registry.get(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk")),
                    registry.get(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_near_capacity")),
                    registry.get(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_full")),
                    registry.get(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_disconnected"))
                ),
                new ResourceLocation(RS.ID + ":block/disks/leds")
            ).disableCache());
        }

        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "crafter"), (base, registry) -> new FullbrightBakedModel(
            base,
            new ResourceLocation(RS.ID, "block/crafter/cutouts/side_connected"),
            new ResourceLocation(RS.ID, "block/crafter/cutouts/side_connected_90"),
            new ResourceLocation(RS.ID, "block/crafter/cutouts/side_connected_180"),
            new ResourceLocation(RS.ID, "block/crafter/cutouts/side_connected_270"),
            new ResourceLocation(RS.ID, "block/crafter/cutouts/front_connected")
        ));

        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "crafter_manager"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/crafter_manager/cutouts/front_connected")));
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "crafting_monitor"), (base, registry) -> new FullbrightBakedModel(base, new ResourceLocation(RS.ID, "block/crafting_monitor/cutouts/front_connected")));

        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "pattern"), (base, registry) -> new PatternBakedModel(base));

        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/disk"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/disk_near_capacity"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/disk_full"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/disk_disconnected"));

        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disk_manipulator_disconnected"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disk_manipulator_connected"));

        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/portable_grid_connected"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/portable_grid_disconnected"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_near_capacity"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_full"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_disconnected"));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);

        API.instance().addPatternRenderHandler(pattern -> Screen.hasShiftDown());
        API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getInstance().player.openContainer;

            if (container instanceof CrafterManagerContainer) {
                for (Slot slot : container.inventorySlots) {
                    if (slot instanceof CrafterManagerSlot && slot.getStack() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });
        API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getInstance().player.openContainer;

            if (container instanceof CrafterContainer) {
                for (int i = 0; i < 9; ++i) {
                    if (container.getSlot(i).getStack() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new KeyInputListener());

        ScreenManager.registerFactory(RSContainers.FILTER, FilterScreen::new);
        ScreenManager.registerFactory(RSContainers.CONTROLLER, ControllerScreen::new);
        ScreenManager.registerFactory(RSContainers.DISK_DRIVE, DiskDriveScreen::new);
        ScreenManager.registerFactory(RSContainers.GRID, new GridScreenFactory());
        ScreenManager.registerFactory(RSContainers.STORAGE_BLOCK, StorageBlockScreen::new);
        ScreenManager.registerFactory(RSContainers.FLUID_STORAGE_BLOCK, FluidStorageBlockScreen::new);
        ScreenManager.registerFactory(RSContainers.EXTERNAL_STORAGE, ExternalStorageScreen::new);
        ScreenManager.registerFactory(RSContainers.IMPORTER, ImporterScreen::new);
        ScreenManager.registerFactory(RSContainers.EXPORTER, ExporterScreen::new);
        ScreenManager.registerFactory(RSContainers.NETWORK_TRANSMITTER, NetworkTransmitterScreen::new);
        ScreenManager.registerFactory(RSContainers.RELAY, RelayScreen::new);
        ScreenManager.registerFactory(RSContainers.DETECTOR, DetectorScreen::new);
        ScreenManager.registerFactory(RSContainers.SECURITY_MANAGER, SecurityManagerScreen::new);
        ScreenManager.registerFactory(RSContainers.INTERFACE, InterfaceScreen::new);
        ScreenManager.registerFactory(RSContainers.FLUID_INTERFACE, FluidInterfaceScreen::new);
        ScreenManager.registerFactory(RSContainers.WIRELESS_TRANSMITTER, WirelessTransmitterScreen::new);
        ScreenManager.registerFactory(RSContainers.STORAGE_MONITOR, StorageMonitorScreen::new);
        ScreenManager.registerFactory(RSContainers.CONSTRUCTOR, ConstructorScreen::new);
        ScreenManager.registerFactory(RSContainers.DESTRUCTOR, DestructorScreen::new);
        ScreenManager.registerFactory(RSContainers.DISK_MANIPULATOR, DiskManipulatorScreen::new);
        ScreenManager.registerFactory(RSContainers.CRAFTER, CrafterScreen::new);
        ScreenManager.registerFactory(RSContainers.CRAFTER_MANAGER, new CrafterManagerScreenFactory());
        ScreenManager.registerFactory(RSContainers.CRAFTING_MONITOR, CraftingMonitorScreen::new);
        ScreenManager.registerFactory(RSContainers.WIRELESS_CRAFTING_MONITOR, CraftingMonitorScreen::new);

        ClientRegistry.registerKeyBinding(RSKeyBindings.FOCUS_SEARCH_BAR);
        ClientRegistry.registerKeyBinding(RSKeyBindings.CLEAR_GRID_CRAFTING_MATRIX);
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_GRID);
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_FLUID_GRID);
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR);
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_PORTABLE_GRID);

        ClientRegistry.bindTileEntitySpecialRenderer(StorageMonitorTile.class, new StorageMonitorTileRenderer());

        e.getMinecraftSupplier().get().getItemColors().register(new PatternItemColor(), RSItems.PATTERN);
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            BakedModelOverrideRegistry.BakedModelOverrideFactory factory = this.bakedModelOverrideRegistry.get(new ResourceLocation(id.getNamespace(), id.getPath()));

            if (factory != null) {
                e.getModelRegistry().put(id, factory.create(e.getModelRegistry().get(id), e.getModelRegistry()));
            }
        }
    }
}
