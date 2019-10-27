package com.raoulvdberge.refinedstorage.proxy;

public class ProxyCommon {
    /* TODO
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);

        API.deliver(e.getAsmData());

        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementItemRender.ID, buf -> new CraftingMonitorElementItemRender(StackUtils.readItemStack(buf), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementFluidRender.ID, buf -> new CraftingMonitorElementFluidRender(StackUtils.readFluidStack(buf), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementError.ID, buf -> {
            String id = ByteBufUtils.readUTF8String(buf);
            String message = ByteBufUtils.readUTF8String(buf);

            return new CraftingMonitorElementError(API.instance().getCraftingMonitorElementRegistry().get(id).apply(buf), message);
        });

        IntegrationInventorySorter.register();
    }

    public void init(FMLInitializationEvent e) {
        if (IntegrationOC.isLoaded()) {
            DriverNetwork.register();
        }

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
