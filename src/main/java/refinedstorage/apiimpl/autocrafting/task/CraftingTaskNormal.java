package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.CraftingTask;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;

public class CraftingTaskNormal extends CraftingTask {
    public static final String NBT_TOOK_SLOT = "TookSlot_%d";

    private ItemStack[] tookSlots = new ItemStack[9];

    public CraftingTaskNormal(ICraftingPattern pattern) {
        super(pattern);
    }

    public ItemStack[] getTookSlots() {
        return tookSlots;
    }

    @Override
    public boolean update(World world, INetworkMaster network) {
        for (int i = 0; i < pattern.getInputs().size(); ++i) {
            checked[i] = true;

            ItemStack input = pattern.getInputs().get(i);

            if (!satisfied[i]) {
                ItemStack received = FluidUtils.extractItemOrIfBucketLookInFluids(network, input, input.stackSize, pattern.isOredicted());

                if (received != null) {
                    satisfied[i] = true;

                    took.add(received);
                    tookSlots[i] = received;
                } else {
                    tryCreateChild(network, i);
                }

                break;
            }
        }

        for (boolean item : satisfied) {
            if (!item) {
                return false;
            }
        }

        for (ItemStack output : pattern.getOutputsBasedOnTook(tookSlots)) {
            // @TODO: Handle remainder
            network.insertItem(output, output.stackSize, false);
        }

        return true;
    }

    @Override
    public String getStatus() {
        StringBuilder builder = new StringBuilder();

        boolean missingItems = false;

        for (int i = 0; i < pattern.getInputs().size(); ++i) {
            ItemStack input = pattern.getInputs().get(i);

            if (!satisfied[i] && !childrenCreated[i] && checked[i]) {
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

            if (!satisfied[i] && childrenCreated[i] && checked[i]) {
                if (!itemsCrafting) {
                    builder.append("I=gui.refinedstorage:crafting_monitor.items_crafting\n");

                    itemsCrafting = true;
                }

                builder.append("T=").append(input.getUnlocalizedName()).append(".name\n");
            }
        }

        return builder.toString();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        for (int i = 0; i < 9; ++i) {
            if (tookSlots[i] != null) {
                tag.setTag(String.format(NBT_TOOK_SLOT, i), tookSlots[i].serializeNBT());
            }
        }

        return tag;
    }

    @Override
    public int getProgress() {
        int satisfiedAmount = 0;

        for (boolean item : satisfied) {
            if (item) {
                satisfiedAmount++;
            }
        }

        return (int) ((float) satisfiedAmount / (float) satisfied.length * 100F);
    }
}
