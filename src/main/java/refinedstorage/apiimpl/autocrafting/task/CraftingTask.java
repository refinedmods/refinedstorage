package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RSUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.util.IComparer;
import refinedstorage.api.util.IFluidStackList;
import refinedstorage.api.util.IItemStackList;
import refinedstorage.apiimpl.API;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementFluidRender;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementText;
import refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewStack;

import java.util.*;
import java.util.stream.Collectors;

public class CraftingTask implements ICraftingTask {
    private INetworkMaster network;
    private ItemStack requested;
    private ICraftingPattern pattern;
    private int quantity;
    private List<IProcessable> toProcess = new ArrayList<>();
    private IItemStackList toTake = API.instance().createItemStackList();
    private IItemStackList toCraft = API.instance().createItemStackList();
    private IFluidStackList toTakeFluids = API.instance().createFluidStackList();
    private IItemStackList missing = API.instance().createItemStackList();
    private Set<ICraftingPattern> usedPatterns = new HashSet<>();
    private boolean recurseFound = false;
    private Deque<ItemStack> toInsert = new ArrayDeque<>();
    private int compare = IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT;
    private List<ItemStack> took = new ArrayList<>();
    private List<FluidStack> tookFluids = new ArrayList<>();

    public CraftingTask(INetworkMaster network, ItemStack requested, ICraftingPattern pattern, int quantity) {
        this.network = network;
        this.requested = requested;
        this.pattern = pattern;
        this.quantity = quantity;

        if (pattern.isOredict()) {
            this.compare = IComparer.COMPARE_OREDICT;
        }
    }

    @Override
    public void calculate() {
        IItemStackList networkList = network.getItemStorageCache().getList().copy();
        IItemStackList toInsert = API.instance().createItemStackList();

        toCraft.add(ItemHandlerHelper.copyStackWithSize(requested, quantity));
        int quantity = this.quantity;
        while (quantity > 0 && !recurseFound) {
            calculate(networkList, pattern, toInsert);
            quantity -= pattern.getQuantityPerRequest(requested);
        }

        if (!recurseFound) {
            this.toInsert.addAll(toInsert.getStacks());
        }
        usedPatterns.clear();
    }

