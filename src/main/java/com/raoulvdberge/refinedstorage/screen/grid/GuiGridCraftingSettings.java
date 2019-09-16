package com.raoulvdberge.refinedstorage.screen.grid;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingSettings;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.GuiAmountSpecifying;
import com.raoulvdberge.refinedstorage.screen.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fluids.FluidAttributes;

public class GuiGridCraftingSettings extends GuiAmountSpecifying<ContainerCraftingSettings> {
    private IGridStack stack;

    public GuiGridCraftingSettings(BaseScreen parent, PlayerEntity player, IGridStack stack) {
        super(parent, new ContainerCraftingSettings(player, stack), 172, 99, player.inventory);

        this.stack = stack;
    }

    @Override
    protected String getOkButtonText() {
        return I18n.format("misc.refinedstorage:start");
    }

    @Override
    protected String getGuiTitle() {
        return I18n.format("container.crafting");
    }

    @Override
    protected String getTexture() {
        return "gui/crafting_settings.png";
    }

    @Override
    protected int[] getIncrements() {
        if (stack instanceof GridStackFluid) {
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
        return stack instanceof GridStackFluid ? FluidAttributes.BUCKET_VOLUME : 1;
    }

    @Override
    protected boolean canAmountGoNegative() {
        return false;
    }

    @Override
    protected int getMaxAmount() {
        return Integer.MAX_VALUE;
    }

    protected void onOkButtonPressed(boolean shiftDown) {
        Integer quantity = Ints.tryParse(amountField.getText());

        if (quantity != null && quantity > 0) {
            // TODO RS.INSTANCE.network.sendToServer(new MessageGridCraftingPreview(stack.getHash(), quantity, shiftDown, stack instanceof GridStackFluid));

            okButton.active = false;
        }
    }
}
