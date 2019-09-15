package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.FluidStorageType;
import com.raoulvdberge.refinedstorage.apiimpl.storage.ItemStorageType;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryItem;
import com.raoulvdberge.refinedstorage.item.*;
import com.raoulvdberge.refinedstorage.item.group.MainItemGroup;
import com.raoulvdberge.refinedstorage.network.NetworkHandler;
import com.raoulvdberge.refinedstorage.recipe.UpgradeWithEnchantedBookRecipeSerializer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RS.ID)
public final class RS {
    public static final String ID = "refinedstorage";

    public static RS INSTANCE;
    public RSConfig config;

    public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();
    public static final ItemGroup MAIN_GROUP = new MainItemGroup();

    public RS() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::onRegisterItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, this::onRegisterRecipeSerializers);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent e) {
        NETWORK_HANDLER.register();

        API.instance().getStorageDiskRegistry().add(StorageDiskFactoryItem.ID, new StorageDiskFactoryItem());
        API.instance().getStorageDiskRegistry().add(StorageDiskFactoryFluid.ID, new StorageDiskFactoryFluid());
    }

    @SubscribeEvent
    public void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> e) {
        e.getRegistry().register(new UpgradeWithEnchantedBookRecipeSerializer().setRegistryName(RS.ID, "upgrade_with_enchanted_book"));
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> e) {

    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new ItemCore(ItemCore.Type.CONSTRUCTION));
        e.getRegistry().register(new ItemCore(ItemCore.Type.DESTRUCTION));
        e.getRegistry().register(new ItemQuartzEnrichedIron());
        e.getRegistry().register(new ItemProcessorBinding());

        for (ItemProcessor.Type type : ItemProcessor.Type.values()) {
            e.getRegistry().register(new ItemProcessor(type));
        }

        e.getRegistry().register(new ItemSilicon());

        e.getRegistry().register(new ItemSecurityCard());
        e.getRegistry().register(new ItemNetworkCard());
        e.getRegistry().register(new ItemCuttingTool());

        for (ItemStorageType type : ItemStorageType.values()) {
            if (type != ItemStorageType.CREATIVE) {
                e.getRegistry().register(new ItemStoragePart(type));
            }

            e.getRegistry().register(new ItemStorageDisk(type));
        }

        for (FluidStorageType type : FluidStorageType.values()) {
            if (type != FluidStorageType.CREATIVE) {
                e.getRegistry().register(new ItemFluidStoragePart(type));
            }

            e.getRegistry().register(new ItemFluidStorageDisk(type));
        }

        e.getRegistry().register(new ItemStorageHousing());

        for (ItemUpgrade.Type type : ItemUpgrade.Type.values()) {
            e.getRegistry().register(new ItemUpgrade(type));
        }

        e.getRegistry().register(new ItemWrench());
        e.getRegistry().register(new ItemPattern());
    }

    /* TODO
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        config = new RSConfig(null, e.getSuggestedConfigurationFile());

        PROXY.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        PROXY.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandCreateDisk());
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent e) {
        FMLLog.bigWarning("Invalid fingerprint detected for the Refined Storage jar file! The file " + e.getSource().getName() + " may have been tampered with. This version will NOT be supported!");
    }*/
}
