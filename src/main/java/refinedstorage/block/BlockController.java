package refinedstorage.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageGui;
import refinedstorage.apiimpl.network.NetworkMaster;
import refinedstorage.apiimpl.network.NetworkMasterRegistry;
import refinedstorage.item.ItemBlockController;
import refinedstorage.tile.controller.TileController;

import java.util.ArrayList;
import java.util.List;

public class BlockController extends BlockBase {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumControllerType.class);
    public static final PropertyInteger ENERGY = PropertyInteger.create("energy", 0, 8);

    public BlockController() {
        super("controller");
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        for (int i = 0; i <= 1; i++) {
            subItems.add(ItemBlockController.createStackWithNBT(new ItemStack(item, 1, i)));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{
            DIRECTION,
            TYPE,
            ENERGY
        });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, meta == 0 ? EnumControllerType.NORMAL : EnumControllerType.CREATIVE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE) == EnumControllerType.NORMAL ? 0 : 1;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileController controller = (TileController) world.getTileEntity(pos);

        return super.getActualState(state, world, pos)
            .withProperty(ENERGY, (int) Math.ceil((float) controller.getEnergy() / (float) NetworkMaster.ENERGY_CAPACITY * 8f));
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (((TileController) world.getTileEntity(pos)).getNetwork() == null) {
                player.addChatComponentMessage(new TextComponentTranslation("misc.refinedstorage:no_network"));
            } else {
                player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.CONTROLLER, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        if (!world.isRemote) {
            NetworkMaster network = new NetworkMaster(pos, world);

            NBTTagCompound tag = stack.getTagCompound();

            if (tag != null && tag.hasKey(NetworkMaster.NBT_ENERGY)) {
                network.getEnergy().receiveEnergy(tag.getInteger(NetworkMaster.NBT_ENERGY), false);
            }

            NetworkMasterRegistry.add(world, network);
        }

        super.onBlockPlacedBy(world, pos, state, player, stack);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            NetworkMasterRegistry.remove(world, pos);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = new ArrayList<ItemStack>();

        ItemStack stack = new ItemStack(RefinedStorageBlocks.CONTROLLER, 1, RefinedStorageBlocks.CONTROLLER.getMetaFromState(state));

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(NetworkMaster.NBT_ENERGY, ((TileController) world.getTileEntity(pos)).getEnergyStored(null));
        stack.setTagCompound(tag);

        drops.add(stack);

        return drops;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        return ((TileController) world.getTileEntity(pos)).getEnergyScaled(15);
    }

    @Override
    public Item createItemForBlock() {
        return new ItemBlockController();
    }
}
