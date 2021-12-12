package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.container.ControllerContainer;
import com.refinedmods.refinedstorage.tile.ControllerTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.ColorMap;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ControllerBlock extends BaseBlock {
    public enum EnergyType implements IStringSerializable {
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

    public static final EnumProperty<EnergyType> ENERGY_TYPE = EnumProperty.create("energy_type", EnergyType.class);

    private final NetworkType type;

    public ControllerBlock(NetworkType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
        this.registerDefaultState(getStateDefinition().any().setValue(ENERGY_TYPE, EnergyType.OFF));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(ENERGY_TYPE);
    }

    public NetworkType getType() {
        return type;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ControllerTile(type);
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);

        if (!world.isClientSide) {
            stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyFromStack -> {
                TileEntity tile = world.getBlockEntity(pos);

                if (tile != null) {
                    tile.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyFromTile -> energyFromTile.receiveEnergy(energyFromStack.getEnergyStored(), false));
                }
            });
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);

        if (!world.isClientSide) {
            INetwork network = API.instance().getNetworkManager((ServerWorld) world).getNetwork(pos);
            if (network instanceof Network) {
                ((Network) network).setRedstonePowered(world.hasNeighborSignal(pos));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ActionResultType result = super.use(state, world, pos, player, hand, hit);
        if (result != ActionResultType.PASS) {
            return result;
        }

        ColorMap<ControllerBlock> colorMap = type == NetworkType.CREATIVE ? RSBlocks.CREATIVE_CONTROLLER : RSBlocks.CONTROLLER;
        DyeColor color = DyeColor.getColor(player.getItemInHand(hand));

        if (color != null && !state.getBlock().equals(colorMap.get(color).get())) {
            BlockState newState = colorMap.get(color).get().defaultBlockState().setValue(ENERGY_TYPE, state.getValue(ENERGY_TYPE));

            return RSBlocks.CONTROLLER.setBlockState(newState, player.getItemInHand(hand), world, pos, player);
        }

        if (!world.isClientSide) {
            return NetworkUtils.attemptModify(world, pos, player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("gui.refinedstorage." + (ControllerBlock.this.getType() == NetworkType.CREATIVE ? "creative_" : "") + "controller");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
                        return new ControllerContainer((ControllerTile) world.getBlockEntity(pos), player, i);
                    }
                },
                pos
            ));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() instanceof ControllerBlock) {
            return;
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }
}
