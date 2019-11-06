package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridFactory;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridManager;
import com.raoulvdberge.refinedstorage.container.factory.GridContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GridManager implements IGridManager {
    private final Map<ResourceLocation, IGridFactory> factories = new HashMap<>();

    @Override
    public void add(ResourceLocation id, IGridFactory factory) {
        factories.put(id, factory);
    }

    @Override
    @Nullable
    public IGridFactory get(ResourceLocation id) {
        return factories.get(id);
    }

    @Override
    public void openGrid(ResourceLocation id, ServerPlayerEntity player, BlockPos pos) {
        openGrid(id, player, null, pos);
    }

    @Override
    public void openGrid(ResourceLocation id, ServerPlayerEntity player, ItemStack stack) {
        openGrid(id, player, stack, null);
    }

    private void openGrid(ResourceLocation id, ServerPlayerEntity player, @Nullable ItemStack stack, @Nullable BlockPos pos) {
        Pair<IGrid, TileEntity> grid = createGrid(id, player, stack, pos);
        if (grid == null) {
            return;
        }

        NetworkHooks.openGui(player, new GridContainerProvider(grid.getLeft(), grid.getRight()), buf -> {
            buf.writeResourceLocation(id);

            buf.writeBoolean(pos != null);
            if (pos != null) {
                buf.writeBlockPos(pos);
            }

            buf.writeBoolean(stack != null);
            if (stack != null) {
                buf.writeItemStack(stack);
            }
        });
    }

    @Override
    @Nullable
    public Pair<IGrid, TileEntity> createGrid(ResourceLocation id, PlayerEntity player, @Nullable ItemStack stack, @Nullable BlockPos pos) {
        IGridFactory factory = get(id);

        if (factory == null) {
            return null;
        }

        IGrid grid = null;
        TileEntity tile = factory.getRelevantTile(player.world, pos);

        switch (factory.getType()) {
            case STACK:
                grid = factory.createFromStack(player, stack);
                break;
            case BLOCK:
                grid = factory.createFromBlock(player, pos);
                break;
        }

        if (grid == null) {
            return null;
        }

        return Pair.of(grid, tile);
    }
}
