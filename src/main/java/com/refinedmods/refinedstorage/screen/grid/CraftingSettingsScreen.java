package com.refinedmods.refinedstorage.screen.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.CraftingSettingsContainerMenu;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewRequestMessage;
import com.refinedmods.refinedstorage.screen.AmountSpecifyingScreen;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidType;

public class CraftingSettingsScreen extends AmountSpecifyingScreen<CraftingSettingsContainerMenu> {
    private final IGridStack stack;

    public CraftingSettingsScreen(BaseScreen parent, Player player, IGridStack stack) {
        super(parent, new CraftingSettingsContainerMenu(player, stack), 172, 99, player.getInventory(), Component.translatable("container.crafting"));

        this.stack = stack;
    }

    @Override
    protected Component getOkButtonText() {
        return Component.translatable("misc.refinedstorage.start");
    }

    @Override
    protected int[] getIncrements() {
        if (stack instanceof FluidGridStack) {
            return new int[]{
                100, 500, 1000,
                -100, -500, -1000
            };
        } else {
            return new int[]{
                1, 10, 64,
                -1, -10, -64
            };
        }
    }

    @Override
    protected int getDefaultAmount() {
        return stack instanceof FluidGridStack ? FluidType.BUCKET_VOLUME : 1;
    }

    @Override
    protected boolean canAmountGoNegative() {
        return false;
    }

    @Override
    protected int getMaxAmount() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected void onOkButtonPressed(boolean shiftDown) {
        try {
            int quantity = Integer.parseInt(amountField.getValue());

            RS.NETWORK_HANDLER.sendToServer(new GridCraftingPreviewRequestMessage(stack.getId(), quantity, shiftDown, stack instanceof FluidGridStack));

            okButton.active = false;
        } catch (NumberFormatException e) {
            // NO OP
        }
    }
}
