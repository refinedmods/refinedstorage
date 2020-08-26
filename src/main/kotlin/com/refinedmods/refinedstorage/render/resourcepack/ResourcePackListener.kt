package com.refinedmods.refinedstorage.render.resourcepack

import com.refinedmods.refinedstorage.render.RenderSettings
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.ReloadListener
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.resources.ResourcePackInfo
import org.apache.logging.log4j.LogManager
import java.io.IOException

class ResourcePackListener : ReloadListener<RSResourcePackSection?>() {
    private val logger = LogManager.getLogger(javaClass)
    protected fun prepare(resourceManager: IResourceManager?, profiler: IProfiler?): RSResourcePackSection? {
        for (info in Minecraft.getInstance().getResourcePackList().getEnabledPacks()) {
            try {
                val section: RSResourcePackSection = info.getResourcePack().getMetadata(RSResourcePackSection.Companion.DESERIALIZER)
                if (section != null) {
                    return section
                }
            } catch (e: IOException) {
                logger.error("Could not read Refined Storage resource pack section", e)
            }
        }
        return null
    }

    protected fun apply(@Nullable section: RSResourcePackSection?, resourceManager: IResourceManager?, profiler: IProfiler?) {
        if (section != null) {
            RenderSettings.Companion.INSTANCE.setColors(section.primaryColor, section.secondaryColor)
        } else {
            RenderSettings.Companion.INSTANCE.setColors(-1, -1)
        }
    }
}