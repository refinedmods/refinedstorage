package refinedstorage.apiimpl.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;

public class ProcessingCraftingTask implements ICraftingTask {
    public static final int ID = 1;

    public static final String NBT_INSERTED = "Inserted";
    public static final String NBT_CHILD_TASKS = "ChildTasks";
    public static final String NBT_SATISFIED = "Satisfied";

    private ICraftingPattern pattern;
    private boolean inserted[];
    private boolean childTasks[];
    private boolean satisfied[];
    private boolean updatedOnce;

    public ProcessingCraftingTask(ICraftingPattern pattern) {
        this.pattern = pattern;
        this.inserted = new boolean[pattern.getInputs().length];
        this.childTasks = new boolean[pattern.getInputs().length];
        this.satisfied = new boolean[pattern.getOutputs().length];
    }

    public ProcessingCraftingTask(NBTTagCompound tag, ICraftingPattern pattern) {
        this.pattern = pattern;
        this.inserted = RefinedStorageUtils.readBooleanArray(tag, NBT_INSERTED);
        this.childTasks = RefinedStorageUtils.readBooleanArray(tag, NBT_CHILD_TASKS);
        this.satisfied = RefinedStorageUtils.readBooleanArray(tag, NBT_SATISFIED);
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean update(INetworkMaster network) {
        this.updatedOnce = true;

        ICraftingPatternContainer container = pattern.getContainer(network.getWorld());

        if (container.getConnectedItems() != null) {
            for (int i = 0; i < inserted.length; ++i) {
                if (!inserted[i]) {
                    ItemStack input = pattern.getInputs()[i];
                    ItemStack took = network.take(input, 1);

                    if (took != null) {
                        if (ItemHandlerHelper.insertItem(container.getConnectedItems(), took, true) == null) {
                            ItemHandlerHelper.insertItem(container.getConnectedItems(), took, false);

                            inserted[i] = true;
                        } else {
                            network.push(took, took.stackSize, false);
                        }
                    } else if (!childTasks[i]) {
                        ICraftingPattern pattern = network.getPatternWithBestScore(input);

                        if (pattern != null) {
                            childTasks[i] = true;

                            network.addCraftingTask(network.createCraftingTask(pattern));

                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        } else {
            return true;
        }

        for (int i = 0; i < satisfied.length; ++i) {
            if (!satisfied[i]) {
                return false;
            }
        }

        return true;
    }

    public void onPushed(ItemStack inserted) {
        for (int i = 0; i < pattern.getOutputs().length; ++i) {
            if (!satisfied[i] && RefinedStorageUtils.compareStackNoQuantity(inserted, pattern.getOutputs()[i])) {
                satisfied[i] = true;

                return;
            }
        }
    }

    @Override
    public void onDone(INetworkMaster network) {
        // NO OP
    }

    @Override
    public void onCancelled(INetworkMaster network) {
        // NO OP
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagCompound patternTag = new NBTTagCompound();
        pattern.writeToNBT(patternTag);
        tag.setTag(CraftingPattern.NBT, patternTag);

        RefinedStorageUtils.writeBooleanArray(tag, NBT_INSERTED, inserted);
        RefinedStorageUtils.writeBooleanArray(tag, NBT_CHILD_TASKS, childTasks);
        RefinedStorageUtils.writeBooleanArray(tag, NBT_SATISFIED, satisfied);

        tag.setInteger("Type", ID);
    }

    @Override
    public String getInfo() {
        if (!updatedOnce) {
            return "T=gui.refinedstorage:crafting_monitor.not_started_yet";
        }

        StringBuilder builder = new StringBuilder();

        boolean hasMissingItems = false;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (!inserted[i] && !childTasks[i]) {
                if (!hasMissingItems) {
                    builder.append("I=gui.refinedstorage:crafting_monitor.missing_items\n");

                    hasMissingItems = true;
                }

                builder.append("T=").append(input.getUnlocalizedName()).append(".name\n");
            }
        }

        boolean areItemsCrafting = false;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (!inserted[i] && childTasks[i]) {
                if (!areItemsCrafting) {
                    builder.append("I=gui.refinedstorage:crafting_monitor.items_crafting\n");

                    areItemsCrafting = true;
                }

                builder.append("T=").append(input.getUnlocalizedName()).append(".name\n");
            }
        }

        boolean areItemsProcessing = false;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (inserted[i]) {
                if (!areItemsProcessing) {
                    builder.append("I=gui.refinedstorage:crafting_monitor.items_processing\n");

                    areItemsProcessing = true;
                }

                builder.append("T=").append(input.getUnlocalizedName()).append(".name\n");
            }
        }

        return builder.toString();
    }
}
