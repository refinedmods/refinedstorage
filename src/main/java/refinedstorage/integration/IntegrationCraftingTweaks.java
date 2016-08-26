package refinedstorage.integration;

import com.google.common.base.Function;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;

public class IntegrationCraftingTweaks {

	public static final String MOD_ID = "craftingtweaks";

	public static void register() {
		if(Loader.isModLoaded(MOD_ID)) {
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setString("ContainerClass", ContainerGrid.class.getName());
			tagCompound.setString("ContainerCallback", ContainerCallback.class.getName());
			tagCompound.setInteger("GridSlotNumber", 36);
			tagCompound.setString("AlignToGrid", "left");
			FMLInterModComms.sendMessage(MOD_ID, "RegisterProvider", tagCompound);
		}
	}

	public static class ContainerCallback implements Function<ContainerGrid, Boolean> {
		@Override
		public Boolean apply(ContainerGrid containerGrid) {
			return containerGrid.getGrid().getType() == EnumGridType.CRAFTING;
		}
	}
}
