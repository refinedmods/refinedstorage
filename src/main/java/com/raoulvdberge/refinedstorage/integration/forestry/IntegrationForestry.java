package com.raoulvdberge.refinedstorage.integration.forestry;

import com.raoulvdberge.refinedstorage.api.util.IComparer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class IntegrationForestry {
	private static final String ID = "forestry";
	private static final String QUEEN_BEE = "forestry:bee_queen_ge";
	private static final String PRINCESS_BEE = "forestry:bee_princess_ge";
	private static final String GEN_TAG = "GEN";
	
    public static boolean isLoaded() {
        return Loader.isModLoaded(ID);
    }
    
    /**
     * Determines if any ItemStack in a list contains a Forestry Queen/Princess Bee
     * and flags it for NBT strip if it contains a "GEN" NBT tag.
     * 
     * @param flags	the flags to compare on, see {@link IComparer}
     * @param items	list of stacks being considered
     * @return		previous compare flags with COMPARE_STRIP_NBT if any itemstack in list is a 
     * 				Forestry Queen/Princess Bee with a GEN NBT tag, otherwise only previous compare flags
     */
	public static int isItem(int flags, ItemStack... items) {
		for (ItemStack item : items) {
			switch (item.getItem().getRegistryName().toString()) {
			case QUEEN_BEE:
			case PRINCESS_BEE:
				if(item.getTagCompound().hasKey(GEN_TAG)) {
					return (flags | IComparer.COMPARE_STRIP_NBT);
				}
			default: continue;
			}
		}
		return flags;
	}
    
    /**
     * Determines if the stack is a Forestry Princess Bee with a GEN NBTtag.
     * 
     * @param	stack	stack being considered
     * @return			true if itemstack is a Forestry Princess Bee with a GEN NBT tag,
     * 					otherwise false, see {@link IComparer}
     */
    @SuppressWarnings("unused")
	private static boolean isPrincess(ItemStack stack) {
    	 return stack.getItem().getRegistryName().toString().equals(PRINCESS_BEE) && stack.getTagCompound().hasKey(GEN_TAG);
    }
    
    /**
     * Determines if the stack is a Forestry Queen Bee with a GEN NBTtag.
     * 
     * @param	stack	stack being considered
     * @return			true if itemstack is a Forestry Queen Bee with a GEN NBT tag,
     * 					otherwise false, see {@link IComparer}
     */
    @SuppressWarnings("unused")
	private static boolean isQueen(ItemStack stack) {
    	 return stack.getItem().getRegistryName().toString().equals(QUEEN_BEE) && stack.getTagCompound().hasKey(GEN_TAG);
    }
}