package com.raoulvdberge.refinedstorage.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

import java.util.function.Function;
import java.util.function.Supplier;

public interface IModelRegistration {
    void addBakedModelOverride(ResourceLocation resource, Function<IBakedModel, IBakedModel> override);

    void setModel(Block block, int meta, ModelResourceLocation resource);

    void setModelMeshDefinition(Block block, ItemMeshDefinition meshDefinition);

    void addModelLoader(Supplier<ICustomModelLoader> modelLoader);

    void setStateMapper(Block block, IStateMapper stateMapper);
}
