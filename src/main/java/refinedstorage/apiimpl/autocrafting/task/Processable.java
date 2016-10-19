package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import refinedstorage.RSUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.api.util.IComparer;
import refinedstorage.api.util.IItemStackList;
import refinedstorage.apiimpl.API;

public class Processable implements IProcessable {
    private static final String NBT_SATISFIED = "Satisfied_%d";
    private static final String NBT_TO_INSERT = "ToInsert";

    private ICraftingPattern pattern;
    private IItemStackList toInsert = API.instance().createItemStackList();
    private boolean satisfied[];
    private boolean startedProcessing;

    public Processable(ICraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getOutputs().size()];

        for (ItemStack input : pattern.getInputs()) {
            if (input != null) {
                toInsert.add(input.copy());
            }
        }
    }

    public Processable(ICraftingPattern pattern, NBTTagCompound tag) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getOutputs().size()];

        for (int i = 0; i < satisfied.length; ++i) {
            String id = String.format(NBT_SATISFIED, i);

            if (tag.hasKey(id)) {
                this.satisfied[i] = tag.getBoolean(id);
            }
        }

        this.toInsert = RSUtils.readItemStackList(tag.getTagList(NBT_TO_INSERT, Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public IItemStackList getToInsert() {
        return toInsert;
    }

    @Override
    public boolean canStartProcessing(IItemStackList list) {
        list = list.copy(); // So we can edit the list
        for (ItemStack stack : toInsert.getStacks()) {
            ItemStack actualStack = list.get(stack, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0));
            if (actualStack == null || actualStack.stackSize == 0 || !list.remove(actualStack, true)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setStartedProcessing() {
        startedProcessing = true;
    }

    @Override
    public boolean hasStartedProcessing() {
        return startedProcessing;
    }

    @Override
    public boolean hasReceivedOutputs() {
        for (boolean item : satisfied) {
            if (!item) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean hasReceivedOutput(int i) {
        return satisfied[i];
    }

    @Override
    public boolean onReceiveOutput(ItemStack stack) {
        for (int i = 0; i < pattern.getOutputs().size(); ++i) {
            if (!satisfied[i]) {
                ItemStack item = pattern.getOutputs().get(i);

                if (API.instance().getComparer().isEqualNoQuantity(stack, item)) {
                    satisfied[i] = true;

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        for (int i = 0; i < satisfied.length; ++i) {
            tag.setBoolean(String.format(NBT_SATISFIED, i), satisfied[i]);
        }

        tag.setTag(NBT_TO_INSERT, RSUtils.serializeItemStackList(toInsert));

        return tag;
    }
}
