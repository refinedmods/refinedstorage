package com.raoulvdberge.refinedstorage.render.model.loader;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.raoulvdberge.refinedstorage.render.model.ModelCover;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import javax.annotation.Nullable;

public class CustomModelLoaderCover implements ICustomModelLoader {
    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getResourceDomain().equals(RS.ID) && getType(modelLocation) != null;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        return new ModelCover(getType(modelLocation));
    }

    @Nullable
    private CoverType getType(ResourceLocation modelLocation) {
        switch (modelLocation.getResourcePath()) {
            case "cover":
                return CoverType.NORMAL;
            case "hollow_cover":
                return CoverType.HOLLOW;
            case "hollow_wide_cover":
                return CoverType.HOLLOW_WIDE;
            default:
                return null;
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        // NO OP
    }
}
