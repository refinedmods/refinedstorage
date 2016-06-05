package refinedstorage.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import refinedstorage.api.storage.IStorageProvider;

public final class RefinedStorageCapabilities {
    @CapabilityInject(IStorageProvider.class)
    public static final Capability<IStorageProvider> STORAGE_PROVIDER_CAPABILITY = null;
}
