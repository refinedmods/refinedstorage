package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.util.StackListEntry;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.screen.grid.view.ItemGridView;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PortableGridItemUpdateMessage {
    private IPortableGrid portableGrid;

    private List<IGridStack> stacks = new ArrayList<>();

    public PortableGridItemUpdateMessage(List<IGridStack> stacks) {
        this.stacks = stacks;
    }

    public PortableGridItemUpdateMessage(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    public static PortableGridItemUpdateMessage decode(PacketBuffer buf) {
        int size = buf.readInt();

        List<IGridStack> stacks = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            stacks.add(StackUtils.readItemGridStack(buf));
        }

        return new PortableGridItemUpdateMessage(stacks);
    }

    public static void encode(PortableGridItemUpdateMessage message, PacketBuffer buf) {
        int size = message.portableGrid.getItemCache().getList().getStacks().size();

        buf.writeInt(size);

        for (StackListEntry<ItemStack> stack : message.portableGrid.getItemCache().getList().getStacks()) {
            StackUtils.writeItemGridStack(buf, stack.getStack(), stack.getId(), false, null, message.portableGrid.getItemStorageTracker().get(stack.getStack()));
        }
    }

    public static void handle(PortableGridItemUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            grid.setView(new ItemGridView(grid, GridScreen.getDefaultSorter(), GridScreen.getSorters()));
            grid.getView().setStacks(message.stacks);
            grid.getView().sort();
        });

        ctx.get().setPacketHandled(true);
    }
}
