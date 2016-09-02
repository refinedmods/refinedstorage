package refinedstorage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.autocrafting.ICraftingPatternProvider;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.apiimpl.autocrafting.CraftingPattern;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemPattern extends ItemBase implements ICraftingPatternProvider {
    private static final String NBT_SLOT = "Slot_%d";

    public ItemPattern() {
        super("pattern");
    }

    @Override
    public void addInformation(ItemStack pattern, EntityPlayer player, List<String> tooltip, boolean advanced) {
       /* @todo CraftingPattern
            if (GuiScreen.isShiftKeyDown() || isProcessing(pattern)) {
                tooltip.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.inputs") + TextFormatting.RESET);

                combineItems(tooltip, true, getInputs(pattern));

                tooltip.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.outputs") + TextFormatting.RESET);
            }

            combineItems(tooltip, true, getOutputs(pattern));
        }*/
    }

    public static void setSlot(ItemStack pattern, int slot, ItemStack stack) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setTag(String.format(NBT_SLOT, slot), stack.serializeNBT());
    }

    public static ItemStack getSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_SLOT, slot);

        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(id)) {
            return null;
        }

        return ItemStack.loadItemStackFromNBT(pattern.getTagCompound().getCompoundTag(id));
    }

    public static void combineItems(List<String> tooltip, boolean displayAmount, ItemStack... stacks) {
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.length; ++i) {
            if (stacks[i] != null && !combinedIndices.contains(i)) {
                String data = stacks[i].getDisplayName();

                int amount = stacks[i].stackSize;

                for (int j = i + 1; j < stacks.length; ++j) {
                    if (CompareUtils.compareStack(stacks[i], stacks[j])) {
                        amount += stacks[j].stackSize;

                        combinedIndices.add(j);
                    }
                }

                data = (displayAmount ? (TextFormatting.WHITE + String.valueOf(amount) + " ") : "") + TextFormatting.GRAY + data;

                tooltip.add(data);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RefinedStorageItems.PATTERN));
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public ICraftingPattern create(World world, ItemStack stack, ICraftingPatternContainer container) {
        return new CraftingPattern(world, container, stack);
    }
}
