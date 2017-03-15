package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Comparer implements IComparer {
    @Override
    public boolean isEqual(ItemStack left, ItemStack right, int flags) {
        if (left == null && right == null) {
            return true;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return false;
        }

        if ((flags & COMPARE_OREDICT) == COMPARE_OREDICT) {
            if (isEqualOredict(left, right)) {
                return true;
            }
        }

        if (left.getItem() != right.getItem()) {
            return false;
        }

        if ((flags & COMPARE_DAMAGE) == COMPARE_DAMAGE && left.getItemDamage() != OreDictionary.WILDCARD_VALUE && right.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
            if (left.getItemDamage() != right.getItemDamage()) {
                return false;
            }
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT) {
            if ((flags & COMPARE_STRIP_NBT) == COMPARE_STRIP_NBT) {
                left = stripTags(left.copy());
                right = stripTags(right.copy());
            }
            if (!isEqualNBT(left, right)) {
                return false;
            }
        }

        if ((flags & COMPARE_QUANTITY) == COMPARE_QUANTITY) {
            if (left.stackSize != right.stackSize) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEqual(FluidStack left, FluidStack right, int flags) {
        if (left == null && right == null) {
            return true;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return false;
        }

        if (left.getFluid() != right.getFluid()) {
            return false;
        }

        if ((flags & COMPARE_QUANTITY) == COMPARE_QUANTITY) {
            if (left.amount != right.amount) {
                return false;
            }
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT) {
            if (left.tag != null && !left.tag.equals(right.tag)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEqualNBT(ItemStack left, ItemStack right) {
        if (!ItemStack.areItemStackTagsEqual(left, right)) {
            if (left.hasTagCompound() && !right.hasTagCompound() && left.getTagCompound().hasNoTags()) {
                return true;
            } else if (!left.hasTagCompound() && right.hasTagCompound() && right.getTagCompound().hasNoTags()) {
                return true;
            }

            return false;
        }

        return true;
    }

    private Map<Integer, Boolean> oredictCache = new HashMap<>();

    @Override
    public boolean isEqualOredict(ItemStack left, ItemStack right) {
        if (left == null && right == null) {
            return true;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return false;
        }

        // We do not care about the NBT tag since the oredict doesn't care either, and generating a NBT hashcode is slow.
        int code = API.instance().getItemStackHashCode(left, false);
        code = 31 * code + API.instance().getItemStackHashCode(right, false);

        if (oredictCache.containsKey(code)) {
            return oredictCache.get(code);
        }

        int[] leftIds = OreDictionary.getOreIDs(left);
        int[] rightIds = OreDictionary.getOreIDs(right);

        for (int i : rightIds) {
            if (ArrayUtils.contains(leftIds, i)) {
                oredictCache.put(code, true);

                return true;
            }
        }

        oredictCache.put(code, false);

        return false;
    }

    public static ItemStack stripTags(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            switch (stack.getItem().getRegistryName().getResourceDomain()) {
                case "mekanism":
                case "mekanismgenerators":
                case "mekanismtools":
                    stack.getTagCompound().removeTag("mekData");
                    break;
                case "enderio":
                    // Soul vials
                    stack.getTagCompound().removeTag("entity");
                    stack.getTagCompound().removeTag("isStub");
                    // Capacitors
                    stack.getTagCompound().removeTag("Energy");
                    // Painted
                    stack.getTagCompound().removeTag("paintSource__null");
                    stack.getTagCompound().removeTag("paintSource");
                    // Sided config
                    stack.getTagCompound().removeTag("faceModes__null");
                    stack.getTagCompound().removeTag("faceModes");
                    // Tank
                    stack.getTagCompound().removeTag("tank");
                    stack.getTagCompound().removeTag("voidMode");
                    stack.getTagCompound().removeTag("inventory");
                    // Name
                    stack.getTagCompound().removeTag("display");
                    stack.getTagCompound().removeTag("eio.abstractMachine");
                    break;
                case "simplyjetpacks":
                    stack.getTagCompound().removeTag("sjData");
                    stack.getTagCompound().removeTag("PackOn");
                    break;
                case "storagedrawers":
                    stack.getTagCompound().removeTag("material");
                    break;
                case "immersiveengineering":
                    stack.getTagCompound().removeTag("hammerDmg");
                    stack.getTagCompound().removeTag("cutterDmg");
                    break;
                case "fluxnetworks":
                    stack.getTagCompound().removeTag("dropped");
                    stack.getTagCompound().removeTag("energy");
                    break;
                case "draconicevolution":
                    stack.getTagCompound().removeTag("Energy");
                    stack.getTagCompound().removeTag("DEUpgrades");
                    Set<String> profiles = stack.getTagCompound().getKeySet().stream().filter(key -> key.startsWith("Profile")).collect(Collectors.toSet());
                    for (String profile : profiles) {
                        stack.getTagCompound().removeTag(profile);
                    }
                    break;
                case "minecraft":
                    stack.getTagCompound().removeTag("RepairCost");
                    break;
            }
        }

        return stack;
    }
}
