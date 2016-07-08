package refinedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
        super(Material.ROCK);

        this.name = name;

        setHardness(1.9F);
        setRegistryName(RefinedStorage.ID, name);
        setCreativeTab(RefinedStorage.INSTANCE.tab);
    }

    @Override
    public String getUnlocalizedName() {
        return "block." + RefinedStorage.ID + ":" + name;
    }

    protected BlockStateContainer.Builder createBlockStateBuilder() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);

        if (getDirectionType() != null) {
            builder.add(DIRECTION);
        }

        return builder;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder().build();
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
        if (getDirectionType() != null) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileBase) {
                return state.withProperty(DIRECTION, ((TileBase) tile).getDirection());
            }
        }

        return state;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (!world.isRemote && getDirectionType() != null) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileBase) {
                ((TileBase) tile).setDirection(getDirectionType().getNext(((TileBase) tile).getDirection()));

                RefinedStorageUtils.updateBlock(world, pos);

                return true;
            }
        }

        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, player, stack);

        if (getDirectionType() != null) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileBase) {
                EnumFacing facing = getDirectionType().getFrom(pos, player);

                if (player.isSneaking() && canBePlacedOpposite()) {
                    facing = facing.getOpposite();
                }

                ((TileBase) tile).setDirection(facing);
            }
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
        return willHarvest ? true : super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);

        world.setBlockToAir(pos);
    }

    public Item createItemForBlock() {
        return new ItemBlockBase(this, false);
    }

    public boolean canBePlacedOpposite() {
        return false;
    }

    public EnumDirectionType getDirectionType() {
        return EnumDirectionType.HORIZONTAL;
    }
}
