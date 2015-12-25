package storagecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileBase;
import storagecraft.util.InventoryUtils;

public abstract class BlockBase extends Block
{
	private String name;

	public BlockBase(String name)
	{
		super(Material.rock);

		this.name = name;

		setCreativeTab(StorageCraft.TAB);
	}

	@Override
	public String getUnlocalizedName()
	{
		return "block." + StorageCraft.ID + ":" + name;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
	{
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileBase)
		{
			EnumFacing dir = ((TileBase) tile).getDirection();

			int newDir = dir.ordinal() + 1;

			if (newDir > EnumFacing.VALUES.length - 1)
			{
				newDir = 0;
			}

			((TileBase) tile).setDirection(EnumFacing.getFront(newDir));

			world.markBlockForUpdate(pos);

			return true;
		}

		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack)
	{
		super.onBlockPlacedBy(world, pos, state, player, itemStack);

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileBase)
		{
			((TileBase) tile).setDirection(BlockPistonBase.func_180695_a(world, pos, player));
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) // @TODO: Make this work all
	{
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileBase && ((TileBase) tile).getDroppedInventory() != null)
		{
			InventoryUtils.dropInventory(world, ((TileBase) tile).getDroppedInventory(), pos.getX(), pos.getY(), pos.getZ());
		}

		super.onBlockDestroyedByPlayer(world, pos, state);
	}
}
