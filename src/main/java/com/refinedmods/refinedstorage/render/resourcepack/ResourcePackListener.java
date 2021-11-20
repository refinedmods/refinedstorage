package com.refinedmods.refinedstorage.render.resourcepack;

import com.refinedmods.refinedstorage.render.RenderSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;

public class ResourcePackListener extends ReloadListener<RSResourcePackSection> {
    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    protected RSResourcePackSection prepare(IResourceManager resourceManager, IProfiler profiler) {
        for (ResourcePackInfo info : Minecraft.getInstance().getResourcePackList().getEnabledPacks()) {
            try {
                RSResourcePackSection section = info.getResourcePack().getMetadata(RSResourcePackSection.DESERIALIZER);

                if (section != null) {
                    return section;
                }
            } catch (IOException e) {
                logger.error("Could not read Refined Storage resource pack section", e);
            }
        }

        return null;
    }

    @Override
    protected void apply(@Nullable RSResourcePackSection section, IResourceManager resourceManager, IProfiler profiler) {
        if (section != null) {
            RenderSettings.INSTANCE.setColors(section.getPrimaryColor(), section.getSecondaryColor());
        } else {
            RenderSettings.INSTANCE.setColors(-1, -1);
        }
    }
}
