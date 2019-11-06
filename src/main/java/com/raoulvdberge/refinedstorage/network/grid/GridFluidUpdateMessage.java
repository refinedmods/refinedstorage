package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.StackListEntry;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.screen.grid.view.FluidGridView;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GridFluidUpdateMessage {
    private INetwork network;

    private boolean canCraft;
    private List<IGridStack> stacks = new ArrayList<>();

    public GridFluidUpdateMessage(boolean canCraft, List<IGridStack> stacks) {
        this.canCraft = canCraft;
        this.stacks = stacks;
    }

    public GridFluidUpdateMessage(INetwork network, boolean canCraft) {
        this.network = network;
        this.canCraft = canCraft;
    }

    public static GridFluidUpdateMessage decode(PacketBuffer buf) {
        boolean canCraft = buf.readBoolean();

        int size = buf.readInt();

        List<IGridStack> stacks = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            stacks.add(StackUtils.readFluidGridStack(buf));
        }

        return new GridFluidUpdateMessage(canCraft, stacks);
    }

    public static void encode(GridFluidUpdateMessage message, PacketBuffer buf) {
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
            grid.setView(new FluidGridView(grid, GridScreen.getDefaultSorter(), GridScreen.getSorters()));
            grid.getView().setCanCraft(message.canCraft);
            grid.getView().setStacks(message.stacks);
            grid.getView().sort();
        });

        ctx.get().setPacketHandled(true);
    }
}
