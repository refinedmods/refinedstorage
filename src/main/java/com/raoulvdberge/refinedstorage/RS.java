package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.FluidStorageType;
import com.raoulvdberge.refinedstorage.apiimpl.storage.ItemStorageType;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryItem;
import com.raoulvdberge.refinedstorage.block.CableBlock;
import com.raoulvdberge.refinedstorage.block.ControllerBlock;
import com.raoulvdberge.refinedstorage.block.MachineCasingBlock;
import com.raoulvdberge.refinedstorage.block.QuartzEnrichedIronBlock;
import com.raoulvdberge.refinedstorage.config.ServerConfig;
import com.raoulvdberge.refinedstorage.container.ControllerContainer;
import com.raoulvdberge.refinedstorage.container.FilterContainer;
import com.raoulvdberge.refinedstorage.item.*;
import com.raoulvdberge.refinedstorage.item.blockitem.ControllerBlockItem;
import com.raoulvdberge.refinedstorage.item.group.MainItemGroup;
import com.raoulvdberge.refinedstorage.network.NetworkHandler;
import com.raoulvdberge.refinedstorage.recipe.UpgradeWithEnchantedBookRecipeSerializer;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RS.ID)
public final class RS {
    public static final String ID = "refinedstorage";

    public static RS INSTANCE;
    public RSOldConfig config = new RSOldConfig();

    public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();
    public static final ItemGroup MAIN_GROUP = new MainItemGroup();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public RS() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientSetup::new);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::onRegisterBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::onRegisterTiles);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::onRegisterItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, this::onRegisterRecipeSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, this::onRegisterContainers);
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
        e.getRegistry().register(new QuartzEnrichedIronBlock());
        e.getRegistry().register(new ControllerBlock(ControllerBlock.Type.NORMAL));
        e.getRegistry().register(new ControllerBlock(ControllerBlock.Type.CREATIVE));
        e.getRegistry().register(new MachineCasingBlock());
        e.getRegistry().register(new CableBlock());
    }

    @SubscribeEvent
    public void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> e) {
        e.getRegistry().register(registerTileDataParameters(
            TileEntityType.Builder.create(() -> new ControllerTile(ControllerBlock.Type.NORMAL), RSBlocks.CONTROLLER).build(null).setRegistryName(RS.ID, "controller")
        ));

        e.getRegistry().register(registerTileDataParameters(
            TileEntityType.Builder.create(() -> new ControllerTile(ControllerBlock.Type.CREATIVE), RSBlocks.CREATIVE_CONTROLLER).build(null).setRegistryName(RS.ID, "creative_controller")
        ));
    }

    private <T extends TileEntity> TileEntityType<T> registerTileDataParameters(TileEntityType<T> t) {
        TileBase tile = (TileBase) t.create();

        tile.getDataManager().getParameters().forEach(TileDataManager::registerParameter);

        return t;
    }

    @SubscribeEvent
    public void onRegisterContainers(RegistryEvent.Register<ContainerType<?>> e) {
        e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new FilterContainer(inv.player, inv.getCurrentItem(), windowId)).setRegistryName(RS.ID, "filter"));
        e.getRegistry().register(IForgeContainerType.create(((windowId, inv, data) -> new ControllerContainer(null, inv.player, windowId))).setRegistryName(RS.ID, "controller"));
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new CoreItem(CoreItem.Type.CONSTRUCTION));
        e.getRegistry().register(new CoreItem(CoreItem.Type.DESTRUCTION));
        e.getRegistry().register(new QuartzEnrichedIronItem());
        e.getRegistry().register(new ProcessorBindingItem());

        for (ProcessorItem.Type type : ProcessorItem.Type.values()) {
            e.getRegistry().register(new ProcessorItem(type));
        }

        e.getRegistry().register(new SiliconItem());

        e.getRegistry().register(new SecurityCardItem());
        e.getRegistry().register(new NetworkCardItem());
        e.getRegistry().register(new CuttingToolItem());

        for (ItemStorageType type : ItemStorageType.values()) {
            if (type != ItemStorageType.CREATIVE) {
                e.getRegistry().register(new StoragePartItem(type));
            }

            e.getRegistry().register(new StorageDiskItem(type));
        }

        for (FluidStorageType type : FluidStorageType.values()) {
            if (type != FluidStorageType.CREATIVE) {
                e.getRegistry().register(new FluidStoragePartItem(type));
            }

            e.getRegistry().register(new FluidStorageDiskItem(type));
        }

        e.getRegistry().register(new StorageHousingItem());

        for (UpgradeItem.Type type : UpgradeItem.Type.values()) {
            e.getRegistry().register(new UpgradeItem(type));
        }

        e.getRegistry().register(new WrenchItem());
        e.getRegistry().register(new PatternItem());
        e.getRegistry().register(new FilterItem());

        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.QUARTZ_ENRICHED_IRON));
        e.getRegistry().register(new ControllerBlockItem(RSBlocks.CONTROLLER));
        e.getRegistry().register(new ControllerBlockItem(RSBlocks.CREATIVE_CONTROLLER));
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.MACHINE_CASING));
        e.getRegistry().register(BlockUtils.createBlockItemFor(RSBlocks.CABLE));
    }

    /* TODO
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandCreateDisk());
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent e) {
        FMLLog.bigWarning("Invalid fingerprint detected for the Refined Storage jar file! The file " + e.getSource().getName() + " may have been tampered with. This version will NOT be supported!");
    }*/
}
