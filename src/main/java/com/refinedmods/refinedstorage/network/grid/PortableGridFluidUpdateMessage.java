package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.view.GridViewImpl;
import com.refinedmods.refinedstorage.blockentity.grid.portable.IPortableGrid;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PortableGridFluidUpdateMessage {
    private IPortableGrid portableGrid;

    private List<IGridStack> stacks = new ArrayList<>();

    public PortableGridFluidUpdateMessage(List<IGridStack> stacks) {
        this.stacks = stacks;
    }

    public PortableGridFluidUpdateMessage(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    public static PortableGridFluidUpdateMessage decode(FriendlyByteBuf buf) {
        int size = buf.readInt();

        List<IGridStack> stacks = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            stacks.add(StackUtils.readFluidGridStack(buf));
        }

        return new PortableGridFluidUpdateMessage(stacks);
    }

    public static void encode(PortableGridFluidUpdateMessage message, FriendlyByteBuf buf) {
        int size = message.portableGrid.getFluidCache().getList().getStacks().size();

        buf.writeInt(size);

        for (StackListEntry<FluidStack> stack : message.portableGrid.getFluidCache().getList().getStacks()) {
            StackUtils.writeFluidGridStack(buf, stack.getStack(), stack.getId(), null, false, message.portableGrid.getFluidStorageTracker().get(stack.getStack()));
        }
    }

    public static void handle(PortableGridFluidUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            grid.setView(new GridViewImpl(grid, GridScreen.getDefaultSorter(), GridScreen.getSorters()));
            grid.getView().setStacks(message.stacks);
            grid.getView().sort();
        });

        ctx.get().setPacketHandled(true);
    }
}