    private void calculate(IItemStackList networkList, ICraftingPattern pattern, IItemStackList toInsert) {
        recurseFound = !usedPatterns.add(pattern);
        if (recurseFound) return;

        ItemStack[] took = new ItemStack[9];

        if (pattern.isProcessing()) {
            toProcess.add(new Processable(pattern));
        }

        IItemStackList inputs = API.instance().createItemStackList();
        IItemStackList actualInputs = API.instance().createItemStackList();
        for (ItemStack input : pattern.getInputs()) {
            if (input != null) {
                inputs.add(input.copy());
            }
        }

        for (ItemStack input : inputs.getStacks()) {
            ItemStack extraStack = toInsert.get(input, compare);
            ItemStack networkStack = networkList.get(input, compare);

            while (input.stackSize > 0) {
                if (extraStack != null && extraStack.stackSize > 0) {
                    int takeQuantity = Math.min(extraStack.stackSize, input.stackSize);
                    ItemStack inputStack = ItemHandlerHelper.copyStackWithSize(extraStack, takeQuantity);
                    actualInputs.add(inputStack.copy());
                    input.stackSize -= takeQuantity;
                    toInsert.remove(inputStack, true);
                } else if (networkStack != null && networkStack.stackSize > 0) {
                    int takeQuantity = Math.min(networkStack.stackSize, input.stackSize);
                    ItemStack inputStack = ItemHandlerHelper.copyStackWithSize(networkStack, takeQuantity);
                    toTake.add(inputStack.copy());
                    actualInputs.add(inputStack.copy());
                    input.stackSize -= takeQuantity;
                    networkList.remove(inputStack, true);
                } else {
                    ICraftingPattern inputPattern = network.getPattern(input, compare);
                    if (inputPattern != null) {
                        int craftQuantity = Math.min(inputPattern.getQuantityPerRequest(input), input.stackSize);
                        ItemStack inputCrafted = ItemHandlerHelper.copyStackWithSize(input, craftQuantity);
                        toCraft.add(inputCrafted.copy());
                        actualInputs.add(inputCrafted.copy());
                        calculate(networkList, inputPattern, toInsert);
                        input.stackSize -= craftQuantity;
                        // Calculate added all the crafted outputs toInsert
                        // So we remove the ones we use from toInsert
                        toInsert.remove(inputCrafted, true);
                    } else if (!doFluidCalculation(networkList, input, toInsert)) {
                        missing.add(input.copy());
                        input.stackSize = 0;
                    }
                }
            }
        }

        if (missing.isEmpty()) {
            for (int i = 0; i < pattern.getInputs().size(); i++) {
                ItemStack input = pattern.getInputs().get(i);
                if (input != null) {
                    ItemStack actualInput = actualInputs.get(input, compare);
                    ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, input.stackSize);
                    took[i] = taken;
                    if (taken == null) {
                        taken = null;
                    }
                    actualInputs.remove(taken, true);
                }
            }

            for (ItemStack byproduct : (pattern.isOredict() ? pattern.getByproducts(took) : pattern.getByproducts())) {
                toInsert.add(byproduct.copy());
            }

            for (ItemStack output : pattern.getOutputs()) {
                toInsert.add(output.copy());
            }
        }
        usedPatterns.remove(pattern);
    }

    private boolean doFluidCalculation(IItemStackList networkList, ItemStack input, IItemStackList toInsert) {
        FluidStack fluidInItem = RSUtils.getFluidFromStack(input, true);

        if (fluidInItem != null && RSUtils.hasFluidBucket(fluidInItem)) {
            FluidStack fluidInStorage = network.getFluidStorageCache().getList().get(fluidInItem);

            if (fluidInStorage == null || fluidInStorage.amount < fluidInItem.amount) {
                missing.add(input);
            } else {
                boolean hasBucket = networkList.get(RSUtils.EMPTY_BUCKET) != null;
                ICraftingPattern bucketPattern = network.getPattern(RSUtils.EMPTY_BUCKET);

                if (!hasBucket) {
                    if (bucketPattern == null) {
                        missing.add(RSUtils.EMPTY_BUCKET.copy());
                    } else {
                        toCraft.add(RSUtils.EMPTY_BUCKET.copy());
                        calculate(networkList, bucketPattern, toInsert);
                    }
                }

                if (hasBucket || bucketPattern != null) {
                    toTakeFluids.add(fluidInItem.copy());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCancelled() {
        for (ItemStack stack : took) {
            network.insertItem(stack, stack.stackSize, false);
        }

        for (FluidStack stack : tookFluids) {
            network.insertFluid(stack, stack.amount, false);
        }
    }

    @Override
    public String toString() {
        return "\nCraftingTask{quantity=" + quantity +
            "\n, toTake=" + toTake +
            "\n, toTakeFluids=" + toTakeFluids +
            "\n, toProcess=" + toProcess +
            "\n, toCraft=" + toProcess +
            "\n, toInsert=" + toInsert +
            '}';
    }

    @Override
    public boolean update() {
        for (IProcessable processable : toProcess) {
            IItemHandler inventory = processable.getPattern().getContainer().getFacingInventory();

            if (inventory != null && processable.getStackToInsert() != null) {
                ItemStack toInsert = network.extractItem(processable.getStackToInsert(), 1, compare);

                if (ItemHandlerHelper.insertItem(inventory, toInsert, true) == null) {
                    ItemHandlerHelper.insertItem(inventory, toInsert, false);

                    processable.nextStack();
                }
            }
        }

        for (ItemStack stack : toTake.getStacks()) {
            ItemStack stackExtracted = network.extractItem(stack, 1, compare);

            if (stackExtracted != null) {
                toTake.remove(stack, 1, true);

                took.add(stackExtracted);
            }

            break;
        }

        // If we took all the items, we can start taking fluids
        if (toTake.isEmpty()) {
            for (FluidStack stack : toTakeFluids.getStacks()) {
                FluidStack stackExtracted = network.extractFluid(stack, stack.amount);

                if (stackExtracted != null) {
                    toTakeFluids.remove(stack, stack.amount, true);

                    tookFluids.add(stackExtracted);
                }

                break;
            }
        }

        if (isFinished()) {
            ItemStack insert = toInsert.peek();

            if (insert != null && network.insertItem(insert, insert.stackSize, true) == null) {
                network.insertItem(insert, insert.stackSize, false);

                toInsert.pop();
            }

            return toInsert.isEmpty();
        }

        return false;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return tag;
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        List<ICraftingMonitorElement> elements = new ArrayList<>();

        elements.add(new CraftingMonitorElementItemRender(
            network.getCraftingTasks().indexOf(this),
            pattern.getOutputs().get(0),
            quantity,
            0
        ));

        if (isFinished()) {
            elements.add(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_inserting", 16));

            elements.addAll(toInsert.stream()
                .map(stack -> new CraftingMonitorElementItemRender(
                    -1,
                    stack,
                    stack.stackSize,
                    32
                ))
                .collect(Collectors.toList())
            );
        } else {
            if (!toTake.isEmpty()) {
                elements.add(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_taking", 16));

                elements.addAll(toTake.getStacks().stream()
                    .map(stack -> new CraftingMonitorElementItemRender(
                        -1,
                        stack,
                        stack.stackSize,
                        32
                    ))
                    .collect(Collectors.toList())
                );
            }

            if (!toTakeFluids.isEmpty()) {
                elements.add(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.fluids_taking", 16));

                elements.addAll(toTakeFluids.getStacks().stream()
                    .map(stack -> new CraftingMonitorElementFluidRender(
                        -1,
                        stack,
                        32
                    ))
                    .collect(Collectors.toList())
                );
            }

            if (!hasProcessedItems()) {
                elements.add(new CraftingMonitorElementText("gui.refinedstorage:crafting_monitor.items_processing", 16));

                for (IProcessable processable : toProcess) {
                    for (int i = 0; i < processable.getPattern().getOutputs().size(); ++i) {
                        if (!processable.hasReceivedOutput(i)) {
                            elements.add(new CraftingMonitorElementItemRender(
                                -1,
                                processable.getPattern().getOutputs().get(i),
                                processable.getPattern().getOutputs().get(i).stackSize,
                                32
                            ));
                        }
                    }
                }
            }
        }

        return elements;
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public List<IProcessable> getToProcess() {
        return toProcess;
    }

    @Override
    public boolean isValid() {
        return !recurseFound;
    }

    @Override
    public List<CraftingPreviewStack> getPreviewStacks() {
        if (!isValid()) return new ArrayList<>();

        Map<Integer, CraftingPreviewStack> map = new LinkedHashMap<>();

        for (ItemStack stack : toCraft.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);
            CraftingPreviewStack previewStack = map.get(hash);
            if (previewStack == null) {
                previewStack = new CraftingPreviewStack(stack);
            }
            previewStack.addToCraft(stack.stackSize);
            map.put(hash, previewStack);
        }

        for (ItemStack stack : toTake.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);
            CraftingPreviewStack previewStack = map.get(hash);
            if (previewStack == null) {
                previewStack = new CraftingPreviewStack(stack);
            }
            previewStack.addAvailable(stack.stackSize);
            map.put(hash, previewStack);
        }

        for (ItemStack stack : missing.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);
            CraftingPreviewStack previewStack = map.get(hash);
            if (previewStack == null) {
                previewStack = new CraftingPreviewStack(stack);
            }
            previewStack.setMissing(true);
            previewStack.addToCraft(stack.stackSize);
            map.put(hash, previewStack);
        }

        return new ArrayList<>(map.values());
    }

    private boolean isFinished() {
        return toTake.isEmpty() && toTakeFluids.isEmpty() && missing.isEmpty() && hasProcessedItems();
    }

    private boolean hasProcessedItems() {
        return toProcess.stream().allMatch(IProcessable::hasReceivedOutputs);
    }
}
