package com.raoulvdberge.refinedstorage.proxy;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.block.*;
import com.raoulvdberge.refinedstorage.gui.GuiCraftingPreview;
import com.raoulvdberge.refinedstorage.gui.grid.GuiCraftingStart;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import com.raoulvdberge.refinedstorage.integration.mcmp.RSMCMPAddon;
import com.raoulvdberge.refinedstorage.item.*;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingPreviewResponse;
import com.raoulvdberge.refinedstorage.render.*;
import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.TilePortableGrid;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ProxyClient extends ProxyCommon {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        ClientRegistry.bindTileEntitySpecialRenderer(TileStorageMonitor.class, new TileEntitySpecialRendererStorageMonitor());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);

        RSKeyBindings.init();

        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            CraftingPattern pattern = ItemPattern.getPatternFromCache(Minecraft.getMinecraft().world, stack);

            if (BakedModelPattern.canDisplayPatternOutput(pattern)) {
                int color = itemColors.getColorFromItemstack(pattern.getOutputs().get(0), tintIndex);

                if (color != -1) {
                    return color;
                }
            }

            return 0xFFFFFF;
        }, RSItems.PATTERN);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent e) {
        ModelBakery.registerItemVariants(RSItems.STORAGE_DISK,
            new ResourceLocation("refinedstorage:1k_storage_disk"),
            new ResourceLocation("refinedstorage:4k_storage_disk"),
            new ResourceLocation("refinedstorage:16k_storage_disk"),
            new ResourceLocation("refinedstorage:64k_storage_disk"),
            new ResourceLocation("refinedstorage:creative_storage_disk")
        );

        ModelBakery.registerItemVariants(RSItems.STORAGE_PART,
            new ResourceLocation("refinedstorage:1k_storage_part"),
            new ResourceLocation("refinedstorage:4k_storage_part"),
            new ResourceLocation("refinedstorage:16k_storage_part"),
            new ResourceLocation("refinedstorage:64k_storage_part")
        );

        ModelBakery.registerItemVariants(RSItems.FLUID_STORAGE_DISK,
            new ResourceLocation("refinedstorage:64k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:128k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:256k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:512k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:creative_fluid_storage_disk")
        );

        ModelBakery.registerItemVariants(RSItems.FLUID_STORAGE_PART,
            new ResourceLocation("refinedstorage:64k_fluid_storage_part"),
            new ResourceLocation("refinedstorage:128k_fluid_storage_part"),
            new ResourceLocation("refinedstorage:256k_fluid_storage_part"),
            new ResourceLocation("refinedstorage:512k_fluid_storage_part")
        );

        ModelBakery.registerItemVariants(RSItems.PROCESSOR,
            new ResourceLocation("refinedstorage:basic_printed_processor"),
            new ResourceLocation("refinedstorage:improved_printed_processor"),
            new ResourceLocation("refinedstorage:advanced_printed_processor"),
            new ResourceLocation("refinedstorage:basic_processor"),
            new ResourceLocation("refinedstorage:improved_processor"),
            new ResourceLocation("refinedstorage:advanced_processor"),
            new ResourceLocation("refinedstorage:printed_silicon")
        );

        ModelBakery.registerItemVariants(RSItems.CORE,
            new ResourceLocation("refinedstorage:construction_core"),
            new ResourceLocation("refinedstorage:destruction_core")
        );

        ModelBakery.registerItemVariants(RSItems.UPGRADE,
            new ResourceLocation("refinedstorage:upgrade"),
            new ResourceLocation("refinedstorage:range_upgrade"),
            new ResourceLocation("refinedstorage:speed_upgrade"),
            new ResourceLocation("refinedstorage:stack_upgrade"),
            new ResourceLocation("refinedstorage:interdimensional_upgrade"),
            new ResourceLocation("refinedstorage:silk_touch_upgrade"),
            new ResourceLocation("refinedstorage:fortune_upgrade")
        );

        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_DISK, ItemStorageDisk.TYPE_1K, new ModelResourceLocation("refinedstorage:1k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_DISK, ItemStorageDisk.TYPE_4K, new ModelResourceLocation("refinedstorage:4k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_DISK, ItemStorageDisk.TYPE_16K, new ModelResourceLocation("refinedstorage:16k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_DISK, ItemStorageDisk.TYPE_64K, new ModelResourceLocation("refinedstorage:64k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_DISK, ItemStorageDisk.TYPE_CREATIVE, new ModelResourceLocation("refinedstorage:creative_storage_disk", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_PART, ItemStoragePart.TYPE_1K, new ModelResourceLocation("refinedstorage:1k_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_PART, ItemStoragePart.TYPE_4K, new ModelResourceLocation("refinedstorage:4k_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_PART, ItemStoragePart.TYPE_16K, new ModelResourceLocation("refinedstorage:16k_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_PART, ItemStoragePart.TYPE_64K, new ModelResourceLocation("refinedstorage:64k_storage_part", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_64K, new ModelResourceLocation("refinedstorage:64k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_128K, new ModelResourceLocation("refinedstorage:128k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_256K, new ModelResourceLocation("refinedstorage:256k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_512K, new ModelResourceLocation("refinedstorage:512k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_CREATIVE, new ModelResourceLocation("refinedstorage:creative_fluid_storage_disk", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_64K, new ModelResourceLocation("refinedstorage:64k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_128K, new ModelResourceLocation("refinedstorage:128k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_256K, new ModelResourceLocation("refinedstorage:256k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_512K, new ModelResourceLocation("refinedstorage:512k_fluid_storage_part", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_BASIC, new ModelResourceLocation("refinedstorage:basic_printed_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_IMPROVED, new ModelResourceLocation("refinedstorage:improved_printed_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_ADVANCED, new ModelResourceLocation("refinedstorage:advanced_printed_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_BASIC, new ModelResourceLocation("refinedstorage:basic_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_IMPROVED, new ModelResourceLocation("refinedstorage:improved_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_ADVANCED, new ModelResourceLocation("refinedstorage:advanced_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_SILICON, new ModelResourceLocation("refinedstorage:printed_silicon", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.SILICON, 0, new ModelResourceLocation("refinedstorage:silicon", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.QUARTZ_ENRICHED_IRON, 0, new ModelResourceLocation("refinedstorage:quartz_enriched_iron", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.CORE, ItemCore.TYPE_CONSTRUCTION, new ModelResourceLocation("refinedstorage:construction_core", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.CORE, ItemCore.TYPE_DESTRUCTION, new ModelResourceLocation("refinedstorage:destruction_core", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.WIRELESS_GRID, 0, new ModelResourceLocation("refinedstorage:wireless_grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.WIRELESS_FLUID_GRID, 0, new ModelResourceLocation("refinedstorage:wireless_fluid_grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.WIRELESS_CRAFTING_MONITOR, 0, new ModelResourceLocation("refinedstorage:wireless_crafting_monitor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PATTERN, 0, new ModelResourceLocation("refinedstorage:pattern", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.STORAGE_HOUSING, 0, new ModelResourceLocation("refinedstorage:storage_housing", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FILTER, 0, new ModelResourceLocation("refinedstorage:filter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.NETWORK_CARD, 0, new ModelResourceLocation("refinedstorage:network_card", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.WRENCH, 0, new ModelResourceLocation("refinedstorage:wrench", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.SECURITY_CARD, 0, new ModelResourceLocation("refinedstorage:security_card", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, 0, new ModelResourceLocation("refinedstorage:upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_RANGE, new ModelResourceLocation("refinedstorage:range_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_SPEED, new ModelResourceLocation("refinedstorage:speed_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_CRAFTING, new ModelResourceLocation("refinedstorage:crafting_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_STACK, new ModelResourceLocation("refinedstorage:stack_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_INTERDIMENSIONAL, new ModelResourceLocation("refinedstorage:interdimensional_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_SILK_TOUCH, new ModelResourceLocation("refinedstorage:silk_touch_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_FORTUNE, new ModelResourceLocation("refinedstorage:fortune_upgrade", "inventory"));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.CABLE), 0, new ModelResourceLocation("refinedstorage:cable", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.MACHINE_CASING), 0, new ModelResourceLocation("refinedstorage:machine_casing", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.DISK_DRIVE), 0, new ModelResourceLocation("refinedstorage:disk_drive", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.EXPORTER), 0, new ModelResourceLocation("refinedstorage:exporter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.IMPORTER), 0, new ModelResourceLocation("refinedstorage:importer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.EXTERNAL_STORAGE), 0, new ModelResourceLocation("refinedstorage:external_storage", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.CONSTRUCTOR), 0, new ModelResourceLocation("refinedstorage:constructor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.DESTRUCTOR), 0, new ModelResourceLocation("refinedstorage:destructor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.READER), 0, new ModelResourceLocation("refinedstorage:reader", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.WRITER), 0, new ModelResourceLocation("refinedstorage:writer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.SOLDERER), 0, new ModelResourceLocation("refinedstorage:solderer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.DETECTOR), 0, new ModelResourceLocation("refinedstorage:detector", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.RELAY), 0, new ModelResourceLocation("refinedstorage:relay", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.INTERFACE), 0, new ModelResourceLocation("refinedstorage:interface", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.FLUID_INTERFACE), 0, new ModelResourceLocation("refinedstorage:fluid_interface", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.CRAFTING_MONITOR), 0, new ModelResourceLocation("refinedstorage:crafting_monitor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.CRAFTER), 0, new ModelResourceLocation("refinedstorage:crafter", "connected=false,direction=north"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.STORAGE), ItemStorageType.TYPE_1K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=1k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.STORAGE), ItemStorageType.TYPE_4K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=4k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.STORAGE), ItemStorageType.TYPE_16K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=16k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.STORAGE), ItemStorageType.TYPE_64K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=64k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.STORAGE), ItemStorageType.TYPE_CREATIVE.getId(), new ModelResourceLocation("refinedstorage:storage", "type=creative"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.FLUID_STORAGE), FluidStorageType.TYPE_64K.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=64k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.FLUID_STORAGE), FluidStorageType.TYPE_128K.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=128k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.FLUID_STORAGE), FluidStorageType.TYPE_256K.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=256k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.FLUID_STORAGE), FluidStorageType.TYPE_512K.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=512k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.FLUID_STORAGE), FluidStorageType.TYPE_CREATIVE.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=creative"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.DISK_MANIPULATOR), 0, new ModelResourceLocation("refinedstorage:disk_manipulator", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.SECURITY_MANAGER), 0, new ModelResourceLocation("refinedstorage:security_manager", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.QUARTZ_ENRICHED_IRON), 0, new ModelResourceLocation("refinedstorage:quartz_enriched_iron_block", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.STORAGE_MONITOR), 0, new ModelResourceLocation("refinedstorage:storage_monitor", "connected=false,direction=north"));

        ModelLoaderRegistry.registerLoader(new CustomModelLoaderDefault(new ResourceLocation(RS.ID, "disk_drive"), ModelDiskDrive::new));
        ModelLoaderRegistry.registerLoader(new CustomModelLoaderDefault(new ResourceLocation(RS.ID, "disk_manipulator"), ModelDiskManipulator::new));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.GRID), GridType.NORMAL.getId(), new ModelResourceLocation("refinedstorage:grid", "connected=false,direction=north,type=normal"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.GRID), GridType.CRAFTING.getId(), new ModelResourceLocation("refinedstorage:grid", "connected=false,direction=north,type=crafting"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.GRID), GridType.PATTERN.getId(), new ModelResourceLocation("refinedstorage:grid", "connected=false,direction=north,type=pattern"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.GRID), GridType.FLUID.getId(), new ModelResourceLocation("refinedstorage:grid", "connected=false,direction=north,type=fluid"));
        ModelLoader.setCustomStateMapper(RSBlocks.GRID, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation("refinedstorage:grid" + (Loader.isModLoaded("ctm") ? "_glow" : ""), "connected=" + state.getValue(BlockGrid.CONNECTED) + ",direction=" + state.getValue(RSBlocks.GRID.getDirection().getProperty()) + ",type=" + state.getValue(BlockGrid.TYPE));
            }
        });

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.WIRELESS_TRANSMITTER), 0, new ModelResourceLocation("refinedstorage:wireless_transmitter" + (Loader.isModLoaded("ctm") ? "_glow" : ""), "inventory"));
        ModelLoader.setCustomStateMapper(RSBlocks.WIRELESS_TRANSMITTER, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation("refinedstorage:wireless_transmitter" + (Loader.isModLoaded("ctm") ? "_glow" : ""), "connected=" + state.getValue(BlockWirelessTransmitter.CONNECTED));
            }
        });

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.NETWORK_TRANSMITTER), 0, new ModelResourceLocation("refinedstorage:network_transmitter", "inventory"));
        ModelLoader.setCustomStateMapper(RSBlocks.NETWORK_TRANSMITTER, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation("refinedstorage:network_transmitter" + (Loader.isModLoaded("ctm") ? "_glow" : ""), "connected=" + state.getValue(BlockNetworkTransmitter.CONNECTED));
            }
        });

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RSBlocks.NETWORK_RECEIVER), 0, new ModelResourceLocation("refinedstorage:network_receiver", "inventory"));
        ModelLoader.setCustomStateMapper(RSBlocks.NETWORK_RECEIVER, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation("refinedstorage:network_receiver" + (Loader.isModLoaded("ctm") ? "_glow" : ""), "connected=" + state.getValue(BlockNetworkReceiver.CONNECTED));
            }
        });

        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(RSBlocks.CONTROLLER), stack -> {
            ControllerEnergyType energyType = stack.getItemDamage() == ControllerType.CREATIVE.getId() ? ControllerEnergyType.ON : TileController.getEnergyType(ItemBlockController.getEnergyStored(stack), ItemBlockController.getEnergyCapacity(stack));

            return new ModelResourceLocation("refinedstorage:controller" + (Loader.isModLoaded("ctm") ? "_glow" : ""), "energy_type=" + energyType);
        });
        ModelLoader.setCustomStateMapper(RSBlocks.CONTROLLER, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation("refinedstorage:controller" + (Loader.isModLoaded("ctm") ? "_glow" : ""), "energy_type=" + state.getValue(BlockController.ENERGY_TYPE));
            }
        });

        ModelLoader.setCustomStateMapper(RSBlocks.PORTABLE_GRID, new StateMap.Builder().ignore(BlockPortableGrid.TYPE).build());
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(RSBlocks.PORTABLE_GRID), stack -> {
            PortableGrid portableGrid = new PortableGrid(null, stack);

            return new ModelResourceLocation("refinedstorage:portable_grid", "connected=" + Boolean.toString(portableGrid.getEnergy() != 0) + ",direction=north,disk_state=" + TilePortableGrid.getDiskState(portableGrid));
        });
    }

    public static void onReceiveCraftingPreviewResponse(MessageGridCraftingPreviewResponse message) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (screen instanceof GuiCraftingStart) {
                screen = ((GuiCraftingStart) screen).getParent();
            }

            FMLCommonHandler.instance().showGuiScreen(new GuiCraftingPreview(screen, message.stacks, message.stack, message.quantity));
        });
    }

    public static void onReceiveCraftingStartResponse() {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (screen instanceof GuiCraftingStart) {
                ((GuiCraftingStart) screen).close();
            }
        });
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        for (ModelResourceLocation model : e.getModelRegistry().getKeys()) {
            if (model.getResourceDomain().equals(RS.ID)) {
                if (model.getResourcePath().equals("pattern")) {
                    e.getModelRegistry().putObject(model, new BakedModelPattern(e.getModelRegistry().getObject(model)));
                } else if (model.getResourcePath().equals("portable_grid")) {
                    e.getModelRegistry().putObject(model, new BakedModelPortableGrid(e.getModelRegistry().getObject(model)));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockDrawHighlight(DrawBlockHighlightEvent e) {
        if (e.getTarget() == null || e.getTarget().getBlockPos() == null) {
            return;
        }

        EntityPlayer player = e.getPlayer();

        BlockPos pos = e.getTarget().getBlockPos();

        Block block = IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapBlock(player.getEntityWorld(), pos) : player.getEntityWorld().getBlockState(pos).getBlock();

        if (!(block instanceof BlockCable)) {
            return;
        }

        BlockCable cable = (BlockCable) block;

        IBlockState state = cable.getActualStateForRendering(player.getEntityWorld(), pos);

        if (cable.collisionRayTrace(state, player.getEntityWorld(), pos, RenderUtils.getStart(player), RenderUtils.getEnd(player)) == null) {
            return;
        }

        List<AxisAlignedBB> unionized = cable.getUnionizedCollisionBoxes(state);
        List<AxisAlignedBB> nonUnionized = cable.getNonUnionizedCollisionBoxes(state);

        e.setCanceled(true);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) e.getPartialTicks();
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) e.getPartialTicks();
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) e.getPartialTicks();

        AxisAlignedBB unionizedAabb = unionized.get(0);

        for (int i = 1; i < unionized.size(); ++i) {
            unionizedAabb = unionizedAabb.union(unionized.get(i));
        }

        drawSelectionBoundingBox(unionizedAabb.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2).offset(pos.getX(), pos.getY(), pos.getZ()));

        for (AxisAlignedBB aabb : nonUnionized) {
            drawSelectionBoundingBox(aabb.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2).offset(pos.getX(), pos.getY(), pos.getZ()));
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private void drawSelectionBoundingBox(AxisAlignedBB aabb) {
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(3, DefaultVertexFormats.POSITION);
        buffer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();

        tessellator.draw();

        buffer.begin(3, DefaultVertexFormats.POSITION);
        buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();

        tessellator.draw();

        buffer.begin(1, DefaultVertexFormats.POSITION);
        buffer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();

        tessellator.draw();
    }
}
