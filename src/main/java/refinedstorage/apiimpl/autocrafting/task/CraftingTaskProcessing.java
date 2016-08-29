package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;

public class CraftingTaskProcessing extends CraftingTask {
    public static final String NBT_SATISFIED = "Satisfied";
    public static final String NBT_CHECKED = "Checked";

    private boolean satisfied[];
    private boolean checked[];

    public CraftingTaskProcessing(ICraftingPattern pattern) {
        super(pattern);

        this.satisfied = new boolean[pattern.getInputs().length];
        this.checked = new boolean[pattern.getInputs().length];
    }

    public void setSatisfied(boolean[] satisfied) {
        this.satisfied = satisfied;
    }

    public void setChecked(boolean[] checked) {
        this.checked = checked;
    }

    @Override
    public boolean update(World world, INetworkMaster network) {
        for (int i = 0; i < pattern.getInputs().length; ++i) {
            checked[i] = true;

            ItemStack input = pattern.getInputs()[i];

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

        ICraftingPatternContainer container = pattern.getContainer(world);

        if (!took.isEmpty()) {
            ItemStack toInsert = took.get(0);

            if (ItemHandlerHelper.insertItem(container.getConnectedItems(), toInsert, true) == null) {
                ItemHandlerHelper.insertItem(container.getConnectedItems(), toInsert, false);

                took.remove(0);
            }
        }

        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        writeBooleanArray(tag, NBT_SATISFIED, satisfied);
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

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

            if (!satisfied[i] && !childrenCreated[i]) {
                if (!missingItems) {
                    builder.append("I=gui.refinedstorage:crafting_monitor.missing_items\n");

                    missingItems = true;
                }

                builder.append("T=").append(input.getUnlocalizedName()).append(".name\n");
            }
        }

        boolean itemsCrafting = false;

        for (int i = 0; i < pattern.getInputs().length; ++i) {
            ItemStack input = pattern.getInputs()[i];

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

            for (int i = 0; i < pattern.getInputs().length; ++i) {
                builder.append("T=").append(pattern.getInputs()[i].getUnlocalizedName()).append(".name\n");
            }
        }

        return builder.toString();
    }
}
