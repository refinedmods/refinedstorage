package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.render.BakedModelOverrideRegistry;
import com.raoulvdberge.refinedstorage.render.model.baked.DiskDriveBakedModel;
import com.raoulvdberge.refinedstorage.render.model.baked.FullbrightBakedModel;
import com.raoulvdberge.refinedstorage.screen.ControllerScreen;
import com.raoulvdberge.refinedstorage.screen.DiskDriveScreen;
import com.raoulvdberge.refinedstorage.screen.FilterScreen;
import com.raoulvdberge.refinedstorage.screen.factory.GridScreenFactory;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup {
    private BakedModelOverrideRegistry bakedModelOverrideRegistry = new BakedModelOverrideRegistry();

    public ClientSetup() {
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

        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/disk"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/disk_near_capacity"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/disk_full"));
        ModelLoader.addSpecialModel(new ResourceLocation(RS.ID + ":block/disks/disk_disconnected"));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent e) {
        ScreenManager.registerFactory(RSContainers.FILTER, FilterScreen::new);
        ScreenManager.registerFactory(RSContainers.CONTROLLER, ControllerScreen::new);
        ScreenManager.registerFactory(RSContainers.DISK_DRIVE, DiskDriveScreen::new);
        ScreenManager.registerFactory(RSContainers.GRID, new GridScreenFactory());

        RSKeyBindings.init();
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
