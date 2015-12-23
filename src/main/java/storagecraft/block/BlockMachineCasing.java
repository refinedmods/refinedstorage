package storagecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockMachineCasing extends BlockBase
{
	private IIcon icon;

	public BlockMachineCasing()
	{
		super("machineCasing");
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		icon = register.registerIcon("storagecraft:generic");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		return icon;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return icon;
	}
}
