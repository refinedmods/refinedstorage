package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.config.ClientConfig;
import com.refinedmods.refinedstorage.config.ServerConfig;
import com.refinedmods.refinedstorage.datageneration.DataGenerators;
import com.refinedmods.refinedstorage.integration.curios.CuriosIntegration;
import com.refinedmods.refinedstorage.network.NetworkHandler;
import com.refinedmods.refinedstorage.setup.ClientSetup;
import com.refinedmods.refinedstorage.setup.CommonSetup;
import com.refinedmods.refinedstorage.setup.ServerSetup;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(RS.ID)
public final class RS {
    public static final String ID = "refinedstorage";
    public static final String NAME = "Refined Storage";

    public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();

    public RS(IEventBus eventBus) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            eventBus.addListener(ClientSetup::onClientSetup);
            eventBus.addListener(ClientSetup::registerMenuScreens);
            eventBus.addListener(ClientSetup::onModelBake);
            eventBus.addListener(ClientSetup::onRegisterAdditionalModels);
            eventBus.addListener(ClientSetup::onRegisterModelGeometry);
            eventBus.addListener(ClientSetup::onRegisterKeymappings);
            eventBus.addListener(ClientSetup::onRegisterColorBindings);
            NeoForge.EVENT_BUS.addListener(ClientSetup::addReloadListener);
        }

        NeoForge.EVENT_BUS.register(new ServerSetup());

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());

        RSBlocks.register(eventBus);
        RSItems.register(eventBus);

        eventBus.addListener(CommonSetup::onCommonSetup);
        eventBus.addListener(CommonSetup::onRegister);
        eventBus.addListener(CommonSetup::onRegisterCapabilities);
        eventBus.addListener(CommonSetup::onRegisterNetworkPackets);
        eventBus.register(new DataGenerators());
        eventBus.register(new CuriosIntegration());

        RSContainerMenus.REGISTRY.register(eventBus);
        RSBlockEntities.REGISTRY.register(eventBus);
        RSRecipeSerializers.REGISTRY.register(eventBus);

        API.deliver();
    }
}
