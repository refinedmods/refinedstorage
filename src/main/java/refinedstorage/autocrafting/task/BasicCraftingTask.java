package refinedstorage.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.storagenet.NetworkMaster;
import refinedstorage.autocrafting.CraftingPattern;

import java.util.ArrayList;
import java.util.List;

public class BasicCraftingTask implements ICraftingTask {
    public static final int ID = 0;

    public static final String NBT_SATISFIED = "Satisfied";
    public static final String NBT_CHECKED = "Checked";
    public static final String NBT_CHILD_TASKS = "ChildTasks";
    public static final String NBT_TOOK = "Took";

    private CraftingPattern pattern;
    private boolean satisfied[];
    private boolean checked[];
    private boolean childTasks[];
    private List<ItemStack> itemsTook = new ArrayList<ItemStack>();
    private boolean updatedOnce;

    public BasicCraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getInputs().length];
        this.checked = new boolean[pattern.getInputs().length];
        this.childTasks = new boolean[pattern.getInputs().length];
    }

    public BasicCraftingTask(NBTTagCompound tag, CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = RefinedStorageUtils.readBooleanArray(tag, NBT_SATISFIED);
        this.checked = RefinedStorageUtils.readBooleanArray(tag, NBT_CHECKED);
        this.childTasks = RefinedStorageUtils.readBooleanArray(tag, NBT_CHILD_TASKS);

        NBTTagList tookList = tag.getTagList(NBT_TOOK, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tookList.tagCount(); ++i) {
            itemsTook.add(ItemStack.loadItemStackFromNBT(tookList.getCompoundTagAt(i)));
        }
    }

    public CraftingPattern getPattern() {
        return pattern;
    }

    public boolean update(NetworkMaster network) {
        this.updatedOnce = true;

        boolean done = true;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            checked[i] = true;

            ItemStack input = pattern.getInputs()[i];

            if (!satisfied[i]) {
                done = false;

                ItemStack took = network.take(input, 1);

                if (took != null) {
                    itemsTook.add(took);

                    satisfied[i] = true;
                } else if (!childTasks[i]) {
                    CraftingPattern pattern = network.getPatternWithBestScore(input);

                    if (pattern != null) {
                        network.addCraftingTask(network.createCraftingTask(pattern));

                        childTasks[i] = true;
                    }

                    break;
                } else {
                    break;
                }
            }
        }

        return done;
    }

    // @todo: handle no space
    @Override
    public void onDone(NetworkMaster network) {
        for (ItemStack output : pattern.getOutputs()) {
            network.push(output, output.stackSize, false);
        }

        if (pattern.getByproducts() != null) {
            for (ItemStack byproduct : pattern.getByproducts()) {
                network.push(byproduct, byproduct.stackSize, false);
            }
        }
    }

    // @todo: handle no space
    @Override
    public void onCancelled(NetworkMaster network) {
        for (ItemStack took : itemsTook) {
            network.push(took, took.stackSize, false);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagCompound patternTag = new NBTTagCompound();
        pattern.writeToNBT(patternTag);
        tag.setTag(CraftingPattern.NBT, patternTag);

        RefinedStorageUtils.writeBooleanArray(tag, NBT_SATISFIED, satisfied);
        RefinedStorageUtils.writeBooleanArray(tag, NBT_CHECKED, checked);
        RefinedStorageUtils.writeBooleanArray(tag, NBT_CHILD_TASKS, childTasks);

        NBTTagList tookList = new NBTTagList();

        for (ItemStack took : itemsTook) {
            tookList.appendTag(took.serializeNBT());
        }

        tag.setTag(NBT_TOOK, tookList);

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

            if (checked[i] && !satisfied[i] && !childTasks[i]) {
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

            if (!satisfied[i] && childTasks[i]) {
                if (!areItemsCrafting) {
                    builder.append("I=gui.refinedstorage:crafting_monitor.items_crafting\n");

                    areItemsCrafting = true;
                }

                builder.append("T=").append(input.getUnlocalizedName()).append(".name\n");
            }
        }

        return builder.toString();
    }
}
