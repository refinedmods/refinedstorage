package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import com.refinedmods.refinedstorage.screen.grid.view.GridViewImpl;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.List;

public class GridFluidUpdateMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_fluid_update");

    private final boolean canCraft;
    private final List<FluidGridStack> stacks;

    public GridFluidUpdateMessage(final boolean canCraft, final List<FluidGridStack> stacks) {
        this.canCraft = canCraft;
        this.stacks = stacks;
    }

    public static GridFluidUpdateMessage decode(FriendlyByteBuf buf) {
        final boolean canCraft = buf.readBoolean();
        final int size = buf.readInt();
        final List<FluidGridStack> stacks = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            stacks.add(StackUtils.readFluidGridStack(buf));
        }
        return new GridFluidUpdateMessage(canCraft, stacks);
    }

    public static void handle(GridFluidUpdateMessage message, PlayPayloadContext ctx) {
        BaseScreen.executeLater(GridScreen.class, grid -> {
            grid.setView(new GridViewImpl(grid, GridScreen.getDefaultSorter(), GridScreen.getSorters()));
            grid.getView().setCanCraft(message.canCraft);
            grid.getView().setStacks(message.stacks);
            grid.getView().forceSort();
        });
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(canCraft);
        buf.writeInt(stacks.size());
        stacks.forEach(stack -> StackUtils.writeFluidGridStack(buf, stack));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
