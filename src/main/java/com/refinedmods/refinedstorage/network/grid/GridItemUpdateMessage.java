package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import com.refinedmods.refinedstorage.screen.grid.view.GridViewImpl;
import com.refinedmods.refinedstorage.util.StackUtils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class GridItemUpdateMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_item_update");

    private final boolean canCraft;
    private final List<ItemGridStack> stacks;

    public GridItemUpdateMessage(boolean canCraft, List<ItemGridStack> stacks) {
        this.canCraft = canCraft;
        this.stacks = stacks;
    }

    public static GridItemUpdateMessage decode(FriendlyByteBuf buf) {
        final boolean canCraft = buf.readBoolean();
        final int size = buf.readInt();
        final List<ItemGridStack> stacks = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            stacks.add(StackUtils.readItemGridStack(buf));
        }
        return new GridItemUpdateMessage(canCraft, stacks);
    }

    public static void handle(GridItemUpdateMessage message, PlayPayloadContext ctx) {
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
        stacks.forEach(stack -> StackUtils.writeItemGridStack(buf, stack));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
