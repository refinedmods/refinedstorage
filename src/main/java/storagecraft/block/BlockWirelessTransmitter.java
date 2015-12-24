package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileWirelessTransmitter;

public class BlockWirelessTransmitter extends BlockBase implements ITileEntityProvider
{
	private IIcon icon;
	private IIcon workingIcon;
	private IIcon sideIcon;
	private IIcon workingSideIcon;

	public BlockWirelessTransmitter()
	{
		super("wirelessTransmitter");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraft.GUI.WIRELESS_TRANSMITTER, world, x, y, z);
		}

		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileWirelessTransmitter();
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		icon = register.registerIcon("storagecraft:wirelessTransmitter");
		workingIcon = register.registerIcon("storagecraft:wirelessTransmitterWorking");
		sideIcon = register.registerIcon("storagecraft:wirelessTransmitterSide");
		workingSideIcon = register.registerIcon("storagecraft:wirelessTransmitterSideWorking");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileWirelessTransmitter tile = (TileWirelessTransmitter) world.getTileEntity(x, y, z);

		if (side == tile.getDirection().ordinal())
		{
			return tile.isWorking() ? workingIcon : icon;
		}

		return tile.isWorking() ? workingSideIcon : sideIcon;
	}

	@Override
	public IIcon getIcon(int side, int damage)
	{
		if (side == 3)
		{
			return icon;
		}

		return sideIcon;
	}
}
