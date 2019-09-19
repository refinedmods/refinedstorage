package com.raoulvdberge.refinedstorage.proxy;

public class ProxyCommon {
    /* TODO
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);

        CapabilityNetworkNodeProxy.register();

        API.deliver(e.getAsmData());

        NetworkNodeGrid.FACTORY_ID = API.instance().getGridManager().add(new GridFactoryGridBlock());
        WirelessGrid.ID = API.instance().getGridManager().add(new GridFactoryWirelessGrid());
        WirelessFluidGrid.ID = API.instance().getGridManager().add(new GridFactoryWirelessFluidGrid());
        TilePortableGrid.FACTORY_ID = API.instance().getGridManager().add(new GridFactoryPortableGridBlock());
        PortableGrid.ID = API.instance().getGridManager().add(new GridFactoryPortableGrid());

        API.instance().getCraftingTaskRegistry().add(CraftingTaskFactory.ID, new CraftingTaskFactory());

        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementItemRender.ID, buf -> new CraftingMonitorElementItemRender(StackUtils.readItemStack(buf), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementFluidRender.ID, buf -> new CraftingMonitorElementFluidRender(StackUtils.readFluidStack(buf), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()));
        API.instance().getCraftingMonitorElementRegistry().add(CraftingMonitorElementError.ID, buf -> {
            String id = ByteBufUtils.readUTF8String(buf);
            String message = ByteBufUtils.readUTF8String(buf);

            return new CraftingMonitorElementError(API.instance().getCraftingMonitorElementRegistry().get(id).apply(buf), message);
        });

        API.instance().getCraftingPreviewElementRegistry().add(CraftingPreviewElementItemStack.ID, CraftingPreviewElementItemStack::fromByteBuf);
        API.instance().getCraftingPreviewElementRegistry().add(CraftingPreviewElementFluidStack.ID, CraftingPreviewElementFluidStack::fromByteBuf);
        API.instance().getCraftingPreviewElementRegistry().add(CraftingPreviewElementError.ID, CraftingPreviewElementError::fromByteBuf);

        API.instance().addPatternRenderHandler(pattern -> GuiBase.isShiftKeyDown());
        API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getMinecraft().player.openContainer;

            if (container instanceof ContainerCrafterManager) {
                for (Slot slot : container.inventorySlots) {
                    if (slot instanceof SlotCrafterManager && slot.getStack() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });
        API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getMinecraft().player.openContainer;

            if (container instanceof ContainerCrafter) {
                for (int i = 0; i < 9; ++i) {
                    if (container.getSlot(i).getStack() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });

        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerItems.ID, ReaderWriterHandlerItems::new);
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerFluids.ID, ReaderWriterHandlerFluids::new);
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerRedstone.ID, tag -> new ReaderWriterHandlerRedstone());
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerForgeEnergy.ID, ReaderWriterHandlerForgeEnergy::new);

        API.instance().addExternalStorageProvider(StorageType.ITEM, new ExternalStorageProviderItem());
        API.instance().addExternalStorageProvider(StorageType.FLUID, new ExternalStorageProviderFluid());

        MinecraftForge.EVENT_BUS.register(new NetworkNodeListener());

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
    public void registerRecipes(RegistryEvent.Register<IRecipe> e) {
        e.getRegistry().register(new RecipeCover().setRegistryName(new ResourceLocation(RS.ID, "cover")));
        e.getRegistry().register(new RecipeHollowCover().setRegistryName(new ResourceLocation(RS.ID, "hollow_cover")));
    }

    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck e) {
        if (e.getTargetBlock().getBlock() instanceof BlockBase) {
            e.setCanHarvest(true); // Allow break without tool
        }
    }

    @SubscribeEvent
    public void onPlayerLoginEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e) {
        if (!e.player.world.isRemote) {
            RS.INSTANCE.network.sendTo(new MessageConfigSync(), (ServerPlayerEntity) e.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogoutEvent(WorldEvent.Unload e) {
        if (e.getWorld().isRemote && RS.INSTANCE.config.getOriginalClientVersion() != null) {
            RS.INSTANCE.config = RS.INSTANCE.config.getOriginalClientVersion();
        }
    }*/
}
