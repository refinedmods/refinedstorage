package com.raoulvdberge.refinedstorage.integration.forestry;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;

public final class IntegrationForestry {
    public enum Tag {
	    GENOME("Genome", 64), // Bees, Trees, Butterflies
	    MATE("Mate", 128), // Bees, Butterflies
	    GEN("GEN", 256), // Bees
	    HEALTH("Health", 512), // Bees, Butterflies
	    IS_ANALYZED("IsAnalyzed", 1024), // Bees, Trees, Butterflies
	    MAX_HEALTH("MaxH", 2048), // Bees, Butterflies
	    AGE("Age", 4096); // Butterflies

	    private String name;
	    private int flag;

	Tag(String name, int flag) {
	    this.name = name;
	    this.flag = flag;
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

    private static final String[] FORESTRY_NAMES = { QUEEN_BEE, PRINCESS_BEE, DRONE_BEE, LARVAE_BEE, SAPLING, POLLEN,
	    BUTTERFLY, SERUM, CATERPILLAR, COCOON };

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
	String itemName = item.getItem().getRegistryName().toString();
	    for (String forestryName : FORESTRY_NAMES) {
	        if (itemName.equals(forestryName)) {
		        return true;
	        }
	    }
	    return false;
    }

    /**
     * Removes NBT tags based on the item to make it easier for comparison with
     * other similar items.
     *
     * @param item	item to remove NBT tags from
     * @return		the item with appropriate NBT tags removed
     */
    public static ItemStack sanitize(ItemStack item, int flags) {
	    NBTTagCompound tagCompound = item.getTagCompound().copy();
	    ArrayList<Tag> tagsToRemove = new ArrayList<>();
	    switch (item.getItem().getRegistryName().toString()) {
	        case QUEEN_BEE:
	        case PRINCESS_BEE:
	            tagsToRemove.add(Tag.GEN);
	        case DRONE_BEE:
	        case LARVAE_BEE:
	            Collections.addAll(tagsToRemove, Tag.GENOME, Tag.MATE, Tag.HEALTH, Tag.IS_ANALYZED, Tag.MAX_HEALTH);
	            item.setTagCompound(removeTags(tagsToRemove, tagCompound, flags));
	            break;
	    case SAPLING:
	    case POLLEN:
	        Collections.addAll(tagsToRemove, Tag.GENOME, Tag.IS_ANALYZED);
	        item.setTagCompound(removeTags(tagsToRemove, tagCompound, flags));
	        break;
	    case BUTTERFLY:
	    case SERUM:
	    case CATERPILLAR:
	    case COCOON:
	        Collections.addAll(tagsToRemove, Tag.GENOME, Tag.MATE, Tag.HEALTH, Tag.IS_ANALYZED, Tag.MAX_HEALTH,
		    Tag.AGE);
	        item.setTagCompound(removeTags(tagsToRemove, tagCompound, flags));
	        break;
	    default:
	        throw new IllegalArgumentException(
		    "Tried to sanitize \"" + item.getItem().getRegistryName().toString() + "\" for Forestry!");
	    }
	    return item;
    }

    private static NBTTagCompound removeTags(ArrayList<Tag> tagsToRemove, NBTTagCompound compound, int flags) {
	    for (Tag tag : tagsToRemove) {
	        if ((flags & tag.flag) == tag.flag && compound.hasKey(tag.name)) {
		        compound.removeTag(tag.name);
	        }
	    }
	    return compound;
    }
}