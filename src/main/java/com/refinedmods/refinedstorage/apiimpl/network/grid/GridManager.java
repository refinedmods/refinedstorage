package com.refinedmods.refinedstorage.apiimpl.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.GridFactoryType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.IGridFactory;
import com.refinedmods.refinedstorage.api.network.grid.IGridManager;
import com.refinedmods.refinedstorage.container.factory.GridContainerProvider;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
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
    public void openGrid(ResourceLocation id, ServerPlayerEntity player, BlockPos pos) {
        openGrid(id, player, null, pos, new PlayerSlot(-1));
    }

    @Override
    public void openGrid(ResourceLocation id, ServerPlayerEntity player, ItemStack stack, PlayerSlot slot) {
        openGrid(id, player, stack, null, slot);
    }

    private void openGrid(ResourceLocation id, ServerPlayerEntity player, @Nullable ItemStack stack, @Nullable BlockPos pos, PlayerSlot slot) {
        Pair<IGrid, TileEntity> grid = createGrid(id, player, stack, pos, slot);
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
                buf.writeItem(stack);
            }

            slot.writePlayerSlot(buf);
        });
    }

    @Override
    @Nullable
    public Pair<IGrid, TileEntity> createGrid(ResourceLocation id, PlayerEntity player, @Nullable ItemStack stack, @Nullable BlockPos pos, PlayerSlot slot) {
        IGridFactory factory = factories.get(id);

        if (factory == null) {
            return null;
        }

        IGrid grid = null;
        TileEntity tile = factory.getRelevantTile(player.level, pos);

        if (factory.getType() == GridFactoryType.STACK) {
            grid = factory.createFromStack(player, stack, slot);
        } else if (factory.getType() == GridFactoryType.BLOCK) {
            grid = factory.createFromBlock(player, pos);
        }

        if (grid == null) {
            return null;
        }

        return Pair.of(grid, tile);
    }
}
