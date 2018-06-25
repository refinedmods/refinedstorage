package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.item.ItemBlockController;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockController extends BlockBase {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", ControllerType.class);
    public static final PropertyEnum ENERGY_TYPE = PropertyEnum.create("energy_type", ControllerEnergyType.class);

    public BlockController() {
        super("controller");
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i <= 1; i++) {
            items.add(ItemBlockController.createStackWithNBT(new ItemStack(this, 1, i)));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder()
            .add(TYPE)
            .add(ENERGY_TYPE)
            .build();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, meta == 0 ? ControllerType.NORMAL : ControllerType.CREATIVE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE) == ControllerType.NORMAL ? 0 : 1;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos)
            .withProperty(ENERGY_TYPE, ((TileController) world.getTileEntity(pos)).getEnergyType());
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileController();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.CONTROLLER, player, world, pos, side);
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        if (!world.isRemote) {
            TileController controller = (TileController) world.getTileEntity(pos);

            NBTTagCompound tag = stack.getTagCompound();

            if (tag != null && tag.hasKey(TileController.NBT_ENERGY)) {
                controller.getEnergy().setStored(tag.getInteger(TileController.NBT_ENERGY));
            }
        }

        super.onBlockPlacedBy(world, pos, state, player, stack);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack stack = new ItemStack(RSBlocks.CONTROLLER, 1, RSBlocks.CONTROLLER.getMetaFromState(state));

        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(TileController.NBT_ENERGY, ((TileController) world.getTileEntity(pos)).getEnergy().getStored());
        stack.getTagCompound().setInteger(TileController.NBT_ENERGY_CAPACITY, ((TileController) world.getTileEntity(pos)).getEnergy().getMaxEnergy());

        drops.add(stack);
    }

    @Override
    public Item createItem() {
        return new ItemBlockController();
    }

    @Nullable
    @Override
    public Direction getDirection() {
        return null;
    }
}
