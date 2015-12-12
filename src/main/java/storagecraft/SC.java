package storagecraft;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import storagecraft.proxy.CommonProxy;

@Mod(modid = SC.ID, version = SC.VERSION)
public class SC {
	public static final class GUI {
		public static final int CONTROLLER = 0;
		public static final int GRID = 1;
		public static final int DRIVE = 2;
	}

	public static final String ID = "storagecraft";
	public static final String VERSION = "1.0";
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ID);
	public static final CreativeTabs TAB = new CreativeTabs(ID) {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(SCBlocks.CONTROLLER);
		}
	};
	@SidedProxy(clientSide = "storagecraft.proxy.ClientProxy", serverSide = "storagecraft.proxy.ServerProxy")
	public static CommonProxy PROXY;
	@Instance
	public static SC INSTANCE;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		PROXY.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		PROXY.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		PROXY.postInit(e);
	}

	public static void saveInventory(IInventory inventory, NBTTagCompound nbt) {
		NBTTagList tagList = new NBTTagList();

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) != null) {
				NBTTagCompound compoundTag = new NBTTagCompound();

				compoundTag.setInteger("Slot", i);

				inventory.getStackInSlot(i).writeToNBT(compoundTag);

				tagList.appendTag(compoundTag);
			}
		}

		nbt.setTag("Inventory", tagList);
	}

	public static void restoreInventory(IInventory inventory, NBTTagCompound nbt) {
		if (nbt.hasKey("Inventory")) {
			NBTTagList tagList = nbt.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < tagList.tagCount(); i++) {
				int slot = tagList.getCompoundTagAt(i).getInteger("Slot");

				ItemStack stack = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));

				inventory.setInventorySlotContents(slot, stack);
			}
		}
	}

	// https://github.com/cpw/ironchest/blob/master/src/main/java/cpw/mods/ironchest/BlockIronChest.java#L200
	public static void dropInventory(World world, IInventory inventory, int x, int y, int z, int newSize) {
		Random random = world.rand;

		for (int i = newSize; i < inventory.getSizeInventory(); ++i) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack == null) {
				continue;
			}

			float xx = random.nextFloat() * 0.8F + 0.1F;
			float yy = random.nextFloat() * 0.8F + 0.1F;
			float zz = random.nextFloat() * 0.8F + 0.1F;

			while (stack.stackSize > 0) {
				int amount = random.nextInt(21) + 10;

				if (amount > stack.stackSize) {
					amount = stack.stackSize;
				}

				stack.stackSize -= amount;

				EntityItem entity = new EntityItem(world, (float) x + xx, (float) y + (newSize > 0 ? 1 : 0) + yy, (float) z + zz, new ItemStack(stack.getItem(), amount, stack.getItemDamage()));

				entity.motionX = (float) random.nextGaussian() * 0.05F;
				entity.motionY = (float) random.nextGaussian() * 0.05F + 0.2F;
				entity.motionZ = (float) random.nextGaussian() * 0.05F;

				if (stack.hasTagCompound()) {
					entity.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
				}

				world.spawnEntityInWorld(entity);
			}
		}
	}
}
