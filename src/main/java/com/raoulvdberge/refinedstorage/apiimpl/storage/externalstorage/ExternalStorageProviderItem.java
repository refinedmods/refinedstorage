package com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import com.raoulvdberge.refinedstorage.tile.TileInterface;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ExternalStorageProviderItem implements IExternalStorageProvider<ItemStack> {
    @Override
    public boolean canProvide(TileEntity tile, Direction direction) {
        INetworkNodeProxy nodeProxy = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, direction.getOpposite()).orElse(null);

        if (!(nodeProxy != null && nodeProxy.getNode() instanceof IStorageProvider)) { // TODO: Correct if still?
            return WorldUtils.getItemHandler(tile, direction.getOpposite()) != null;
        }

        return false;
    }

    @Nonnull
    @Override
    public IStorageExternal<ItemStack> provide(IExternalStorageContext context, Supplier<TileEntity> tile, Direction direction) {
        return new StorageExternalItem(context, () -> WorldUtils.getItemHandler(tile.get(), direction.getOpposite()), tile.get() instanceof TileInterface);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
