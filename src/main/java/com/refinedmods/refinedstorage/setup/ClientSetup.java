package com.refinedmods.refinedstorage.setup;

import com.refinedmods.refinedstorage.*;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.refinedmods.refinedstorage.container.CrafterContainerMenu;
import com.refinedmods.refinedstorage.container.CrafterManagerContainerMenu;
import com.refinedmods.refinedstorage.container.slot.CrafterManagerSlot;
import com.refinedmods.refinedstorage.item.property.ControllerItemPropertyGetter;
import com.refinedmods.refinedstorage.item.property.NetworkItemPropertyGetter;
import com.refinedmods.refinedstorage.item.property.SecurityCardItemPropertyGetter;
import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry;
import com.refinedmods.refinedstorage.render.blockentity.StorageMonitorBlockEntityRenderer;
import com.refinedmods.refinedstorage.render.color.PatternItemColor;
import com.refinedmods.refinedstorage.render.model.*;
import com.refinedmods.refinedstorage.render.model.baked.DiskDriveBakedModel;
import com.refinedmods.refinedstorage.render.model.baked.DiskManipulatorBakedModel;
import com.refinedmods.refinedstorage.render.model.baked.PatternBakedModel;
import com.refinedmods.refinedstorage.render.model.baked.PortableGridBakedModel;
import com.refinedmods.refinedstorage.render.resourcepack.ResourcePackListener;
import com.refinedmods.refinedstorage.screen.*;
import com.refinedmods.refinedstorage.screen.factory.CrafterManagerScreenFactory;
import com.refinedmods.refinedstorage.screen.factory.GridScreenFactory;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Arrays;
import java.util.function.BiConsumer;

public final class ClientSetup {
    private static final ResourceLocation DISK_RESOURCE = new ResourceLocation(RS.ID + ":block/disks/disk");
    private static final ResourceLocation DISK_NEAR_CAPACITY_RESOURCE = new ResourceLocation(RS.ID + ":block/disks/disk_near_capacity");
    private static final ResourceLocation DISK_FULL_RESOURCE = new ResourceLocation(RS.ID + ":block/disks/disk_full");
    private static final ResourceLocation DISK_DISCONNECTED_RESOURCE = new ResourceLocation(RS.ID + ":block/disks/disk_disconnected");

    private static final ResourceLocation CONNECTED = new ResourceLocation("connected");

    private static final BakedModelOverrideRegistry BAKED_MODEL_OVERRIDE_REGISTRY = new BakedModelOverrideRegistry();

    private static ResourceLocation[] getMultipleColoredModels(DyeColor color, String... paths) {
        return Arrays.stream(paths).map(path -> getColoredModel(color, path)).toArray(ResourceLocation[]::new);
    }

    private static ResourceLocation getColoredModel(DyeColor color, String path) {
        return new ResourceLocation(RS.ID, path + color);
    }

