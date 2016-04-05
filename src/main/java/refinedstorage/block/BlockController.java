package refinedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.item.ItemBlockBase;
import refinedstorage.tile.TileController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockController extends BlockBase {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumControllerType.class);
    public static final PropertyInteger ENERGY = PropertyInteger.create("energy", 0, 8);

    public BlockController() {
        super("controller");
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        for (int i = 0; i <= 1; i++) {
            subItems.add(new ItemStack(item, 1, i));
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
        return super.getActualState(state, world, pos)
            .withProperty(ENERGY, ((TileController) world.getTileEntity(pos)).getEnergyScaled(8));
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
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.CONTROLLER, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        ((TileController) world.getTileEntity(pos)).onDestroyed();

        super.breakBlock(world,pos,state);
    }

    //Unless making a ItemBlock this seems to be the only solution to store NBT on the dropped stack
    // any function called after this like get drops will not be able to get the tile
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack)
    {
        player.addStat(StatList.func_188055_a(this));
        player.addExhaustion(0.025F);

        harvesters.set(player);
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.fortune, stack);
        if ((te instanceof TileController)) {
            TileController controller = (TileController) te;
            ItemStack item = new ItemStack(getItemDropped(state, null, 0), 1, damageDropped(state));
            NBTTagCompound tag = new NBTTagCompound();
            controller.writeItemToNBT(tag);
            item.setTagCompound(tag);
            if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots)
            {
                spawnAsEntity(worldIn, pos, item);
            }
        }
        harvesters.set(null);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        super.onBlockPlacedBy(world, pos, state, player, itemStack);

        NBTTagCompound tag = itemStack.getTagCompound();
        if(tag != null)
        {
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileController)
            {
                TileController controller = (TileController)tile;
                controller.readItemFromNBT(tag);
            }
        }
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
        return new ItemBlockBase(this, true);
    }


}
