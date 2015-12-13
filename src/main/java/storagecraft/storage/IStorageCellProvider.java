package storagecraft.storage;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IStorageCellProvider {
	public List<ItemStack> getStorageCells();
}
