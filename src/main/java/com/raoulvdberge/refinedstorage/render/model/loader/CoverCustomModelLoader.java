package com.raoulvdberge.refinedstorage.render.model.loader;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.raoulvdberge.refinedstorage.render.model.ModelCover;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

import javax.annotation.Nullable;

public class CoverCustomModelLoader implements ICustomModelLoader {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        // NO OP
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getNamespace().equals(RS.ID) && getType(modelLocation) != null;
    }

    @Override
    public IUnbakedModel loadModel(ResourceLocation modelLocation) {
        return new ModelCover(getType(modelLocation));
    }

    @Nullable
    private CoverType getType(ResourceLocation modelLocation) {
        switch (modelLocation.getPath()) {
            case "cover":
                return CoverType.NORMAL;
            case "hollow_cover":
                return CoverType.HOLLOW;
            default:
                return null;
        }
    }
}
