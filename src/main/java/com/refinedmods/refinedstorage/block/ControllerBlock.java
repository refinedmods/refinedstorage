package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import com.refinedmods.refinedstorage.container.ControllerContainerMenu;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.ColorMap;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class ControllerBlock extends BaseBlock implements EntityBlock {
    public static final EnumProperty<EnergyType> ENERGY_TYPE = EnumProperty.create("energy_type", EnergyType.class);
    private final NetworkType type;

    public ControllerBlock(NetworkType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
        this.registerDefaultState(getStateDefinition().any().setValue(ENERGY_TYPE, EnergyType.OFF));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(ENERGY_TYPE);
    }

    public NetworkType getType() {
        return type;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);

        if (!level.isClientSide) {
            stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyFromStack -> {
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity != null) {
                    blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyFromBlockEntity -> energyFromBlockEntity.receiveEnergy(energyFromStack.getEnergyStored(), false));
                }
            });
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);

        if (!level.isClientSide) {
            INetwork network = API.instance().getNetworkManager((ServerLevel) level).getNetwork(pos);
            if (network instanceof Network) {
                ((Network) network).setRedstonePowered(level.hasNeighborSignal(pos));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result != InteractionResult.PASS) {
            return result;
        }

        ColorMap<ControllerBlock> colorMap = type == NetworkType.CREATIVE ? RSBlocks.CREATIVE_CONTROLLER : RSBlocks.CONTROLLER;
        DyeColor color = DyeColor.getColor(player.getItemInHand(hand));

        if (color != null && !state.getBlock().equals(colorMap.get(color).get())) {
            BlockState newState = colorMap.get(color).get().defaultBlockState().setValue(ENERGY_TYPE, state.getValue(ENERGY_TYPE));

            return RSBlocks.CONTROLLER.setBlockState(newState, player.getItemInHand(hand), level, pos, player);
        }

        if (!level.isClientSide) {
            return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openScreen(
                (ServerPlayer) player,
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("gui.refinedstorage." + (ControllerBlock.this.getType() == NetworkType.CREATIVE ? "creative_" : "") + "controller");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
                        return new ControllerContainerMenu((ControllerBlockEntity) level.getBlockEntity(pos), player, i);
                    }
                },
                pos
            ));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() instanceof ControllerBlock) {
            return;
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ControllerBlockEntity(type, pos, state);
    }

    public enum EnergyType implements StringRepresentable {
        OFF("off"),
        NEARLY_OFF("nearly_off"),
        NEARLY_ON("nearly_on"),
        ON("on");

        private final String name;

        EnergyType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
