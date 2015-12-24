package storagecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftGUI;
import storagecraft.tile.TileGrid;
import storagecraft.tile.TileWirelessTransmitter;

public class ItemWirelessGrid extends ItemBase
{
	public static final String NBT_WIRELESS_TRANSMITTER_X = "WirelessTransmitterX";
	public static final String NBT_WIRELESS_TRANSMITTER_Y = "WirelessTransmitterY";
	public static final String NBT_WIRELESS_TRANSMITTER_Z = "WirelessTransmitterZ";

	private IIcon iconConnected;
	private IIcon iconDisconnected;

	public ItemWirelessGrid()
	{
		super("wirelessGrid");

		setMaxStackSize(1);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < 2; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		if (isValid(stack))
		{
			list.add(StatCollector.translateToLocalFormatted("misc.storagecraft:wirelessGrid.tooltip", getX(stack), getY(stack), getZ(stack)));
		}
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

					if (tile instanceof TileWirelessTransmitter)
					{
						TileWirelessTransmitter wirelessTransmitter = (TileWirelessTransmitter) tile;

						if (wirelessTransmitter.isWorking())
						{
							TileGrid grid = wirelessTransmitter.getGrid(stack.getItemDamage());

							if (grid == null)
							{
								player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wirelessGrid.noGrid." + stack.getItemDamage())));
							}
							else
							{
								player.openGui(StorageCraft.INSTANCE, StorageCraftGUI.GRID, world, grid.xCoord, grid.yCoord, grid.zCoord);
							}
						}
						else
						{
							player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wirelessGrid.notWorking")));
						}
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
				player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wirelessGrid.notSet." + stack.getItemDamage())));
			}
		}

		return stack;
	}

	public boolean isCrafting(ItemStack stack)
	{
		return stack.getItemDamage() == 1;
	}

	public int getX(ItemStack stack)
	{
		return stack.stackTagCompound.getInteger(NBT_WIRELESS_TRANSMITTER_X);
	}

	public int getY(ItemStack stack)
	{
		return stack.stackTagCompound.getInteger(NBT_WIRELESS_TRANSMITTER_Y);
	}

	public int getZ(ItemStack stack)
	{
		return stack.stackTagCompound.getInteger(NBT_WIRELESS_TRANSMITTER_Z);
	}

	public boolean isInRange(ItemStack stack, EntityPlayer player)
	{
		return (int) Math.sqrt(Math.pow(getX(stack) - player.posX, 2) + Math.pow(getY(stack) - player.posY, 2) + Math.pow(getZ(stack) - player.posZ, 2)) < 64;
	}

	public boolean isValid(ItemStack stack)
	{
		return stack.stackTagCompound != null && stack.stackTagCompound.hasKey(NBT_WIRELESS_TRANSMITTER_X) && stack.stackTagCompound.hasKey(NBT_WIRELESS_TRANSMITTER_Y) && stack.stackTagCompound.hasKey(NBT_WIRELESS_TRANSMITTER_Z);
	}

	@Override
	public void registerIcons(IIconRegister register)
	{
		iconConnected = register.registerIcon("storagecraft:wirelessGridConnected");
		iconDisconnected = register.registerIcon("storagecraft:wirelessGridDisconnected");
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
