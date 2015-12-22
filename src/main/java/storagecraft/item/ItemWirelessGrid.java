package storagecraft.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileGrid;

public class ItemWirelessGrid extends ItemBase
{
	public static final String NBT_GRID_X = "GridX";
	public static final String NBT_GRID_Y = "GridY";
	public static final String NBT_GRID_Z = "GridZ";

	private IIcon iconConnected;
	private IIcon iconDisconnected;

	public ItemWirelessGrid()
	{
		super("wirelessGrid");

		setMaxStackSize(1);
	}

	@Override
	public void registerIcons(IIconRegister register)
	{
		iconConnected = register.registerIcon("storagecraft:wirelessGridConnected");
		iconDisconnected = register.registerIcon("storagecraft:wirelessGridDisconnected");
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			TileEntity tile = world.getTileEntity(x, y, z);

			if (tile instanceof TileGrid)
			{
				stack.stackTagCompound = new NBTTagCompound();
				stack.stackTagCompound.setInteger(NBT_GRID_X, x);
				stack.stackTagCompound.setInteger(NBT_GRID_Y, y);
				stack.stackTagCompound.setInteger(NBT_GRID_Z, z);

				player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocalFormatted("misc.storagecraft:wirelessGrid.set", x, y, z)));

				return true;
			}
		}

		return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (isValid(stack))
			{
				if (isInRange(stack, player))
				{
					int x = getX(stack);
					int y = getY(stack);
					int z = getZ(stack);

					TileEntity tile = world.getTileEntity(x, y, z);

					if (tile instanceof TileGrid)
					{
						player.openGui(StorageCraft.INSTANCE, StorageCraft.GUI.GRID, world, x, y, z);
					}
					else
					{
						player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wirelessGrid.notFound")));
					}
				}
				else
				{
					player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wirelessGrid.outOfRange")));
				}
			}
			else
			{
				player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wirelessGrid.notSet")));
			}
		}

		return stack;
	}

	public int getX(ItemStack stack)
	{
		return stack.stackTagCompound.getInteger(NBT_GRID_X);
	}

	public int getY(ItemStack stack)
	{
		return stack.stackTagCompound.getInteger(NBT_GRID_Y);
	}

	public int getZ(ItemStack stack)
	{
		return stack.stackTagCompound.getInteger(NBT_GRID_Z);
	}

	public boolean isInRange(ItemStack stack, EntityPlayer player)
	{
		return (int) Math.sqrt(Math.pow(getX(stack) - player.posX, 2) + Math.pow(getY(stack) - player.posY, 2) + Math.pow(getZ(stack) - player.posZ, 2)) < 64;
	}

	public boolean isValid(ItemStack stack)
	{
		return stack.stackTagCompound != null && stack.stackTagCompound.hasKey(NBT_GRID_X) && stack.stackTagCompound.hasKey(NBT_GRID_Y) && stack.stackTagCompound.hasKey(NBT_GRID_Z);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass)
	{
		return getIconIndex(stack);
	}

	@Override
	public IIcon getIconIndex(ItemStack stack)
	{
		return isValid(stack) ? iconConnected : iconDisconnected;
	}
}
