package refinedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.item.ItemBlockBase;
import refinedstorage.tile.TileBase;

public abstract class BlockBase extends Block {
    public static final PropertyDirection DIRECTION = PropertyDirection.create("direction");

    private String name;

    public BlockBase(String name) {
        super(Material.rock);

        this.name = name;

        setHardness(1.9F);
        setRegistryName(RefinedStorage.ID, name);
        setCreativeTab(RefinedStorage.TAB);
    }

    @Override
    public String getUnlocalizedName() {
        return "block." + RefinedStorage.ID + ":" + name;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{
            DIRECTION,
        });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileBase) {
            return state.withProperty(DIRECTION, ((TileBase) tile).getDirection());
        }

        return state;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);

        if (!world.isRemote && tile instanceof TileBase) {
            EnumFacing dir = ((TileBase) tile).getDirection();

            int newDir = dir.ordinal() + 1;

            if (newDir > EnumFacing.VALUES.length - 1) {
                newDir = 0;
            }

            ((TileBase) tile).setDirection(EnumFacing.getFront(newDir));

            RefinedStorageUtils.updateBlock(world, pos);

            return true;
        }

        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        super.onBlockPlacedBy(world, pos, state, player, itemStack);

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileBase) {
            EnumFacing facing = BlockPistonBase.getFacingFromEntity(pos, player);

            if (player.isSneaking() && hasOppositeFacingOnSneakPlace()) {
                facing = facing.getOpposite();
            }

            ((TileBase) tile).setDirection(facing);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileBase && ((TileBase) tile).getDroppedItems() != null) {
            IItemHandler handler = ((TileBase) tile).getDroppedItems();

            for (int i = 0; i < handler.getSlots(); ++i) {
                if (handler.getStackInSlot(i) != null) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                }
            }
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);

        world.setBlockToAir(pos);
    }

    public Item createItemForBlock() {
        return new ItemBlockBase(this, false);
    }

    public boolean hasOppositeFacingOnSneakPlace() {
        return false;
    }
}
