package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.config.ClientConfig;
import com.refinedmods.refinedstorage.config.ServerConfig;
import com.refinedmods.refinedstorage.datageneration.DataGenerators;
import com.refinedmods.refinedstorage.integration.curios.CuriosIntegration;
import com.refinedmods.refinedstorage.item.group.MainCreativeModeTab;
import com.refinedmods.refinedstorage.network.NetworkHandler;
import com.refinedmods.refinedstorage.setup.ClientSetup;
import com.refinedmods.refinedstorage.setup.CommonSetup;
import com.refinedmods.refinedstorage.setup.ServerSetup;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RS.ID)
public final class RS {
    public static final String ID = "refinedstorage";
    public static final String NAME = "Refined Storage";

    public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();
    public static final CreativeModeTab CREATIVE_MODE_TAB = new MainCreativeModeTab();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();

    public RS() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onClientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onModelBake);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onModelRegistry);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onTextureStitch);
            MinecraftForge.EVENT_BUS.addListener(ClientSetup::addReloadListener);
        });

        MinecraftForge.EVENT_BUS.register(new ServerSetup());

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());

        RSBlocks.register();
        RSItems.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(BlockEntityType.class, CommonSetup::onRegisterBlockEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(RecipeSerializer.class, CommonSetup::onRegisterRecipeSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(MenuType.class, CommonSetup::onRegisterMenus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::onRegisterCapabilities);
        FMLJavaModLoadingContext.get().getModEventBus().register(new DataGenerators());
        FMLJavaModLoadingContext.get().getModEventBus().register(new CuriosIntegration());

        API.deliver();
    }
}
