package com.raoulvdberge.refinedstorage.render;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.function.Supplier;

public class CustomModelLoaderDefault implements ICustomModelLoader {
    private ResourceLocation modelLocation;
    private Supplier<IModel> model;

    public CustomModelLoaderDefault(ResourceLocation modelLocation, Supplier<IModel> model) {
        this.modelLocation = modelLocation;
        this.model = model;
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return this.modelLocation.equals(modelLocation);
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        return model.get();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        // NO OP
    }
}
