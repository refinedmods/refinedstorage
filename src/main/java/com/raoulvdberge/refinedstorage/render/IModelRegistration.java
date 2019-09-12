package com.raoulvdberge.refinedstorage.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

import java.util.function.Function;
import java.util.function.Supplier;

public interface IModelRegistration {
    void addBakedModelOverride(ResourceLocation resource, Function<IBakedModel, IBakedModel> override);

    void setModel(Block block, int meta, ModelResourceLocation resource); //  ModelLoader.setCustomModelResourceLocation ->  no longer needed due to flattening, for custom, use custom baked model

    void setModel(Item item, int meta, ModelResourceLocation resource); // same as above

    void setModelVariants(Item item, ResourceLocation... variants); // no longer needed

    // Supplier needed to avoid server crash.
    void addModelLoader(Supplier<ICustomModelLoader> modelLoader);

    <T extends TileEntity> void setTesr(Class<T> tile, TileEntityRenderer<T> tesr);

    void addItemColor(Item item, IItemColor itemColor);
}
