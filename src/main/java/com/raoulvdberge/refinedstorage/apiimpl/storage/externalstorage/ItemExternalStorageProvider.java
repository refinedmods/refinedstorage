package com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.raoulvdberge.refinedstorage.tile.InterfaceTile;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ItemExternalStorageProvider implements IExternalStorageProvider<ItemStack> {
    @Override
    public boolean canProvide(TileEntity tile, Direction direction) {
        INetworkNode node = NetworkUtils.getNodeFromTile(tile);

        if (node instanceof IStorageProvider) {
            return false;
        }

        return WorldUtils.getItemHandler(tile, direction.getOpposite()) != null;
    }

    @Nonnull
    @Override
    public IExternalStorage<ItemStack> provide(IExternalStorageContext context, Supplier<TileEntity> tile, Direction direction) {
        return new ItemExternalStorage(context, () -> WorldUtils.getItemHandler(tile.get(), direction.getOpposite()), tile.get() instanceof InterfaceTile);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
