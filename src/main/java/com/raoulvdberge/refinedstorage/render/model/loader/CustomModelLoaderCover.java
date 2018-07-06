package com.raoulvdberge.refinedstorage.render.model.loader;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.render.model.ModelCover;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class CustomModelLoaderCover implements ICustomModelLoader {
    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getResourceDomain().equals(RS.ID) && (modelLocation.getResourcePath().equals("cover") || modelLocation.getResourcePath().equals("hollow_cover"));
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        return new ModelCover(modelLocation.getResourcePath().equals("hollow_cover"));
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        // NO OP
    }
}
