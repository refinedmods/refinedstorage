package refinedstorage.apiimpl.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class BasicCraftingTask implements ICraftingTask {
    public static final int ID = 0;

    private static final String NBT_SATISFIED = "Satisfied";
    private static final String NBT_CHECKED = "Checked";
    private static final String NBT_CHILD_TASKS = "ChildTasks";
    private static final String NBT_TOOK = "Took";

    private ICraftingPattern pattern;
    private boolean satisfied[];
    private boolean checked[];
    private boolean childTasks[];
    private List<ItemStack> itemsTook = new ArrayList<ItemStack>();
    private boolean updatedOnce;

    public BasicCraftingTask(ICraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getInputs().length];
        this.checked = new boolean[pattern.getInputs().length];
        this.childTasks = new boolean[pattern.getInputs().length];
    }

    public BasicCraftingTask(NBTTagCompound tag, ICraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = RefinedStorageUtils.readBooleanArray(tag, NBT_SATISFIED);
        this.checked = RefinedStorageUtils.readBooleanArray(tag, NBT_CHECKED);
        this.childTasks = RefinedStorageUtils.readBooleanArray(tag, NBT_CHILD_TASKS);

        NBTTagList tookList = tag.getTagList(NBT_TOOK, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tookList.tagCount(); ++i) {
            itemsTook.add(ItemStack.loadItemStackFromNBT(tookList.getCompoundTagAt(i)));
        }
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean update(World world, INetworkMaster network) {
        this.updatedOnce = true;

        boolean done = true;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            checked[i] = true;

            ItemStack input = pattern.getInputs()[i];

            if (!satisfied[i]) {
                done = false;

                ItemStack took = NetworkUtils.extractItem(network, input, 1);

                if (took != null) {
                    itemsTook.add(took);

                    satisfied[i] = true;
                } else if (!childTasks[i]) {
                    ICraftingPattern pattern = NetworkUtils.getPattern(network, input);

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
    public void onDone(INetworkMaster network) {
        for (ItemStack output : pattern.getOutputs()) {
            network.insertItem(output, output.stackSize, false);
        }

        if (pattern.getByproducts() != null) {
            for (ItemStack byproduct : pattern.getByproducts()) {
                network.insertItem(byproduct, byproduct.stackSize, false);
            }
        }
    }

    // @todo: handle no space
    @Override
    public void onCancelled(INetworkMaster network) {
        for (ItemStack took : itemsTook) {
            network.insertItem(took, took.stackSize, false);
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
