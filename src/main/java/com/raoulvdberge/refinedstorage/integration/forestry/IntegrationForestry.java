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
	private static final String DRONE_BEE = "forestry:bee_drone_ge";
	private static final String LARVAE_BEE = "forestry:bee_larvae_ge";
	
	private static final String SAPLING = "forestry:sapling";
	private static final String POLLEN = "forestry:pollen_fertile";
	
	private static final String BUTTERFLY = "forestry:butterfly_ge";
	private static final String SERUM = "forestry:serum_ge";
	private static final String CATERPILLAR = "forestry:caterpillar_ge";
	private static final String COCOON = "forestry:cocoon_ge";
	
	//public static final int allFlags = (64+128+256+512+1024+2048+4096);
	public static final int tempFlags = (256+1024);
	
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
		case DRONE_BEE:
		case LARVAE_BEE:
		case SAPLING:
		case POLLEN:
		case BUTTERFLY:
		case SERUM:
		case CATERPILLAR:
		case COCOON:
			return true;
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
				if((flags & Tag.GEN.flag) == Tag.GEN.flag && item.getTagCompound().hasKey(Tag.GEN.name)) {
					item.getTagCompound().removeTag(Tag.GEN.name);
					return item;
				}
			case DRONE_BEE:
			case LARVAE_BEE:
				if((flags & Tag.GENOME.flag) == Tag.GENOME.flag && item.getTagCompound().hasKey(Tag.GENOME.name)) {
					item.getTagCompound().removeTag(Tag.GENOME.name);
					return item;
				}
				if((flags & Tag.MATE.flag) == Tag.MATE.flag && item.getTagCompound().hasKey(Tag.MATE.name)) {
					item.getTagCompound().removeTag(Tag.MATE.name);
					return item;
				}
				if((flags & Tag.HEALTH.flag) == Tag.HEALTH.flag && item.getTagCompound().hasKey(Tag.HEALTH.name)) {
					item.getTagCompound().removeTag(Tag.HEALTH.name);
					return item;
				}
				if((flags & Tag.IS_ANLAYZED.flag) == Tag.IS_ANLAYZED.flag && item.getTagCompound().hasKey(Tag.IS_ANLAYZED.name)) {
					item.getTagCompound().removeTag(Tag.IS_ANLAYZED.name);
					return item;
				}
				if((flags & Tag.MAX_HEALTH.flag) == Tag.MAX_HEALTH.flag && item.getTagCompound().hasKey(Tag.MAX_HEALTH.name)) {
					item.getTagCompound().removeTag(Tag.MAX_HEALTH.name);
					return item;
				}
				break;
			case SAPLING:
			case POLLEN:
				if((flags & Tag.GENOME.flag) == Tag.GENOME.flag && item.getTagCompound().hasKey(Tag.GENOME.name)) {
					item.getTagCompound().removeTag(Tag.GENOME.name);
					return item;
				}
				if((flags & Tag.IS_ANLAYZED.flag) == Tag.IS_ANLAYZED.flag && item.getTagCompound().hasKey(Tag.IS_ANLAYZED.name)) {
					item.getTagCompound().removeTag(Tag.IS_ANLAYZED.name);
					return item;
				}
				break;
			case BUTTERFLY:
			case SERUM:
			case CATERPILLAR:
			case COCOON:
				if((flags & Tag.GENOME.flag) == Tag.GENOME.flag && item.getTagCompound().hasKey(Tag.GENOME.name)) {
					item.getTagCompound().removeTag(Tag.GENOME.name);
					return item;
				}
				if((flags & Tag.MATE.flag) == Tag.MATE.flag && item.getTagCompound().hasKey(Tag.MATE.name)) {
					item.getTagCompound().removeTag(Tag.MATE.name);
					return item;
				}
				if((flags & Tag.HEALTH.flag) == Tag.HEALTH.flag && item.getTagCompound().hasKey(Tag.HEALTH.name)) {
					item.getTagCompound().removeTag(Tag.HEALTH.name);
					return item;
				}
				if((flags & Tag.IS_ANLAYZED.flag) == Tag.IS_ANLAYZED.flag && item.getTagCompound().hasKey(Tag.IS_ANLAYZED.name)) {
					item.getTagCompound().removeTag(Tag.IS_ANLAYZED.name);
					return item;
				}
				if((flags & Tag.MAX_HEALTH.flag) == Tag.MAX_HEALTH.flag && item.getTagCompound().hasKey(Tag.MAX_HEALTH.name)) {
					item.getTagCompound().removeTag(Tag.MAX_HEALTH.name);
					return item;
				}
				if((flags & Tag.AGE.flag) == Tag.AGE.flag && item.getTagCompound().hasKey(Tag.AGE.name)) {
					item.getTagCompound().removeTag(Tag.AGE.name);
					return item;
				}
				break;
			default: throw new IllegalArgumentException("Tried to sanitize \"" + item.getItem().getRegistryName().toString() +"\" for Forestry!");
		}
		return item; // Do nothing
	}
}