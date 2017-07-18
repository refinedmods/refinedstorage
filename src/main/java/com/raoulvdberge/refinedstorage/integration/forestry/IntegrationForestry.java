package com.raoulvdberge.refinedstorage.integration.forestry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public final class IntegrationForestry {
	public enum Tag {
		GENOME("Genome", 64),				// Bees, Trees, Butterflies
		MATE("Mate", 128),					// Bees, Butterflies
		GEN("GEN", 256),					// Bees
		HEALTH("Health", 512),				// Bees, Butterflies
		IS_ANLAYZED("IsAnalyzed", 1024),	// Bees, Trees, Butterflies
		MAX_HEALTH("MaxH", 2048),			// Bees, Butterflies
		AGE("Age", 4096);					// Butterflies
		
		private String name;
		private int flag;
		
		Tag(String name, int flag){
			this.name = name;
			this.flag = flag;
		}
		
		public String getName() {
			return name;
		}
		
		public int getFlag() {
			return flag;
		}
	}
	
	private static final String ID = "forestry";
	private static final String QUEEN_BEE = "forestry:bee_queen_ge";
	private static final String PRINCESS_BEE = "forestry:bee_princess_ge";
	//private static final String DRONE_BEE = "forestry:bee_drone_ge";
	//private static final String LARVAE_BEE = "forestry:bee_larvae_ge";
	
	//private static final String SAPLING = "forestry:sapling";
	//private static final String POLLEN = "forestry:pollen_fertile";
	
	//private static final String BUTTERFLY = "forestry:butterfly_ge";
	//private static final String SERUM = "forestry:serum_ge";
	//private static final String CATERPILLAR = "forestry:caterpillar_ge";
	//private static final String COCOON = "forestry:cocoon_ge";
	
    public static boolean isLoaded() {
        return Loader.isModLoaded(ID);
    }
    
    /**
     * Determines if the item is breedable in Forestry.
     * 
     * @param item	item being determined if breedable
     * @return		true if breedable, else false
     */
	public static boolean isBreedable(ItemStack item) {
		switch (item.getItem().getRegistryName().toString()) {
		case QUEEN_BEE:
		case PRINCESS_BEE:
			return true;
		/* Do nothing with these for now
		case DRONE_BEE:
		case LARVAE:
		case SAPLING:
		case POLLEN:
		case BUTTERFLY:
		case SERUM:
		case CATERPILLAR:
		case COCOON:
		*/
		default: return false;
		}
	}
	
	/**
	 * Removes NBT tags based on the item to make it easier for comparison with other similar items.
	 * 
	 * @param item	item to remove NBT tags from
	 * @return		the item with appropriate NBT tags removed
	 */
	public static ItemStack sanitize(ItemStack item, int flags) {
		switch (item.getItem().getRegistryName().toString()) {
			case QUEEN_BEE:
			case PRINCESS_BEE:
				//if((flags & Tag.GEN.flag) == Tag.GEN.flag && item.getTagCompound().hasKey(Tag.GEN.name)) {
				if(item.getTagCompound().hasKey(Tag.GEN.name)) {	// Don't use any flags for now
					item.getTagCompound().removeTag(Tag.GEN.name);
					return item;
				}
			/* Do nothing with these for now
			case DRONE_BEE:
			case LARVAE:
			case SAPLING:
			case POLLEN:
			case BUTTERFLY:
			case SERUM:
			case CATERPILLAR:
			case COCOON:
			*/
			default: throw new IllegalArgumentException("Tried to sanitize \"" + item.getItem().getRegistryName().toString() +"\" for Forestry!");
		}
	}
}