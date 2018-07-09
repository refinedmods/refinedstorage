package com.raoulvdberge.refinedstorage.proxy;

import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.item.ItemBase;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.collision.BlockHighlightListener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ProxyClient extends ProxyCommon implements IModelRegistration {
    private Map<ResourceLocation, Function<IBakedModel, IBakedModel>> bakedModelOverrides = new HashMap<>();
    private List<Pair<Item, IItemColor>> itemColors = new LinkedList<>();

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(new BlockHighlightListener());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);

        RSKeyBindings.init();

        itemColors.forEach(p -> Minecraft.getMinecraft().getItemColors().registerItemColorHandler(p.getRight(), p.getKey()));
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent e) {
        for (BlockBase block : blocksToRegister) {
            block.registerModels(this);
        }

        for (Item item : itemsToRegister) {
            if (item instanceof ItemBase) {
                ((ItemBase) item).registerModels(this);
            }
        }
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
        setModel(Item.getItemFromBlock(block), meta, resource);
    }

    @Override
    public void setModel(Item item, int meta, ModelResourceLocation resource) {
        ModelLoader.setCustomModelResourceLocation(item, meta, resource);
    }

    @Override
    public void setModelVariants(Item item, ResourceLocation... variants) {
        ModelBakery.registerItemVariants(item, variants);
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

    @Override
    public void setTesr(Class<? extends TileEntity> tile, TileEntitySpecialRenderer tesr) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, tesr);
    }

    @Override
    public void addItemColor(Item item, IItemColor itemColor) {
        itemColors.add(Pair.of(item, itemColor)); // ItemColors is only available in init.
    }
}
