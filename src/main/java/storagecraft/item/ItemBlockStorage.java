package storagecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import storagecraft.block.EnumStorageType;
import storagecraft.storage.NBTStorage;
import storagecraft.tile.TileStorage;

public class ItemBlockStorage extends ItemBlockBase
{
	public ItemBlockStorage(Block block)
	{
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		EnumStorageType type = EnumStorageType.getById(stack.getMetadata());

		NBTTagCompound tag = stack.getTagCompound().getCompoundTag(TileStorage.NBT_STORAGE);

		if (type == EnumStorageType.TYPE_CREATIVE)
		{
			list.add(String.format(StatCollector.translateToLocal("misc.storagecraft:storage.stored"), NBTStorage.getStored(tag)));
		}
		else
		{
			list.add(String.format(StatCollector.translateToLocal("misc.storagecraft:storage.stored_capacity"), NBTStorage.getStored(tag), type.getCapacity()));
		}
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
	{
		super.onCreated(stack, world, player);

		initNBT(stack);
	}

	public static ItemStack initNBT(ItemStack stack)
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setTag(TileStorage.NBT_STORAGE, NBTStorage.getBaseNBT());

		stack.setTagCompound(tag);

		return stack;
	}
}
