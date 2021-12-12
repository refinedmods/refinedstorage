package com.refinedmods.refinedstorage.screen.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.CraftingSettingsContainer;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewRequestMessage;
import com.refinedmods.refinedstorage.screen.AmountSpecifyingScreen;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidAttributes;

public class CraftingSettingsScreen extends AmountSpecifyingScreen<CraftingSettingsContainer> {
    private final IGridStack stack;

    public CraftingSettingsScreen(BaseScreen parent, Player player, IGridStack stack) {
        super(parent, new CraftingSettingsContainer(player, stack), 172, 99, player.getInventory(), new TranslatableComponent("container.crafting"));

        this.stack = stack;
    }

    @Override
    protected Component getOkButtonText() {
        return new TranslatableComponent("misc.refinedstorage.start");
    }

    @Override
    protected String getTexture() {
        return "gui/amount_specifying.png";
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
        return stack instanceof FluidGridStack ? FluidAttributes.BUCKET_VOLUME : 1;
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
