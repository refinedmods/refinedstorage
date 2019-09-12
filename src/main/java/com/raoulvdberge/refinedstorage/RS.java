package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.item.*;
import com.raoulvdberge.refinedstorage.item.group.MainItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RS.ID)
public final class RS {
    public static final String ID = "refinedstorage";

    public static RS INSTANCE;
    public RSConfig config;

    public static final ItemGroup MAIN_GROUP = new MainItemGroup();

    public RS() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::onRegisterItems);
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

        e.getRegistry().register(new ItemProcessor(ItemProcessor.Type.RAW_BASIC));
        e.getRegistry().register(new ItemProcessor(ItemProcessor.Type.RAW_IMPROVED));
        e.getRegistry().register(new ItemProcessor(ItemProcessor.Type.RAW_ADVANCED));
        e.getRegistry().register(new ItemProcessor(ItemProcessor.Type.BASIC));
        e.getRegistry().register(new ItemProcessor(ItemProcessor.Type.IMPROVED));
        e.getRegistry().register(new ItemProcessor(ItemProcessor.Type.ADVANCED));

        e.getRegistry().register(new ItemSilicon());

        e.getRegistry().register(new ItemSecurityCard());
        e.getRegistry().register(new ItemNetworkCard());
    }

    /* TODO
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        config = new RSConfig(null, e.getSuggestedConfigurationFile());

        PROXY.preInit(e);

        NetworkHooks.openGui();
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
