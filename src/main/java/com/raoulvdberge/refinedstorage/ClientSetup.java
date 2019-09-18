package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.container.ControllerContainer;
import com.raoulvdberge.refinedstorage.render.BakedModelOverrideRegistry;
import com.raoulvdberge.refinedstorage.render.model.baked.FullbrightBakedModel;
import com.raoulvdberge.refinedstorage.screen.ControllerScreen;
import com.raoulvdberge.refinedstorage.screen.FilterScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Function;

public class ClientSetup {
    private BakedModelOverrideRegistry bakedModelOverrideRegistry = new BakedModelOverrideRegistry();

    public ClientSetup() {
        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "controller"), base -> new FullbrightBakedModel(
            base,
            new ResourceLocation(RS.ID, "block/controller/cutouts/nearly_off"),
            new ResourceLocation(RS.ID, "block/controller/cutouts/nearly_on"),
            new ResourceLocation(RS.ID, "block/controller/cutouts/on")
        ));

        bakedModelOverrideRegistry.add(new ResourceLocation(RS.ID, "creative_controller"), base -> new FullbrightBakedModel(
            base,
            new ResourceLocation(RS.ID, "block/controller/cutouts/nearly_off"),
            new ResourceLocation(RS.ID, "block/controller/cutouts/nearly_on"),
            new ResourceLocation(RS.ID, "block/controller/cutouts/on")
        ));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent e) {
        ScreenManager.registerFactory(RSContainers.FILTER, FilterScreen::new);
        ScreenManager.registerFactory(RSContainers.CONTROLLER, new ScreenManager.IScreenFactory<ControllerContainer, ControllerScreen>() {
            @Override
            public ControllerScreen create(ControllerContainer p_create_1_, PlayerInventory p_create_2_, ITextComponent p_create_3_) {
                return new ControllerScreen(p_create_1_, p_create_2_, p_create_3_);
            }
        });
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            Function<IBakedModel, IBakedModel> factory = this.bakedModelOverrideRegistry.get(new ResourceLocation(id.getNamespace(), id.getPath()));

            if (factory != null) {
                e.getModelRegistry().put(id, factory.apply(e.getModelRegistry().get(id)));
            }
        }
    }
}