    private static void forEachColorApply(String name, BiConsumer<ResourceLocation, DyeColor> consumer) {
        for (DyeColor color : DyeColor.values()) {
            String prefix = color == ColorMap.DEFAULT_COLOR ? "" : color + "_";
            consumer.accept(new ResourceLocation(RS.ID, prefix + name), color);
        }
    }

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new KeyInputListener());

        registerBakedModelOverrides();
        registerPatternRenderHandlers();

        // MenuScreens isn't thread safe
        e.enqueueWork(() -> {
            MenuScreens.register(RSContainerMenus.FILTER, FilterScreen::new);
            MenuScreens.register(RSContainerMenus.CONTROLLER, ControllerScreen::new);
            MenuScreens.register(RSContainerMenus.DISK_DRIVE, DiskDriveScreen::new);
            MenuScreens.register(RSContainerMenus.GRID, new GridScreenFactory());
            MenuScreens.register(RSContainerMenus.STORAGE_BLOCK, StorageBlockScreen::new);
            MenuScreens.register(RSContainerMenus.FLUID_STORAGE_BLOCK, FluidStorageBlockScreen::new);
            MenuScreens.register(RSContainerMenus.EXTERNAL_STORAGE, ExternalStorageScreen::new);
            MenuScreens.register(RSContainerMenus.IMPORTER, ImporterScreen::new);
            MenuScreens.register(RSContainerMenus.EXPORTER, ExporterScreen::new);
            MenuScreens.register(RSContainerMenus.NETWORK_TRANSMITTER, NetworkTransmitterScreen::new);
            MenuScreens.register(RSContainerMenus.RELAY, RelayScreen::new);
            MenuScreens.register(RSContainerMenus.DETECTOR, DetectorScreen::new);
            MenuScreens.register(RSContainerMenus.SECURITY_MANAGER, SecurityManagerScreen::new);
            MenuScreens.register(RSContainerMenus.INTERFACE, InterfaceScreen::new);
            MenuScreens.register(RSContainerMenus.FLUID_INTERFACE, FluidInterfaceScreen::new);
            MenuScreens.register(RSContainerMenus.WIRELESS_TRANSMITTER, WirelessTransmitterScreen::new);
            MenuScreens.register(RSContainerMenus.STORAGE_MONITOR, StorageMonitorScreen::new);
            MenuScreens.register(RSContainerMenus.CONSTRUCTOR, ConstructorScreen::new);
            MenuScreens.register(RSContainerMenus.DESTRUCTOR, DestructorScreen::new);
            MenuScreens.register(RSContainerMenus.DISK_MANIPULATOR, DiskManipulatorScreen::new);
            MenuScreens.register(RSContainerMenus.CRAFTER, CrafterScreen::new);
            MenuScreens.register(RSContainerMenus.CRAFTER_MANAGER, new CrafterManagerScreenFactory());
            MenuScreens.register(RSContainerMenus.CRAFTING_MONITOR, CraftingMonitorScreen::new);
            MenuScreens.register(RSContainerMenus.WIRELESS_CRAFTING_MONITOR, CraftingMonitorScreen::new);
        });

        ClientRegistry.registerKeyBinding(RSKeyBindings.FOCUS_SEARCH_BAR);
        ClientRegistry.registerKeyBinding(RSKeyBindings.CLEAR_GRID_CRAFTING_MATRIX);
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_GRID);
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_FLUID_GRID);
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR);
        ClientRegistry.registerKeyBinding(RSKeyBindings.OPEN_PORTABLE_GRID);

        // RenderLayer isn't thread safe
        e.enqueueWork(() -> {
            RSBlocks.CONTROLLER.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.CREATIVE_CONTROLLER.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.CRAFTER.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.CRAFTER_MANAGER.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.CRAFTING_MONITOR.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.DETECTOR.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.DISK_MANIPULATOR.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.GRID.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.CRAFTING_GRID.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.PATTERN_GRID.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.FLUID_GRID.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.NETWORK_RECEIVER.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.NETWORK_TRANSMITTER.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.RELAY.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.SECURITY_MANAGER.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            RSBlocks.WIRELESS_TRANSMITTER.values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            ItemBlockRenderTypes.setRenderLayer(RSBlocks.CABLE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RSBlocks.IMPORTER.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RSBlocks.EXPORTER.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RSBlocks.EXTERNAL_STORAGE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RSBlocks.CONSTRUCTOR.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RSBlocks.DESTRUCTOR.get(), RenderType.cutout());
        });

        BlockEntityRenderers.register(RSBlockEntities.STORAGE_MONITOR, ctx -> new StorageMonitorBlockEntityRenderer());

        // ItemProperties isn't thread safe
        e.enqueueWork(() -> {
            Minecraft.getInstance().getItemColors().register(new PatternItemColor(), RSItems.PATTERN.get());

            ItemProperties.register(RSItems.SECURITY_CARD.get(), new ResourceLocation("active"), new SecurityCardItemPropertyGetter());

            RSItems.CONTROLLER.values().forEach(controller -> ItemProperties.register(controller.get(), new ResourceLocation("energy_type"), new ControllerItemPropertyGetter()));
            RSItems.CREATIVE_CONTROLLER.values().forEach(controller -> ItemProperties.register(controller.get(), new ResourceLocation("energy_type"), new ControllerItemPropertyGetter()));

            ItemProperties.register(RSItems.WIRELESS_CRAFTING_MONITOR.get(), CONNECTED, new NetworkItemPropertyGetter());
            ItemProperties.register(RSItems.CREATIVE_WIRELESS_CRAFTING_MONITOR.get(), CONNECTED, new NetworkItemPropertyGetter());

            ItemProperties.register(RSItems.WIRELESS_GRID.get(), CONNECTED, new NetworkItemPropertyGetter());
            ItemProperties.register(RSItems.CREATIVE_WIRELESS_GRID.get(), CONNECTED, new NetworkItemPropertyGetter());

            ItemProperties.register(RSItems.WIRELESS_FLUID_GRID.get(), CONNECTED, new NetworkItemPropertyGetter());
            ItemProperties.register(RSItems.CREATIVE_WIRELESS_FLUID_GRID.get(), CONNECTED, new NetworkItemPropertyGetter());
        });
    }

    private static void registerPatternRenderHandlers() {
        API.instance().addPatternRenderHandler(pattern -> Screen.hasShiftDown());
        API.instance().addPatternRenderHandler(pattern -> {
            AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;

            if (container instanceof CrafterManagerContainerMenu) {
                for (Slot slot : container.slots) {
                    if (slot instanceof CrafterManagerSlot && slot.getItem() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });
        API.instance().addPatternRenderHandler(pattern -> {
            AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;

            if (container instanceof CrafterContainerMenu) {
                for (int i = 0; i < 9; ++i) {
                    if (container.getSlot(i).getItem() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });
    }

    // TODO: we have probably too much emissivity (when disconnected)

    private static void registerBakedModelOverrides() {
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "cable"), (base, registry) -> new BakedModelCableCover(base));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "exporter"), (base, registry) -> new BakedModelCableCover(base));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "importer"), (base, registry) -> new BakedModelCableCover(base));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "external_storage"), (base, registry) -> new BakedModelCableCover(base));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "cover"), (base, registry) -> new BakedModelCover(ItemStack.EMPTY, CoverType.NORMAL));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "hollow_cover"), (base, registry) -> new BakedModelCover(ItemStack.EMPTY, CoverType.HOLLOW));

        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "pattern"), (base, registry) -> new PatternBakedModel(base));
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new ResourcePackListener());
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent e) {
        ForgeModelBakery.addSpecialModel(DISK_RESOURCE);
        ForgeModelBakery.addSpecialModel(DISK_NEAR_CAPACITY_RESOURCE);
        ForgeModelBakery.addSpecialModel(DISK_FULL_RESOURCE);
        ForgeModelBakery.addSpecialModel(DISK_DISCONNECTED_RESOURCE);

        ForgeModelBakery.addSpecialModel(new ResourceLocation(RS.ID + ":block/disk_manipulator/disconnected"));

        for (DyeColor color : DyeColor.values()) {
            ForgeModelBakery.addSpecialModel(new ResourceLocation(RS.ID + ":block/disk_manipulator/" + color));
        }

        ForgeModelBakery.addSpecialModel(new ResourceLocation(RS.ID + ":block/portable_grid_connected"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(RS.ID + ":block/portable_grid_disconnected"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_near_capacity"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_full"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_disconnected"));
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e) {
        FullbrightBakedModel.invalidateCache();

        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            BakedModelOverrideRegistry.BakedModelOverrideFactory factory = BAKED_MODEL_OVERRIDE_REGISTRY.get(new ResourceLocation(id.getNamespace(), id.getPath()));

            if (factory != null) {
                e.getModelRegistry().put(id, factory.create(e.getModelRegistry().get(id), e.getModelRegistry()));
            }
        }
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
            event.addSprite(new ResourceLocation(RS.ID, "block/cable_part_border"));
        }
    }

    @SubscribeEvent
    public static void onRegisterModelGeometry(final ModelEvent.RegisterGeometryLoaders e) {
        e.register("disk_drive", new DiskDriveGeometryLoader());
        e.register("portable_grid", new PortableGridGeometryLoader());
    }
}
