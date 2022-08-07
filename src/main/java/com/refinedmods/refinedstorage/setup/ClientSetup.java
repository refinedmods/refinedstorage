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
import com.refinedmods.refinedstorage.render.model.baked.CableCoverItemBakedModel;
import com.refinedmods.refinedstorage.render.model.DiskDriveGeometryLoader;
import com.refinedmods.refinedstorage.render.model.PortableGridGeometryLoader;
import com.refinedmods.refinedstorage.render.model.baked.CableCoverBakedModel;
import com.refinedmods.refinedstorage.render.model.baked.PatternBakedModel;
import com.refinedmods.refinedstorage.render.resourcepack.ResourcePackListener;
import com.refinedmods.refinedstorage.screen.*;
import com.refinedmods.refinedstorage.screen.factory.CrafterManagerScreenFactory;
import com.refinedmods.refinedstorage.screen.factory.GridScreenFactory;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
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
            MenuScreens.register(RSContainerMenus.FILTER.get(), FilterScreen::new);
            MenuScreens.register(RSContainerMenus.CONTROLLER.get(), ControllerScreen::new);
            MenuScreens.register(RSContainerMenus.DISK_DRIVE.get(), DiskDriveScreen::new);
            MenuScreens.register(RSContainerMenus.GRID.get(), new GridScreenFactory());
            MenuScreens.register(RSContainerMenus.STORAGE_BLOCK.get(), StorageBlockScreen::new);
            MenuScreens.register(RSContainerMenus.FLUID_STORAGE_BLOCK.get(), FluidStorageBlockScreen::new);
            MenuScreens.register(RSContainerMenus.EXTERNAL_STORAGE.get(), ExternalStorageScreen::new);
            MenuScreens.register(RSContainerMenus.IMPORTER.get(), ImporterScreen::new);
            MenuScreens.register(RSContainerMenus.EXPORTER.get(), ExporterScreen::new);
            MenuScreens.register(RSContainerMenus.NETWORK_TRANSMITTER.get(), NetworkTransmitterScreen::new);
            MenuScreens.register(RSContainerMenus.RELAY.get(), RelayScreen::new);
            MenuScreens.register(RSContainerMenus.DETECTOR.get(), DetectorScreen::new);
            MenuScreens.register(RSContainerMenus.SECURITY_MANAGER.get(), SecurityManagerScreen::new);
            MenuScreens.register(RSContainerMenus.INTERFACE.get(), InterfaceScreen::new);
            MenuScreens.register(RSContainerMenus.FLUID_INTERFACE.get(), FluidInterfaceScreen::new);
            MenuScreens.register(RSContainerMenus.WIRELESS_TRANSMITTER.get(), WirelessTransmitterScreen::new);
            MenuScreens.register(RSContainerMenus.STORAGE_MONITOR.get(), StorageMonitorScreen::new);
            MenuScreens.register(RSContainerMenus.CONSTRUCTOR.get(), ConstructorScreen::new);
            MenuScreens.register(RSContainerMenus.DESTRUCTOR.get(), DestructorScreen::new);
            MenuScreens.register(RSContainerMenus.DISK_MANIPULATOR.get(), DiskManipulatorScreen::new);
            MenuScreens.register(RSContainerMenus.CRAFTER.get(), CrafterScreen::new);
            MenuScreens.register(RSContainerMenus.CRAFTER_MANAGER.get(), new CrafterManagerScreenFactory());
            MenuScreens.register(RSContainerMenus.CRAFTING_MONITOR.get(), CraftingMonitorScreen::new);
            MenuScreens.register(RSContainerMenus.WIRELESS_CRAFTING_MONITOR.get(), CraftingMonitorScreen::new);
        });

        BlockEntityRenderers.register(RSBlockEntities.STORAGE_MONITOR, ctx -> new StorageMonitorBlockEntityRenderer());

        // ItemProperties isn't thread safe
        e.enqueueWork(() -> {
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
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "cable"), (base, registry) -> new CableCoverBakedModel(base));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "exporter"), (base, registry) -> new CableCoverBakedModel(base));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "importer"), (base, registry) -> new CableCoverBakedModel(base));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "external_storage"), (base, registry) -> new CableCoverBakedModel(base));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "cover"), (base, registry) -> new CableCoverItemBakedModel(ItemStack.EMPTY, CoverType.NORMAL));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "hollow_cover"), (base, registry) -> new CableCoverItemBakedModel(ItemStack.EMPTY, CoverType.HOLLOW));
        BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(RS.ID, "pattern"), (base, registry) -> new PatternBakedModel(base));
    }

    @SubscribeEvent
    public static void onRegisterColorBindings(RegisterColorHandlersEvent.Item e) {
        e.register(new PatternItemColor(), RSItems.PATTERN.get());
    }

    @SubscribeEvent
    public static void onRegisterKeymappings(RegisterKeyMappingsEvent e) {
        e.register(RSKeyBindings.FOCUS_SEARCH_BAR);
        e.register(RSKeyBindings.CLEAR_GRID_CRAFTING_MATRIX);
        e.register(RSKeyBindings.OPEN_WIRELESS_GRID);
        e.register(RSKeyBindings.OPEN_WIRELESS_FLUID_GRID);
        e.register(RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR);
        e.register(RSKeyBindings.OPEN_PORTABLE_GRID);
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new ResourcePackListener());
    }

    @SubscribeEvent
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional e) {
        e.register(DISK_RESOURCE);
        e.register(DISK_NEAR_CAPACITY_RESOURCE);
        e.register(DISK_FULL_RESOURCE);
        e.register(DISK_DISCONNECTED_RESOURCE);

        e.register(new ResourceLocation(RS.ID + ":block/disk_manipulator/disconnected"));

        for (DyeColor color : DyeColor.values()) {
            e.register(new ResourceLocation(RS.ID + ":block/disk_manipulator/" + color));
        }

        e.register(new ResourceLocation(RS.ID + ":block/portable_grid_connected"));
        e.register(new ResourceLocation(RS.ID + ":block/portable_grid_disconnected"));
        e.register(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk"));
        e.register(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_near_capacity"));
        e.register(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_full"));
        e.register(new ResourceLocation(RS.ID + ":block/disks/portable_grid_disk_disconnected"));
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.BakingCompleted e) {
        for (ResourceLocation id : e.getModels().keySet()) {
            BakedModelOverrideRegistry.BakedModelOverrideFactory factory = BAKED_MODEL_OVERRIDE_REGISTRY.get(new ResourceLocation(id.getNamespace(), id.getPath()));

            if (factory != null) {
                e.getModels().put(id, factory.create(e.getModels().get(id), e.getModels()));
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
