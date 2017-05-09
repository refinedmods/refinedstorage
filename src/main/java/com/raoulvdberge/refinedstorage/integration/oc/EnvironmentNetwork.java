package com.raoulvdberge.refinedstorage.integration.oc;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.item.ItemStack;

import static com.raoulvdberge.refinedstorage.api.util.IComparer.COMPARE_DAMAGE;
import static com.raoulvdberge.refinedstorage.api.util.IComparer.COMPARE_NBT;

public class EnvironmentNetwork extends AbstractManagedEnvironment {
    protected final INetworkNode node;

    public EnvironmentNetwork(INetworkNode node) {
        this.node = node;

        setNode(Network.newNode(this, Visibility.Network).withComponent("refinedstorage", Visibility.Network).create());
    }

    @Callback
    public Object[] isConnected(final Context context, final Arguments args) {
        return new Object[]{node.getNetwork() != null};
    }

    @Callback
    public Object[] getEnergyUsage(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{null, "not connected"};
        }

        return new Object[]{node.getNetwork().getEnergyUsage()};
    }

    @Callback
    public Object[] getTasks(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{null, "not connected"};
        }

        return new Object[]{node.getNetwork().getCraftingManager().getTasks()};
    }

    @Callback
    public Object[] getPatterns(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{null, "not connected"};
        }

        return new Object[]{node.getNetwork().getCraftingManager().getPatterns()};
    }

    @Callback
    public Object[] hasPattern(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{null, "not connected"};
        }

        ItemStack stack = args.checkItemStack(0);

        return new Object[]{node.getNetwork().getCraftingManager().hasPattern(stack)};
    }

    @Callback
    public Object[] getMissingItems(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{null, "not connected"};
        }

        ItemStack stack = args.checkItemStack(0);

        if (!node.getNetwork().getCraftingManager().hasPattern(stack)) {
            throw new IllegalArgumentException("No pattern for this item exists");
        }

        int count = args.optInteger(1, 1);
        ICraftingPattern pattern = node.getNetwork().getCraftingManager().getPattern(stack);

        ICraftingTask task = node.getNetwork().getCraftingManager().create(stack, pattern, count, true);
        task.calculate();

        return new Object[]{task.getMissing().getStacks()};
    }

    @Callback
    public Object[] scheduleTask(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{"not connected"};
        }

        ItemStack stack = args.checkItemStack(0);

        if (!node.getNetwork().getCraftingManager().hasPattern(stack)) {
            throw new IllegalArgumentException("No pattern for this item stack exists");
        }

        int amount = args.optInteger(1, 1);
        ICraftingPattern pattern = node.getNetwork().getCraftingManager().getPattern(stack);

        ICraftingTask task = node.getNetwork().getCraftingManager().create(stack, pattern, amount, true);
        task.calculate();

        node.getNetwork().getCraftingManager().add(task);

        return new Object[]{};
    }

    @Callback
    public Object[] cancelTask(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{null, "not connected"};
        }

        ItemStack stack = args.checkItemStack(0);

        int count = 0;
        for (ICraftingTask task : node.getNetwork().getCraftingManager().getTasks()) {
            if (API.instance().getComparer().isEqual(task.getRequested(), stack, COMPARE_NBT | COMPARE_DAMAGE)) {
                node.getNetwork().getCraftingManager().cancel(task);

                count++;
            }
        }

        return new Object[]{count};
    }

    @Callback
    public Object[] getFluids(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{null, "not connected"};
        }

        return new Object[]{node.getNetwork().getFluidStorageCache().getList().getStacks()};
    }

    @Callback
    public Object[] getItem(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{null, "not connected"};
        }

        ItemStack stack = args.checkItemStack(0);
        boolean compareMeta = args.optBoolean(1, true);
        boolean compareNBT = args.optBoolean(2, true);

        int flags = 0;

        if (compareMeta) {
            flags |= IComparer.COMPARE_DAMAGE;
        }

        if (compareNBT) {
            flags |= IComparer.COMPARE_NBT;
        }

        return new Object[]{node.getNetwork().getItemStorageCache().getList().get(stack, flags)};
    }

    @Callback
    public Object[] getItems(final Context context, final Arguments args) {
        if (node.getNetwork() == null) {
            return new Object[]{null, "not connected"};
        }

        return new Object[]{node.getNetwork().getItemStorageCache().getList().getStacks()};
    }
}

