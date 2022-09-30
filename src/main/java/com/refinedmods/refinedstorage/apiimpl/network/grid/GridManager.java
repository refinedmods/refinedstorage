package com.refinedmods.refinedstorage.apiimpl.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.GridFactoryType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.IGridFactory;
import com.refinedmods.refinedstorage.api.network.grid.IGridManager;
import com.refinedmods.refinedstorage.container.factory.GridMenuProvider;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
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
    public void openGrid(ResourceLocation id, ServerPlayer player, BlockPos pos) {
        openGrid(id, player, null, pos, new PlayerSlot(-1));
    }

    @Override
    public void openGrid(ResourceLocation id, ServerPlayer player, ItemStack stack, PlayerSlot slot) {
        openGrid(id, player, stack, null, slot);
    }

    private void openGrid(ResourceLocation id, ServerPlayer player, @Nullable ItemStack stack, @Nullable BlockPos pos, PlayerSlot slot) {
        Pair<IGrid, BlockEntity> grid = createGrid(id, player, stack, pos, slot);
        if (grid == null) {
            return;
        }

        NetworkHooks.openScreen(player, new GridMenuProvider(grid.getLeft(), grid.getRight()), buf -> {
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
    public Pair<IGrid, BlockEntity> createGrid(ResourceLocation id, Player player, @Nullable ItemStack stack, @Nullable BlockPos pos, PlayerSlot slot) {
        IGridFactory factory = factories.get(id);

        if (factory == null) {
            return null;
        }

        IGrid grid = null;
        BlockEntity blockEntity = factory.getRelevantBlockEntity(player.level, pos);

        if (factory.getType() == GridFactoryType.STACK) {
            grid = factory.createFromStack(player, stack, slot);
        } else if (factory.getType() == GridFactoryType.BLOCK) {
            grid = factory.createFromBlock(player, pos);
        }

        if (grid == null) {
            return null;
        }

        return Pair.of(grid, blockEntity);
    }
}
