package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.screen.grid.view.ItemGridView;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GridItemUpdateMessage {
    private INetwork network;

    private boolean canCraft;
    private List<IGridStack> stacks = new ArrayList<>();

    public GridItemUpdateMessage(boolean canCraft, List<IGridStack> stacks) {
        this.canCraft = canCraft;
        this.stacks = stacks;
    }

    public GridItemUpdateMessage(INetwork network, boolean canCraft) {
        this.network = network;
        this.canCraft = canCraft;
    }

    public static GridItemUpdateMessage decode(PacketBuffer buf) {
        boolean canCraft = buf.readBoolean();

        int size = buf.readInt();

        List<IGridStack> stacks = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            stacks.add(StackUtils.readItemGridStack(buf));
        }

        return new GridItemUpdateMessage(canCraft, stacks);
    }

    public static void encode(GridItemUpdateMessage message, PacketBuffer buf) {
        buf.writeBoolean(message.canCraft);

        int size = message.network.getItemStorageCache().getList().getStacks().size();

        for (ICraftingPattern pattern : message.network.getCraftingManager().getPatterns()) {
            size += pattern.getOutputs().size();
        }

        buf.writeInt(size);

        for (ItemStack stack : message.network.getItemStorageCache().getList().getStacks()) {
            StackUtils.writeItemGridStack(buf, stack, message.network, false, message.network.getItemStorageTracker().get(stack));
        }

        for (ICraftingPattern pattern : message.network.getCraftingManager().getPatterns()) {
            for (ItemStack output : pattern.getOutputs()) {
                StackUtils.writeItemGridStack(buf, output, message.network, true, message.network.getItemStorageTracker().get(output));
            }
        }
    }

    public static void handle(GridItemUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            grid.setView(new ItemGridView(grid, GridScreen.getDefaultSorter(), GridScreen.getSorters()));
            grid.getView().setCanCraft(message.canCraft);
            grid.getView().setStacks(message.stacks);
            grid.getView().sort();
        });

        ctx.get().setPacketHandled(true);
    }
}
