package refinedstorage.proxy;

import mcmultipart.client.multipart.ModelMultipartContainer;
import mcmultipart.raytrace.PartMOP;
import mcmultipart.raytrace.RayTraceUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.*;
import refinedstorage.item.*;

import java.util.List;

public class ClientProxy extends CommonProxy {
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        for (ModelResourceLocation model : e.getModelRegistry().getKeys()) {
            for (BlockCable cable : cables) {
                if (model.getResourceDomain().equals(RefinedStorage.ID) && model.getResourcePath().equals(cable.getName()) && !model.getVariant().equals("inventory")) {
                    e.getModelRegistry().putObject(model, new ModelMultipartContainer(e.getModelRegistry().getObject(model), input -> cable.canRenderInLayer(input)));
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

        IBlockState state = player.worldObj.getBlockState(pos);

        if (!(state.getBlock() instanceof BlockCable)) {
            return;
        }

        state = ((BlockCable) state.getBlock()).getActualState(state, player.worldObj, pos);

        if (((BlockCable) state.getBlock()).collisionRayTrace(state, player.worldObj, pos, RayTraceUtils.getStart(player), RayTraceUtils.getEnd(player)) instanceof PartMOP) {
            return;
        }

        List<AxisAlignedBB> unionized = ((BlockCable) state.getBlock()).getUnionizedCollisionBoxes(state);
        List<AxisAlignedBB> nonUnionized = ((BlockCable) state.getBlock()).getNonUnionizedCollisionBoxes(state);

        e.setCanceled(true);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
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

        VertexBuffer buffer = tessellator.getBuffer();

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

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(this);

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

        ModelBakery.registerItemVariants(RefinedStorageItems.FLUID_STORAGE_DISK,
            new ResourceLocation("refinedstorage:64k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:128k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:256k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:512k_fluid_storage_disk"),
            new ResourceLocation("refinedstorage:creative_fluid_storage_disk")
        );

        ModelBakery.registerItemVariants(RefinedStorageItems.FLUID_STORAGE_PART,
            new ResourceLocation("refinedstorage:64k_fluid_storage_part"),
            new ResourceLocation("refinedstorage:128k_fluid_storage_part"),
            new ResourceLocation("refinedstorage:256k_fluid_storage_part"),
            new ResourceLocation("refinedstorage:512k_fluid_storage_part")
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

        ModelBakery.registerItemVariants(RefinedStorageItems.UPGRADE,
            new ResourceLocation("refinedstorage:upgrade"),
            new ResourceLocation("refinedstorage:range_upgrade"),
            new ResourceLocation("refinedstorage:speed_upgrade"),
            new ResourceLocation("refinedstorage:stack_upgrade"),
            new ResourceLocation("refinedstorage:interdimensional_upgrade")
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

        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_64K, new ModelResourceLocation("refinedstorage:64k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_128K, new ModelResourceLocation("refinedstorage:128k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_256K, new ModelResourceLocation("refinedstorage:256k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_512K, new ModelResourceLocation("refinedstorage:512k_fluid_storage_disk", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.FLUID_STORAGE_DISK, ItemFluidStorageDisk.TYPE_CREATIVE, new ModelResourceLocation("refinedstorage:creative_fluid_storage_disk", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_64K, new ModelResourceLocation("refinedstorage:64k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_128K, new ModelResourceLocation("refinedstorage:128k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_256K, new ModelResourceLocation("refinedstorage:256k_fluid_storage_part", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.FLUID_STORAGE_PART, ItemFluidStoragePart.TYPE_512K, new ModelResourceLocation("refinedstorage:512k_fluid_storage_part", "inventory"));

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

        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.WIRELESS_GRID, 0, new ModelResourceLocation("refinedstorage:wireless_grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.PATTERN, 0, new ModelResourceLocation("refinedstorage:pattern", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.STORAGE_HOUSING, 0, new ModelResourceLocation("refinedstorage:storage_housing", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.GRID_FILTER, 0, new ModelResourceLocation("refinedstorage:grid_filter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.NETWORK_CARD, 0, new ModelResourceLocation("refinedstorage:network_card", "inventory"));

        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.UPGRADE, 0, new ModelResourceLocation("refinedstorage:upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_RANGE, new ModelResourceLocation("refinedstorage:range_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED, new ModelResourceLocation("refinedstorage:speed_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_CRAFTING, new ModelResourceLocation("refinedstorage:crafting_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_STACK, new ModelResourceLocation("refinedstorage:stack_upgrade", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_INTERDIMENSIONAL, new ModelResourceLocation("refinedstorage:interdimensional_upgrade", "inventory"));

        // Blocks
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CABLE), 0, new ModelResourceLocation("refinedstorage:cable", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.GRID), EnumGridType.NORMAL.getId(), new ModelResourceLocation("refinedstorage:grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.GRID), EnumGridType.CRAFTING.getId(), new ModelResourceLocation("refinedstorage:grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.GRID), EnumGridType.PATTERN.getId(), new ModelResourceLocation("refinedstorage:grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.GRID), EnumGridType.FLUID.getId(), new ModelResourceLocation("refinedstorage:fluid_grid", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.MACHINE_CASING), 0, new ModelResourceLocation("refinedstorage:machine_casing", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.EXPORTER), 0, new ModelResourceLocation("refinedstorage:exporter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.IMPORTER), 0, new ModelResourceLocation("refinedstorage:importer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.EXTERNAL_STORAGE), 0, new ModelResourceLocation("refinedstorage:external_storage", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.DISK_DRIVE), 0, new ModelResourceLocation("refinedstorage:disk_drive", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CONTROLLER), EnumControllerType.NORMAL.getId(), new ModelResourceLocation("refinedstorage:controller", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CONTROLLER), EnumControllerType.CREATIVE.getId(), new ModelResourceLocation("refinedstorage:creative_controller", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CONSTRUCTOR), 0, new ModelResourceLocation("refinedstorage:constructor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.DESTRUCTOR), 0, new ModelResourceLocation("refinedstorage:destructor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.SOLDERER), 0, new ModelResourceLocation("refinedstorage:solderer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.DETECTOR), 0, new ModelResourceLocation("refinedstorage:detector", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.RELAY), 0, new ModelResourceLocation("refinedstorage:relay", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.INTERFACE), 0, new ModelResourceLocation("refinedstorage:interface", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.FLUID_INTERFACE), 0, new ModelResourceLocation("refinedstorage:fluid_interface", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.WIRELESS_TRANSMITTER), 0, new ModelResourceLocation("refinedstorage:wireless_transmitter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CRAFTING_MONITOR), 0, new ModelResourceLocation("refinedstorage:crafting_monitor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.CRAFTER), 0, new ModelResourceLocation("refinedstorage:crafter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.PROCESSING_PATTERN_ENCODER), 0, new ModelResourceLocation("refinedstorage:processing_pattern_encoder", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.NETWORK_TRANSMITTER), 0, new ModelResourceLocation("refinedstorage:network_transmitter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.NETWORK_RECEIVER), 0, new ModelResourceLocation("refinedstorage:network_receiver", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumItemStorageType.TYPE_1K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=1k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumItemStorageType.TYPE_4K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=4k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumItemStorageType.TYPE_16K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=16k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumItemStorageType.TYPE_64K.getId(), new ModelResourceLocation("refinedstorage:storage", "type=64k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.STORAGE), EnumItemStorageType.TYPE_CREATIVE.getId(), new ModelResourceLocation("refinedstorage:storage", "type=creative"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_64K.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=64k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_128K.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=128k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_256K.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=256k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_512K.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=512k"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RefinedStorageBlocks.FLUID_STORAGE), EnumFluidStorageType.TYPE_CREATIVE.getId(), new ModelResourceLocation("refinedstorage:fluid_storage", "type=creative"));
    }
}
