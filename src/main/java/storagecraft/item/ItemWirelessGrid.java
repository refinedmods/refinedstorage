package storagecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftGUI;
import storagecraft.block.EnumGridType;
import storagecraft.tile.TileGrid;
import storagecraft.tile.TileWirelessTransmitter;

import java.util.List;

public class ItemWirelessGrid extends ItemBase
{
	public static final String NBT_WIRELESS_TRANSMITTER_X = "WirelessTransmitterX";
	public static final String NBT_WIRELESS_TRANSMITTER_Y = "WirelessTransmitterY";
	public static final String NBT_WIRELESS_TRANSMITTER_Z = "WirelessTransmitterZ";

	public ItemWirelessGrid()
	{
		super("wireless_grid");

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
			list.add(StatCollector.translateToLocalFormatted("misc.storagecraft:wireless_grid.tooltip", getX(stack), getY(stack), getZ(stack)));
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

					TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

					if (tile instanceof TileWirelessTransmitter)
					{
						TileWirelessTransmitter wirelessTransmitter = (TileWirelessTransmitter) tile;

						if (wirelessTransmitter.isWorking())
						{
							TileGrid grid = wirelessTransmitter.getGrid(stack.getItemDamage() == 1 ? EnumGridType.CRAFTING : EnumGridType.NORMAL);

							if (grid == null)
							{
								player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wireless_grid.no_grid." + stack.getItemDamage())));
							}
							else
							{
								player.openGui(StorageCraft.INSTANCE, StorageCraftGUI.GRID, world, grid.getPos().getX(), grid.getPos().getY(), grid.getPos().getZ());
							}
						}
						else
						{
							player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wireless_grid.not_working")));
						}
					}
					else
					{
						player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wireless_grid.not_found")));
					}
				}
				else
				{
					player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wireless_grid.out_of_range")));
				}
			}
			else
			{
				player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("misc.storagecraft:wireless_grid.not_set." + stack.getItemDamage())));
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
		return stack.getTagCompound().getInteger(NBT_WIRELESS_TRANSMITTER_X);
	}

	public int getY(ItemStack stack)
	{
		return stack.getTagCompound().getInteger(NBT_WIRELESS_TRANSMITTER_Y);
	}

	public int getZ(ItemStack stack)
	{
		return stack.getTagCompound().getInteger(NBT_WIRELESS_TRANSMITTER_Z);
	}

	public boolean isInRange(ItemStack stack, EntityPlayer player)
	{
		return (int) Math.sqrt(Math.pow(getX(stack) - player.posX, 2) + Math.pow(getY(stack) - player.posY, 2) + Math.pow(getZ(stack) - player.posZ, 2)) < 64;
	}

	public boolean isValid(ItemStack stack)
	{
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_WIRELESS_TRANSMITTER_X) && stack.getTagCompound().hasKey(NBT_WIRELESS_TRANSMITTER_Y) && stack.getTagCompound().hasKey(NBT_WIRELESS_TRANSMITTER_Z);
	}
}
