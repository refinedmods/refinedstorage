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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GridItemUpdateMessage {
    private final boolean canCraft;
    private INetwork network;
    private List<IGridStack> stacks = new ArrayList<>();

    public GridItemUpdateMessage(boolean canCraft, List<IGridStack> stacks) {
        this.canCraft = canCraft;
        this.stacks = stacks;
    }

    public GridItemUpdateMessage(INetwork network, boolean canCraft) {
        this.network = network;
        this.canCraft = canCraft;
    }

    public static GridItemUpdateMessage decode(FriendlyByteBuf buf) {
        boolean canCraft = buf.readBoolean();

        int size = buf.readInt();

        List<IGridStack> stacks = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            stacks.add(StackUtils.readItemGridStack(buf));
        }

        return new GridItemUpdateMessage(canCraft, stacks);
    }

    public static void encode(GridItemUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.canCraft);

        int size = message.network.getItemStorageCache().getList().getStacks().size() + message.network.getItemStorageCache().getCraftablesList().getStacks().size();

        buf.writeInt(size);

        for (StackListEntry<ItemStack> stack : message.network.getItemStorageCache().getList().getStacks()) {
            StackListEntry<ItemStack> craftingEntry = message.network.getItemStorageCache().getCraftablesList().getEntry(stack.getStack(), IComparer.COMPARE_NBT);

            StackUtils.writeItemGridStack(buf, stack.getStack(), stack.getId(), craftingEntry != null ? craftingEntry.getId() : null, false, message.network.getItemStorageTracker().get(stack.getStack()));
        }

        for (StackListEntry<ItemStack> stack : message.network.getItemStorageCache().getCraftablesList().getStacks()) {
            StackListEntry<ItemStack> regularEntry = message.network.getItemStorageCache().getList().getEntry(stack.getStack(), IComparer.COMPARE_NBT);

            StackUtils.writeItemGridStack(buf, stack.getStack(), stack.getId(), regularEntry != null ? regularEntry.getId() : null, true, message.network.getItemStorageTracker().get(stack.getStack()));
        }
    }

    public static void handle(GridItemUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            grid.setView(new GridViewImpl(grid, GridScreen.getDefaultSorter(), GridScreen.getSorters()));
            grid.getView().setCanCraft(message.canCraft);
            grid.getView().setStacks(message.stacks);
            grid.getView().forceSort();
        });

        ctx.get().setPacketHandled(true);
    }
}
