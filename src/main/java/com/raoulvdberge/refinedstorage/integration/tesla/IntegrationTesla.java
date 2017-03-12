package com.raoulvdberge.refinedstorage.integration.tesla;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraftforge.fml.common.Loader;

public final class IntegrationTesla {
    public static boolean isLoaded() {
        return Loader.isModLoaded("tesla");
    }

    public static void register() {
        API.instance().getReaderWriterHandlerRegistry().add(ReaderWriterHandlerTesla.ID, ReaderWriterHandlerTesla::new);
    }
}
