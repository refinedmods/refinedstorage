package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.extractor;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingTask;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CraftingExtractor {
    private INetwork network;
    private List<CraftingExtractorStack> stacks;
    private boolean processing;

    public CraftingExtractor(INetwork network, List<CraftingExtractorStack> stacks, boolean processing) {
        this.network = network;
        this.stacks = stacks;
        this.processing = processing;
    }

    public CraftingExtractor(INetwork network, NBTTagList tag, boolean processing) throws CraftingTaskReadException {
        this.network = network;
        this.processing = processing;

        this.stacks = new ArrayList<>();

        for (int i = 0; i < tag.tagCount(); ++i) {
            this.stacks.add(new CraftingExtractorStack(tag.getCompoundTagAt(i)));
        }
    }

    public List<CraftingExtractorStack> getStacks() {
        return stacks;
    }

    public void updateStatus(@Nullable IItemHandler processingInventory, @Nullable IFluidHandler processingFluidInventory) {
        boolean updated = false;

        for (CraftingExtractorStack stack : stacks) {
            if (stack.getStatus() != CraftingExtractorStatus.EXTRACTED) {
                CraftingExtractorStatus previousStatus = stack.getStatus();

                if (stack.getItem() != null) {
                    ItemStack item = stack.getItem();

                    ItemStack inNetwork = network.extractItem(item, item.getCount(), CraftingTask.getFlags(item), Action.SIMULATE);

                    if (inNetwork == null || inNetwork.getCount() < item.getCount()) {
                        stack.setStatus(CraftingExtractorStatus.MISSING);
                    } else {
                        stack.setStatus(CraftingExtractorStatus.AVAILABLE);

                        if (processing) {
                            if (processingInventory == null) {
                                stack.setStatus(CraftingExtractorStatus.MACHINE_NONE);
                            } else if (!ItemHandlerHelper.insertItem(processingInventory, item, true).isEmpty()) {
                                stack.setStatus(CraftingExtractorStatus.MACHINE_DOES_NOT_ACCEPT);
                            }
                        }
                    }
                } else {
                    FluidStack fluid = stack.getFluid();

                    FluidStack inNetwork = network.extractFluid(fluid, fluid.amount, IComparer.COMPARE_NBT, Action.SIMULATE);

                    if (inNetwork == null || inNetwork.amount < fluid.amount) {
                        stack.setStatus(CraftingExtractorStatus.MISSING);
                    } else {
                        stack.setStatus(CraftingExtractorStatus.AVAILABLE);

                        if (processingFluidInventory == null) {
                            stack.setStatus(CraftingExtractorStatus.MACHINE_NONE);
                        } else if (processingFluidInventory.fill(fluid, false) != fluid.amount) {
                            stack.setStatus(CraftingExtractorStatus.MACHINE_DOES_NOT_ACCEPT);
                        }
                    }
                }

                if (previousStatus != stack.getStatus()) {
                    updated = true;
                }
            }
        }

        if (updated) {
            network.getCraftingManager().onTaskChanged();
        }
    }

    public boolean isAllAvailable() {
        return !stacks.isEmpty() && stacks.stream().allMatch(s -> s.getStatus() == CraftingExtractorStatus.AVAILABLE || s.getStatus() == CraftingExtractorStatus.EXTRACTED);
    }

    public boolean isAllExtracted() {
        return !stacks.isEmpty() && stacks.stream().allMatch(s -> s.getStatus() == CraftingExtractorStatus.EXTRACTED);
    }

    public void extractOne(@Nullable IItemHandler processingInventory, @Nullable IFluidHandler processingFluidInventory) {
        boolean changed = false;

        for (CraftingExtractorStack stack : stacks) {
            if (stack.getStatus() == CraftingExtractorStatus.AVAILABLE) {
                if (stack.getItem() != null) {
                    ItemStack item = stack.getItem();

                    ItemStack extracted = network.extractItem(item, item.getCount(), CraftingTask.getFlags(item), Action.PERFORM);
                    if (extracted == null) {
                        throw new IllegalStateException("Did not extract anything while available");
                    }

                    if (processing) {
                        if (processingInventory == null) {
                            throw new IllegalStateException("Processing inventory is null");
                        }

                        ItemStack remainder = ItemHandlerHelper.insertItem(processingInventory, extracted, false);
                        if (!remainder.isEmpty()) {
                            throw new IllegalStateException("The processing inventory gave back a remainder while it previously stated it could handle all");
                        }
                    }

                    stack.setStatus(CraftingExtractorStatus.EXTRACTED);

                    changed = true;
                } else {
                    FluidStack fluid = stack.getFluid();

                    FluidStack extracted = network.extractFluid(fluid, fluid.amount, IComparer.COMPARE_NBT, Action.PERFORM);
                    if (extracted == null) {
                        throw new IllegalStateException("Did not extract any fluids while available");
                    }

                    if (processingFluidInventory == null) {
                        throw new IllegalStateException("Processing fluid inventory is null");
                    }

                    int filled = processingFluidInventory.fill(fluid, true);
                    if (filled != fluid.amount) {
                        throw new IllegalStateException("The processing fluid inventory gave back a remainder while it previously stated it could handle all");
                    }

                    stack.setStatus(CraftingExtractorStatus.EXTRACTED);

                    changed = true;
                }

                // For processing patterns we want to insert all items at once to avoid conflicts with other crafting steps.
                if (!processing) {
                    return;
                } else {
                    updateStatus(processingInventory, processingFluidInventory);
                }
            }
        }

        if (changed) {
            network.getCraftingManager().onTaskChanged();
        }
    }

    public NBTTagList writeToNbt() {
        NBTTagList list = new NBTTagList();

        for (CraftingExtractorStack stack : stacks) {
            list.appendTag(stack.writeToNbt());
        }

        return list;
    }
}
