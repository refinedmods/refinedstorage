package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.NetworkType;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.refinedmods.refinedstorage.container.ControllerContainer;
import com.refinedmods.refinedstorage.tile.ControllerTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
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
        public String getString() {
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
        this.setRegistryName(RS.ID, type == NetworkType.CREATIVE ? "creative_controller" : "controller");
        this.setDefaultState(getStateContainer().getBaseState().with(ENERGY_TYPE, EnergyType.OFF).with(BlockUtils.COLOR_PROPERTY, DyeColor.LIGHT_BLUE));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        builder.add(ENERGY_TYPE);
        builder.add(BlockUtils.COLOR_PROPERTY);
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
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, entity, stack);

        if (!world.isRemote) {
            stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyFromStack -> {
                TileEntity tile = world.getTileEntity(pos);

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

        if (!world.isRemote) {
            INetwork network = API.instance().getNetworkManager((ServerWorld) world).getNetwork(pos);
            if (network instanceof Network) {
                ((Network) network).setRedstonePowered(world.isBlockPowered(pos));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            ActionResultType result = super.onBlockActivated(state, world, pos, player, hand, hit);
            if (result != ActionResultType.PASS) {
                return result;
            }
            ActionResultType colorResult = BlockUtils.changeBlockColor(state, player.getHeldItem(hand), world, pos, player);
            if (colorResult != ActionResultType.PASS) {
                return colorResult;
            }
            return NetworkUtils.attemptModify(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("gui.refinedstorage." + (ControllerBlock.this.getType() == NetworkType.CREATIVE ? "creative_" : "") + "controller");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
                        return new ControllerContainer((ControllerTile) world.getTileEntity(pos), player, i);
                    }
                },
                pos
            ));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putInt(ColoredNetworkBlock.COLOR_NBT, state.get(BlockUtils.COLOR_PROPERTY).getId());
        stack.setTag(tag);
        return stack;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null && context.getItem().hasTag() && context.getItem().getTag().contains(ColoredNetworkBlock.COLOR_NBT)) {
            DyeColor color = DyeColor.byId(context.getItem().getTag().getInt(ColoredNetworkBlock.COLOR_NBT));
            state = state.with(BlockUtils.COLOR_PROPERTY, color);
        }
        return state;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        ItemStack stack = new ItemStack(this);
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putInt(ColoredNetworkBlock.COLOR_NBT, DyeColor.LIGHT_BLUE.getId());
        stack.setTag(tag);
        items.add(stack);
    }
}
