package com.raoulvdberge.refinedstorage.render.model.loader;

import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

import java.util.function.Supplier;

public class CustomModelLoaderDefault implements ICustomModelLoader {
    private ResourceLocation modelLocation;
    private Supplier<IUnbakedModel> model;

    public CustomModelLoaderDefault(ResourceLocation modelLocation, Supplier<IUnbakedModel> model) {
        this.modelLocation = modelLocation;
        this.model = model;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        // NO OP
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return this.modelLocation.equals(modelLocation);
    }

    @Override
    public IUnbakedModel loadModel(ResourceLocation modelLocation) {
        return model.get();
    }
}
