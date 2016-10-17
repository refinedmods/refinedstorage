package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.apiimpl.API;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Processable implements IProcessable {
    private static final String NBT_SATISFIED = "Satisfied_%d";
    private static final String NBT_TO_INSERT = "ToInsert";

    private ICraftingPattern pattern;
    private Deque<ItemStack> toInsert = new ArrayDeque<>();
    private boolean satisfied[];

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

        NBTTagList toInsertList = tag.getTagList(NBT_TO_INSERT, Constants.NBT.TAG_COMPOUND);

        List<ItemStack> toInsert = new ArrayList<>();

        for (int i = 0; i < toInsertList.tagCount(); ++i) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(toInsertList.getCompoundTagAt(i));

            if (stack != null) {
                toInsert.add(stack);
            }
        }

        this.toInsert = new ArrayDeque<>(toInsert);
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public Deque<ItemStack> getToInsert() {
        return toInsert;
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

        NBTTagList toInsertList = new NBTTagList();

        for (ItemStack stack : new ArrayList<>(toInsert)) {
            toInsertList.appendTag(stack.serializeNBT());
        }

        tag.setTag(NBT_TO_INSERT, toInsertList);

        return tag;
    }
}
