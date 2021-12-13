package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.config.ClientConfig;
import com.refinedmods.refinedstorage.config.ServerConfig;
import com.refinedmods.refinedstorage.datageneration.DataGenerators;
import com.refinedmods.refinedstorage.integration.curios.CuriosIntegration;
import com.refinedmods.refinedstorage.item.group.MainItemGroup;
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
    public static final CreativeModeTab MAIN_GROUP = new MainItemGroup();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();

    public RS() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientSetup::new);

        MinecraftForge.EVENT_BUS.register(new ServerSetup());

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());

        CommonSetup commonSetup = new CommonSetup();
        RSBlocks.register();
        RSItems.register();
        RSLootFunctions.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(commonSetup::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(BlockEntityType.class, commonSetup::onRegisterBlockEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(RecipeSerializer.class, commonSetup::onRegisterRecipeSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(MenuType.class, commonSetup::onRegisterContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(commonSetup::onRegisterCapabilities);
        FMLJavaModLoadingContext.get().getModEventBus().register(new DataGenerators());
        FMLJavaModLoadingContext.get().getModEventBus().register(new CuriosIntegration());

        API.deliver();
    }
}
