package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

public class ControllerBlock extends Block {
    public enum Type {
        NORMAL,
        CREATIVE
    }

    public enum EnergyType implements IStringSerializable {
        OFF("off"),
        NEARLY_OFF("nearly_off"),
        NEARLY_ON("nearly_on"),
        ON("on");

        private String name;

        EnergyType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final EnumProperty<EnergyType> ENERGY_TYPE = EnumProperty.create("energy_type", EnergyType.class);

    private Type type;

    public ControllerBlock(Type type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
        this.setRegistryName(RS.ID, type == Type.CREATIVE ? "creative_controller" : "controller");
        this.setDefaultState(getStateContainer().getBaseState().with(ENERGY_TYPE, EnergyType.OFF));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        builder.add(ENERGY_TYPE);
    }

    public Type getType() {
        return type;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
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

    /* TODO Controller
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.CONTROLLER, player, world, pos, side);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
        ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));

        stack.setTagCompound(new CompoundNBT());
        stack.getTagCompound().putInt(TileController.NBT_ENERGY, ((TileController) world.getTileEntity(pos)).getEnergy().getStored());

        drops.add(stack);
    }*/
}
