package refinedstorage.proxy;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.EnumControllerType;
import refinedstorage.block.EnumGridType;
import refinedstorage.block.EnumStorageType;
import refinedstorage.item.*;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        // Item Variants
        ModelBakery.registerItemVariants(RefinedStorageItems.STORAGE_DISK,
            new ResourceLocation("refinedstorage:1k_storage_disk"),
            new ResourceLocation("refinedstorage:4k_storage_disk"),
            new ResourceLocation("refinedstorage:16k_storage_disk"),
            new ResourceLocation("refinedstorage:64k_storage_disk"),
            new ResourceLocation("refinedstorage:creative_storage_disk")
        );

        ModelBakery.registerItemVariants(RefinedStorageItems.STORAGE_PART,
            new ResourceLocation("refinedstorage:1k_storage_part"),
            new ResourceLocation("refinedstorage:4k_storage_part"),
            new ResourceLocation("refinedstorage:16k_storage_part"),
            new ResourceLocation("refinedstorage:64k_storage_part")
        );

        ModelBakery.registerItemVariants(RefinedStorageItems.PROCESSOR,
            new ResourceLocation("refinedstorage:basic_printed_processor"),
            new ResourceLocation("refinedstorage:improved_printed_processor"),
            new ResourceLocation("refinedstorage:advanced_printed_processor"),
            new ResourceLocation("refinedstorage:basic_processor"),
            new ResourceLocation("refinedstorage:improved_processor"),
            new ResourceLocation("refinedstorage:advanced_processor"),
            new ResourceLocation("refinedstorage:printed_silicon")
        );

        ModelBakery.registerItemVariants(RefinedStorageItems.CORE,
            new ResourceLocation("refinedstorage:construction_core"),
            new ResourceLocation("refinedstorage:destruction_core")
        );

        ModelBakery.registerItemVariants(RefinedStorageItems.WIRELESS_GRID,
            new ResourceLocation("refinedstorage:wireless_grid_connected"),
            new ResourceLocation("refinedstorage:wireless_grid_disconnected")
        );

        // Items
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_1K, new ModelResourceLocation("refinedstorage:1k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_4K, new ModelResourceLocation("refinedstorage:4k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_16K, new ModelResourceLocation("refinedstorage:16k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_64K, new ModelResourceLocation("refinedstorage:64k_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_DISK, ItemStorageDisk.TYPE_CREATIVE, new ModelResourceLocation("refinedstorage:creative_storage_disk", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_PART, ItemStoragePart.TYPE_1K, new ModelResourceLocation("refinedstorage:1k_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_PART, ItemStoragePart.TYPE_4K, new ModelResourceLocation("refinedstorage:4k_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_PART, ItemStoragePart.TYPE_16K, new ModelResourceLocation("refinedstorage:16k_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_PART, ItemStoragePart.TYPE_64K, new ModelResourceLocation("refinedstorage:64k_storage_part", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_BASIC, new ModelResourceLocation("refinedstorage:basic_printed_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_IMPROVED, new ModelResourceLocation("refinedstorage:improved_printed_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_ADVANCED, new ModelResourceLocation("refinedstorage:advanced_printed_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.PROCESSOR, ItemProcessor.TYPE_BASIC, new ModelResourceLocation("refinedstorage:basic_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.PROCESSOR, ItemProcessor.TYPE_IMPROVED, new ModelResourceLocation("refinedstorage:improved_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.PROCESSOR, ItemProcessor.TYPE_ADVANCED, new ModelResourceLocation("refinedstorage:advanced_processor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.PROCESSOR, ItemProcessor.TYPE_PRINTED_SILICON, new ModelResourceLocation("refinedstorage:printed_silicon", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.SILICON, 0, new ModelResourceLocation("refinedstorage:silicon", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.QUARTZ_ENRICHED_IRON, 0, new ModelResourceLocation("refinedstorage:quartz_enriched_iron", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.CORE, ItemCore.TYPE_CONSTRUCTION, new ModelResourceLocation("refinedstorage:construction_core", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.CORE, ItemCore.TYPE_DESTRUCTION, new ModelResourceLocation("refinedstorage:destruction_core", "inventory"));

        ModelLoader.setCustomMeshDefinition(RefinedStorageItems.WIRELESS_GRID, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                if (ItemWirelessGrid.isValid(stack)) {
                    return new ModelResourceLocation("refinedstorage:wireless_grid_connected", "inventory");
                } else {
                    return new ModelResourceLocation("refinedstorage:wireless_grid_disconnected", "inventory");
                }
            }
        });

        // Blocks
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CABLE), 0, new ModelResourceLocation("refinedstorage:cable", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.GRID), EnumGridType.NORMAL.getId(), new ModelResourceLocation("refinedstorage:grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.GRID), EnumGridType.CRAFTING.getId(), new ModelResourceLocation("refinedstorage:grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.MACHINE_CASING), 0, new ModelResourceLocation("refinedstorage:machine_casing", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.EXPORTER), 0, new ModelResourceLocation("refinedstorage:exporter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.IMPORTER), 0, new ModelResourceLocation("refinedstorage:importer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.EXTERNAL_STORAGE), 0, new ModelResourceLocation("refinedstorage:external_storage", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.DISK_DRIVE), 0, new ModelResourceLocation("refinedstorage:disk_drive", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CONTROLLER), EnumControllerType.NORMAL.getId(), new ModelResourceLocation("refinedstorage:controller", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CONTROLLER), EnumControllerType.CREATIVE.getId(), new ModelResourceLocation("refinedstorage:controller", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CONSTRUCTOR), 0, new ModelResourceLocation("refinedstorage:constructor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.DESTRUCTOR), 0, new ModelResourceLocation("refinedstorage:destructor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.SOLDERER), 0, new ModelResourceLocation("refinedstorage:solderer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.DETECTOR), 0, new ModelResourceLocation("refinedstorage:detector", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.RELAY), 0, new ModelResourceLocation("refinedstorage:relay", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.INTERFACE), 0, new ModelResourceLocation("refinedstorage:interface", "inventory"));

        ModelLoader.setCustomStateMapper(RefinedStorageBlocks.STORAGE, (new StateMap.Builder())
            .ignore(RefinedStorageBlocks.STORAGE.DIRECTION)
            .ignore(RefinedStorageBlocks.STORAGE.CONNECTED)
            .build()
        );

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumStorageType.TYPE_1K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=1k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumStorageType.TYPE_4K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=4k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumStorageType.TYPE_16K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=16k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumStorageType.TYPE_64K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=64k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumStorageType.TYPE_CREATIVE.getId(), new ModelResourceLocation("refinedstorage:storage", "type=creative"));
    }
}
