package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.block.CraftingMonitorBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.CraftingMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.ICraftingMonitor;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class CraftingMonitorNetworkNode extends NetworkNode implements ICraftingMonitor {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "crafting_monitor");

    private static final String NBT_TAB_SELECTED = "TabSelected";
    private static final String NBT_TAB_PAGE = "TabPage";

    private Optional<UUID> tabSelected = Optional.empty();
    private int tabPage;

    public CraftingMonitorNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getCraftingMonitor().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("gui.refinedstorage.crafting_monitor");
    }

    @Override
    public void onCancelled(ServerPlayer player, @Nullable UUID id) {
        if (network != null) {
            network.getItemGridHandler().onCraftingCancelRequested(player, id);
        }
    }

    @Override
    public BlockEntitySynchronizationParameter<Integer, ?> getRedstoneModeParameter() {
        return NetworkNodeBlockEntity.REDSTONE_MODE;
    }

    @Override
    public Collection<ICraftingTask> getTasks() {
        return network != null ? network.getCraftingManager().getTasks() : Collections.emptyList();
    }

    @Nullable
    @Override
    public ICraftingManager getCraftingManager() {
        return network != null ? network.getCraftingManager() : null;
    }

    @Override
    public boolean isActiveOnClient() {
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof CraftingMonitorBlock) {
            return state.getValue(NetworkNodeBlock.CONNECTED);
        }

        return false;
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        tag.putInt(NBT_TAB_PAGE, tabPage);

        tabSelected.ifPresent(uuid -> tag.putUUID(NBT_TAB_SELECTED, uuid));

        return tag;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if (tag.contains(NBT_TAB_PAGE)) {
            tabPage = tag.getInt(NBT_TAB_PAGE);
        }

        if (tag.hasUUID(NBT_TAB_SELECTED)) {
            tabSelected = Optional.of(tag.getUUID(NBT_TAB_SELECTED));
        }
    }

    @Override
    public void onClosed(Player player) {
        // NO OP
    }

    @Override
    public Optional<UUID> getTabSelected() {
        return level.isClientSide ? CraftingMonitorBlockEntity.TAB_SELECTED.getValue() : tabSelected;
    }

    public void setTabSelected(Optional<UUID> tabSelected) {
        this.tabSelected = tabSelected;
    }

    @Override
    public int getTabPage() {
        return level.isClientSide ? CraftingMonitorBlockEntity.TAB_PAGE.getValue() : tabPage;
    }

    public void setTabPage(int tabPage) {
        this.tabPage = tabPage;
    }

    @Override
    public void onTabSelectionChanged(Optional<UUID> tab) {
        BlockEntitySynchronizationManager.setParameter(CraftingMonitorBlockEntity.TAB_SELECTED, tab);
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0) {
            BlockEntitySynchronizationManager.setParameter(CraftingMonitorBlockEntity.TAB_PAGE, page);
        }
    }

    @Override
    public int getSlotId() {
        return -1;
    }
}
