package storagecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import storagecraft.SC;
import storagecraft.tile.TileSC;

public class BlockSC extends Block {
	private String name;

	public BlockSC(String name) {
		super(Material.rock);

		this.name = name;

		setCreativeTab(SC.TAB);
		setBlockTextureName("storagecraft:" + name);
	}

	@Override
	public String getUnlocalizedName() {
		return "block." + SC.ID + ":" + name;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack) {
		super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);

		if (world.getTileEntity(x, y, z) instanceof TileSC) {
			ForgeDirection direction = ForgeDirection.UNKNOWN;

			int facing = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

			switch (facing) {
				case 0:
					direction = ForgeDirection.SOUTH;
					break;
				case 1:
					direction = ForgeDirection.WEST;
					break;
				case 2:
					direction = ForgeDirection.NORTH;
					break;
				case 3:
					direction = ForgeDirection.EAST;
					break;
			}

			((TileSC) world.getTileEntity(x, y, z)).setDirection(direction);
		}
	}
}
