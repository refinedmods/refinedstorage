package refinedstorage.apiimpl.autocrafting.task;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.storage.CompareUtils;

import java.util.List;

public class CraftingTaskProcessing extends CraftingTask {
    public static final String NBT_SATISFIED = "Satisfied";
    public static final String NBT_SATISFIED_INSERTION = "SatisfiedInsertion";
    public static final String NBT_CHECKED = "Checked";

    private boolean satisfied[];
    private boolean satisfiedInsertion[];
    private boolean checked[];

    public CraftingTaskProcessing(ICraftingPattern pattern) {
        super(pattern);

        this.satisfied = new boolean[pattern.getInputs().size()];
        this.satisfiedInsertion = new boolean[pattern.getOutputs().size()];
        this.checked = new boolean[pattern.getInputs().size()];
    }

    public void setSatisfied(boolean[] satisfied) {
        this.satisfied = satisfied;
    }

    public void setSatisfiedInsertion(boolean[] satisfiedInsertion) {
        this.satisfiedInsertion = satisfiedInsertion;
    }

    public void setChecked(boolean[] checked) {
        this.checked = checked;
    }

    @Override
    public boolean update(World world, INetworkMaster network) {
        for (int i = 0; i < pattern.getInputs().size(); ++i) {
            checked[i] = true;

            ItemStack input = pattern.getInputs().get(i);

            if (!satisfied[i]) {
                ItemStack received = NetworkUtils.extractItem(network, input, input.stackSize);

                if (received != null) {
                    satisfied[i] = true;

                    took.add(received);

                    network.updateCraftingTasks();
                } else {
                    tryCreateChild(network, i);
                }

                break;
            }
        }

        if (!isReadyToInsert()) {
            return false;
        }

        if (!took.isEmpty()) {
            ICraftingPatternContainer container = pattern.getContainer();

            ItemStack toInsert = took.get(0);

            if (ItemHandlerHelper.insertItem(container.getConnectedItems(), toInsert, true) == null) {
                ItemHandlerHelper.insertItem(container.getConnectedItems(), toInsert, false);

                took.remove(0);
            }
        }

        return isDone();
    }

    private boolean isDone() {
        for (boolean item : satisfiedInsertion) {
            if (!item) {
                return false;
            }
        }

        return true;
    }

    public boolean onInserted(ItemStack stack) {
        if (isDone()) {
            return false;
        }

        for (int i = 0; i < pattern.getOutputs().size(); ++i) {
            ItemStack output = pattern.getOutputs().get(i);

            if (!satisfiedInsertion[i]) {
                if (CompareUtils.compareStackNoQuantity(output, stack)) {
                    satisfiedInsertion[i] = true;

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        writeBooleanArray(tag, NBT_SATISFIED, satisfied);
        writeBooleanArray(tag, NBT_SATISFIED_INSERTION, satisfiedInsertion);
        writeBooleanArray(tag, NBT_CHECKED, checked);

        return tag;
    }

    private boolean isReadyToInsert() {
        for (boolean item : satisfied) {
            if (!item) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getStatus() {
        StringBuilder builder = new StringBuilder();

        boolean missingItems = false;

        for (int i = 0; i < pattern.getInputs().size(); ++i) {
            ItemStack input = pattern.getInputs().get(i);

            if (!satisfied[i] && !childrenCreated[i]) {
                if (!missingItems) {
                    builder.append("I=gui.refinedstorage:crafting_monitor.missing_items\n");

                    missingItems = true;
                }

                builder.append("T=").append(input.getUnlocalizedName()).append(".name\n");
            }
        }

        boolean itemsCrafting = false;

        for (int i = 0; i < pattern.getInputs().size(); ++i) {
            ItemStack input = pattern.getInputs().get(i);

            if (!satisfied[i] && childrenCreated[i]) {
                if (!itemsCrafting) {
                    builder.append("I=gui.refinedstorage:crafting_monitor.items_crafting\n");

                    itemsCrafting = true;
                }

                builder.append("T=").append(input.getUnlocalizedName()).append(".name\n");
            }
        }

        if (isReadyToInsert()) {
            builder.append("I=gui.refinedstorage:crafting_monitor.items_processing\n");

            for (int i = 0; i < pattern.getInputs().size(); ++i) {
                builder.append("T=").append(pattern.getInputs().get(i).getUnlocalizedName()).append(".name\n");
            }
        }

        return builder.toString();
    }

    @Override
    public int getProgress() {
        int satisfiedAmount = 0;

        for (boolean item : satisfied) {
            if (item) {
                satisfiedAmount++;
            }
        }

        for (boolean item : satisfiedInsertion) {
            if (item) {
                satisfiedAmount++;
            }
        }

        return (int) ((float) satisfiedAmount / (float) (satisfied.length + satisfiedInsertion.length) * 100F);
    }

    @Override
    public List<ItemStack> getMissingItems() {
        List<ItemStack> missingItemStacks = Lists.newArrayList();
        for (int i = 0; i < pattern.getInputs().size(); ++i) {
            ItemStack input = pattern.getInputs().get(i);
            if (!satisfied[i] && childrenCreated[i] && checked[i]) {
                missingItemStacks.add(input);
            }
        }
        return missingItemStacks;
    }
}
