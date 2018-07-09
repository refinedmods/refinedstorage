package com.raoulvdberge.refinedstorage.proxy;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.gui.GuiCraftingPreview;
import com.raoulvdberge.refinedstorage.gui.grid.GuiCraftingStart;
import com.raoulvdberge.refinedstorage.item.*;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingPreviewResponse;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.collision.BlockHighlightListener;
import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelPattern;
import com.raoulvdberge.refinedstorage.render.model.loader.CustomModelLoaderCover;
import com.raoulvdberge.refinedstorage.render.tesr.TileEntitySpecialRendererStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ProxyClient extends ProxyCommon implements IModelRegistration {
    private Map<ResourceLocation, Function<IBakedModel, IBakedModel>> bakedModelOverrides = new HashMap<>();

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(new BlockHighlightListener());

        ClientRegistry.bindTileEntitySpecialRenderer(TileStorageMonitor.class, new TileEntitySpecialRendererStorageMonitor());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);

        RSKeyBindings.init();

        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            CraftingPattern pattern = ItemPattern.getPatternFromCache(Minecraft.getMinecraft().world, stack);

            if (BakedModelPattern.canDisplayOutput(stack, pattern)) {
                int color = itemColors.colorMultiplier(pattern.getOutputs().get(0), tintIndex);

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
            new ResourceLocation("refinedstorage:256k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:1024k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:4096k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:creative_fluid_storage_disk")
        );

        ModelBakery.registerItemVariants(RSItems.FLUID_STORAGE_PART,
            new ResourceLocation("refinedstorage:64k_fluid_storage_part"),
            new ResourceLocation("refinedstorage:256k_fluid_storage_part"),
            new ResourceLocation("refinedstorage:1024k_fluid_storage_part"),
            new ResourceLocation("refinedstorage:4096k_fluid_storage_part")
        );

        ModelBakery.registerItemVariants(RSItems.PROCESSOR,
            new ResourceLocation("refinedstorage:cut_basic_processor"),
            new ResourceLocation("refinedstorage:cut_improved_processor"),
            new ResourceLocation("refinedstorage:cut_advanced_processor"),
            new ResourceLocation("refinedstorage:basic_processor"),
            new ResourceLocation("refinedstorage:improved_processor"),
            new ResourceLocation("refinedstorage:advanced_processor"),
            new ResourceLocation("refinedstorage:cut_silicon")
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
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_256K, new ModelResourceLocation("refinedstorage:256k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_1024K, new ModelResourceLocation("refinedstorage:1024k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_4096K, new ModelResourceLocation("refinedstorage:4096k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_CREATIVE, new ModelResourceLocation("refinedstorage:creative_fluid_storage_disk", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_64K, new ModelResourceLocation("refinedstorage:64k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_256K, new ModelResourceLocation("refinedstorage:256k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_1024K, new ModelResourceLocation("refinedstorage:1024k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_4096K, new ModelResourceLocation("refinedstorage:4096k_fluid_storage_part", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_CUT_BASIC, new ModelResourceLocation("refinedstorage:cut_basic_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_CUT_IMPROVED, new ModelResourceLocation("refinedstorage:cut_improved_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_CUT_ADVANCED, new ModelResourceLocation("refinedstorage:cut_advanced_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_BASIC, new ModelResourceLocation("refinedstorage:basic_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_IMPROVED, new ModelResourceLocation("refinedstorage:improved_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_ADVANCED, new ModelResourceLocation("refinedstorage:advanced_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.PROCESSOR, ItemProcessor.TYPE_CUT_SILICON, new ModelResourceLocation("refinedstorage:cut_silicon", "inventory"));

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
        ModelLoader.setCustomModelResourceLocation(RSItems.SECURITY_CARD, 0, new ModelResourceLocation("refinedstorage:security_card", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.CUTTING_TOOL, 0, new ModelResourceLocation("refinedstorage:cutting_tool", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, 0, new ModelResourceLocation("refinedstorage:upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_RANGE, new ModelResourceLocation("refinedstorage:range_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_SPEED, new ModelResourceLocation("refinedstorage:speed_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_CRAFTING, new ModelResourceLocation("refinedstorage:crafting_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_STACK, new ModelResourceLocation("refinedstorage:stack_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_INTERDIMENSIONAL, new ModelResourceLocation("refinedstorage:interdimensional_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_SILK_TOUCH, new ModelResourceLocation("refinedstorage:silk_touch_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_FORTUNE_1, new ModelResourceLocation("refinedstorage:fortune_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_FORTUNE_2, new ModelResourceLocation("refinedstorage:fortune_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.UPGRADE, ItemUpgrade.TYPE_FORTUNE_3, new ModelResourceLocation("refinedstorage:fortune_upgrade", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RSItems.COVER, 0, new ModelResourceLocation("refinedstorage:cover", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RSItems.HOLLOW_COVER, 0, new ModelResourceLocation("refinedstorage:hollow_cover", "inventory"));

        ModelLoaderRegistry.registerLoader(new CustomModelLoaderCover());

        addBakedModelOverride(new ResourceLocation(RS.ID, "pattern"), BakedModelPattern::new);

        for (BlockBase block : blocksToRegister) {
            block.registerModels(this);
        }
    }

    public static void onReceiveCraftingPreviewResponse(MessageGridCraftingPreviewResponse message) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (screen instanceof GuiCraftingStart) {
                screen = ((GuiCraftingStart) screen).getParent();
            }

            FMLCommonHandler.instance().showGuiScreen(new GuiCraftingPreview(screen, message.stacks, message.hash, message.quantity));
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
        for (ModelResourceLocation resource : e.getModelRegistry().getKeys()) {
            ResourceLocation key = new ResourceLocation(resource.getResourceDomain(), resource.getResourcePath());

            if (bakedModelOverrides.containsKey(key)) {
                e.getModelRegistry().putObject(resource, bakedModelOverrides.get(key).apply(e.getModelRegistry().getObject(resource)));
            }
        }
    }

    @Override
    public void addBakedModelOverride(ResourceLocation resource, Function<IBakedModel, IBakedModel> override) {
        bakedModelOverrides.put(resource, override);
    }

    @Override
    public void setModel(Block block, int meta, ModelResourceLocation resource) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, resource);
    }

    @Override
    public void setModelMeshDefinition(Block block, ItemMeshDefinition meshDefinition) {
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(block), meshDefinition);
    }

    @Override
    public void addModelLoader(Supplier<ICustomModelLoader> modelLoader) {
        ModelLoaderRegistry.registerLoader(modelLoader.get());
    }

    @Override
    public void setStateMapper(Block block, IStateMapper stateMapper) {
        ModelLoader.setCustomStateMapper(block, stateMapper);
    }
}
