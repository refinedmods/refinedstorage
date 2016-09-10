package refinedstorage.api.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.tile.TileController;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A default implementation for crafting tasks.
 */
public abstract class CraftingTask implements ICraftingTask {
    public static final String NBT_CHILDREN_CREATED = "ChildrenCreated";
    public static final String NBT_SATISFIED = "Satisfied";
    public static final String NBT_CHECKED = "Checked";
    public static final String NBT_TOOK = "Took";
    private static final String NBT_CHILD = "Child";

    protected ICraftingPattern pattern;
    protected ICraftingTask child;

    protected List<ItemStack> took = new ArrayList<>();

    protected boolean childrenCreated[];
    protected boolean satisfied[];
    protected boolean checked[];

    public CraftingTask(ICraftingPattern pattern) {
        this.pattern = pattern;
        this.childrenCreated = new boolean[pattern.getInputs().size()];
        this.satisfied = new boolean[pattern.getInputs().size()];
        this.checked = new boolean[pattern.getInputs().size()];
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    public void setTook(List<ItemStack> took) {
        this.took = took;
    }

    public List<ItemStack> getTook() {
        return took;
    }

    public boolean[] getChildrenCreated() {
        return childrenCreated;
    }

    public void setChildrenCreated(boolean[] childrenCreated) {
        this.childrenCreated = childrenCreated;
    }

    public boolean[] getSatisfied() {
        return satisfied;
    }

    public void setSatisfied(boolean[] satisfied) {
        this.satisfied = satisfied;
    }

    public boolean[] getChecked() {
        return checked;
    }

    public void setChecked(boolean[] checked) {
        this.checked = checked;
    }

    protected void tryCreateChild(INetworkMaster network, int i) {
        if (!childrenCreated[i]) {
            ICraftingPattern pattern = NetworkUtils.getPattern(network, this.pattern.getInputs().get(i));

            if (pattern != null) {
                child = NetworkUtils.createCraftingTask(network, pattern);

                childrenCreated[i] = true;

                network.updateCraftingTasks();
            }
        }
    }

    @Override
    @Nullable
    public ICraftingTask getChild() {
        return child;
    }

    @Override
    public void setChild(@Nullable ICraftingTask child) {
        this.child = child;
    }

    @Override
    public void onCancelled(INetworkMaster network) {
        for (ItemStack stack : took) {
            // @TODO: Handle remainder
            network.insertItem(stack, stack.stackSize, false);
        }

        if (child != null) {
            child.onCancelled(network);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (child != null) {
            tag.setTag(NBT_CHILD, child.writeToNBT(new NBTTagCompound()));
        }

        writeBooleanArray(tag, NBT_CHILDREN_CREATED, childrenCreated);
        writeBooleanArray(tag, NBT_SATISFIED, satisfied);
        writeBooleanArray(tag, NBT_CHECKED, checked);

        NBTTagList took = new NBTTagList();

        for (ItemStack stack : this.took) {
            took.appendTag(stack.serializeNBT());
        }

        tag.setTag(NBT_TOOK, took);

        return tag;
    }

    public void readChildNBT(World world, NBTTagCompound tag) {
        if (tag.hasKey(NBT_CHILD)) {
            child = TileController.readCraftingTask(world, tag.getCompoundTag(NBT_CHILD));
        }
    }

    public static void writeBooleanArray(NBTTagCompound tag, String name, boolean[] array) {
        int[] intArray = new int[array.length];

        for (int i = 0; i < intArray.length; ++i) {
            intArray[i] = array[i] ? 1 : 0;
        }

        tag.setTag(name, new NBTTagIntArray(intArray));
    }

    public static boolean[] readBooleanArray(NBTTagCompound tag, String name) {
        int[] intArray = tag.getIntArray(name);

        boolean array[] = new boolean[intArray.length];

        for (int i = 0; i < intArray.length; ++i) {
            array[i] = intArray[i] == 1 ? true : false;
        }

        return array;
    }
}
