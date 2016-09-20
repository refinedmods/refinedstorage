package refinedstorage.item;

import com.google.common.collect.Iterables;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.autocrafting.ICraftingPatternProvider;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.apiimpl.autocrafting.CraftingPattern;

import javax.annotation.Nonnull;
import java.util.*;

public class ItemPattern extends ItemBase implements ICraftingPatternProvider {
    /**
     * A cache that maps a stack to a crafting pattern.
     * Only used client side for rendering and tooltips, to avoid crafting pattern allocations and crafting pattern output calculation (which is expensive).
     */
    private static Map<ItemStack, CraftingPattern> PATTERN_CACHE = new HashMap<>();

    private static final String NBT_SLOT = "Slot_%d";
    private static final String NBT_OUTPUTS = "Outputs";

    public ItemPattern() {
        super("pattern");
    }

    public static CraftingPattern getPatternFromCache(World world, ItemStack stack) {
        if (!PATTERN_CACHE.containsKey(stack)) {
            PATTERN_CACHE.put(stack, new CraftingPattern(world, null, stack));
        }

        return PATTERN_CACHE.get(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (!stack.hasTagCompound()) {
            return;
        }

        ICraftingPattern pattern = getPatternFromCache(player.worldObj, stack);

        if (pattern.isValid()) {
            if (GuiScreen.isShiftKeyDown() || isProcessing(stack)) {
                tooltip.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.inputs") + TextFormatting.RESET);

                combineItems(tooltip, true, Iterables.toArray(pattern.getInputs(), ItemStack.class));

                tooltip.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.outputs") + TextFormatting.RESET);
            }

            combineItems(tooltip, true, Iterables.toArray(pattern.getOutputs(), ItemStack.class));
        } else {
            tooltip.add(TextFormatting.RED + I18n.format("misc.refinedstorage:pattern.invalid") + TextFormatting.RESET);

            // Display a helpful message stating the outputs if this is a legacy pattern
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Inputs") && stack.getTagCompound().hasKey("Outputs")) {
                tooltip.add(TextFormatting.WHITE + "This pattern is a legacy pattern made before RS 1.0, please re-make!" + TextFormatting.RESET);

                tooltip.add("This pattern used to output:");

                NBTTagList outputsTag = stack.getTagCompound().getTagList("Outputs", Constants.NBT.TAG_COMPOUND);

                ItemStack[] outputs = new ItemStack[outputsTag.tagCount()];

                for (int i = 0; i < outputsTag.tagCount(); ++i) {
                    outputs[i] = ItemStack.loadItemStackFromNBT(outputsTag.getCompoundTagAt(i));
                }

                combineItems(tooltip, true, outputs);

                if (stack.getTagCompound().hasKey("Processing") && stack.getTagCompound().getBoolean("Processing")) {
                    tooltip.add(TextFormatting.GREEN + "This pattern was a processing pattern!" + TextFormatting.RESET);
                }
            }
        }
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

    public static void addOutput(ItemStack pattern, ItemStack output) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        NBTTagList outputs;
        if (!pattern.getTagCompound().hasKey(NBT_OUTPUTS)) {
            outputs = new NBTTagList();
        } else {
            outputs = pattern.getTagCompound().getTagList(NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);
        }

        outputs.appendTag(output.serializeNBT());

        pattern.getTagCompound().setTag(NBT_OUTPUTS, outputs);
    }

    public static List<ItemStack> getOutputs(ItemStack pattern) {
        if (!isProcessing(pattern)) {
            return null;
        }

        ArrayList<ItemStack> outputs = new ArrayList<>();

        NBTTagList outputsTag = pattern.getTagCompound().getTagList(NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < outputsTag.tagCount(); ++i) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(outputsTag.getCompoundTagAt(i));

            if (stack != null) {
                outputs.add(stack);
            }
        }

        return outputs;
    }

    public static boolean isProcessing(ItemStack pattern) {
        return pattern.hasTagCompound() && pattern.getTagCompound().hasKey(NBT_OUTPUTS);
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
    @Nonnull
    public ICraftingPattern create(World world, ItemStack stack, ICraftingPatternContainer container) {
        return new CraftingPattern(world, container, stack);
    }
}
