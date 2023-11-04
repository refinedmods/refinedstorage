package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.view.GridViewImpl;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GridFluidUpdateMessage {
    private final boolean canCraft;
    private INetwork network;
    private List<IGridStack> stacks = new ArrayList<>();

    public GridFluidUpdateMessage(boolean canCraft, List<IGridStack> stacks) {
        this.canCraft = canCraft;
        this.stacks = stacks;
    }

    public GridFluidUpdateMessage(INetwork network, boolean canCraft) {
        this.network = network;
        this.canCraft = canCraft;
    }

    public static GridFluidUpdateMessage decode(FriendlyByteBuf buf) {
        boolean canCraft = buf.readBoolean();

        int size = buf.readInt();

        List<IGridStack> stacks = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            stacks.add(StackUtils.readFluidGridStack(buf));
        }

        return new GridFluidUpdateMessage(canCraft, stacks);
    }

    public static void encode(GridFluidUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.canCraft);

        int size = message.network.getFluidStorageCache().getList().getStacks().size() + message.network.getFluidStorageCache().getCraftablesList().getStacks().size();

        buf.writeInt(size);

        for (StackListEntry<FluidStack> stack : message.network.getFluidStorageCache().getList().getStacks()) {
            StackListEntry<FluidStack> craftingEntry = message.network.getFluidStorageCache().getCraftablesList().getEntry(stack.getStack(), IComparer.COMPARE_NBT);

            StackUtils.writeFluidGridStack(buf, stack.getStack(), stack.getId(), craftingEntry != null ? craftingEntry.getId() : null, false, message.network.getFluidStorageTracker().get(stack.getStack()));
        }

        for (StackListEntry<FluidStack> stack : message.network.getFluidStorageCache().getCraftablesList().getStacks()) {
            StackListEntry<FluidStack> regularEntry = message.network.getFluidStorageCache().getList().getEntry(stack.getStack(), IComparer.COMPARE_NBT);

            StackUtils.writeFluidGridStack(buf, stack.getStack(), stack.getId(), regularEntry != null ? regularEntry.getId() : null, true, message.network.getFluidStorageTracker().get(stack.getStack()));
        }
    }

    public static void handle(GridFluidUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            grid.setView(new GridViewImpl(grid, GridScreen.getDefaultSorter(), GridScreen.getSorters()));
            grid.getView().setCanCraft(message.canCraft);
            grid.getView().setStacks(message.stacks);
            grid.getView().forceSort();
        });

        ctx.get().setPacketHandled(true);
    }
}
