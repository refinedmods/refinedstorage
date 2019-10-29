package com.raoulvdberge.refinedstorage.proxy;

public class ProxyCommon {
    /* TODO
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);

        API.deliver(e.getAsmData());

        IntegrationInventorySorter.register();
    }

    public void init(FMLInitializationEvent e) {
        if (IntegrationCraftingTweaks.isLoaded()) {
            IntegrationCraftingTweaks.register();
        }
    }

    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck e) {
        if (e.getTargetBlock().getBlock() instanceof BlockBase) {
            e.setCanHarvest(true); // Allow break without tool
        }
    }*/
}
