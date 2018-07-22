package com.raoulvdberge.refinedstorage.gui.grid;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingSettings;
import com.raoulvdberge.refinedstorage.gui.GuiAmountSpecifying;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingPreview;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;

public class GuiGridCraftingSettings extends GuiAmountSpecifying {
    private IGridStack stack;

    public GuiGridCraftingSettings(GuiBase parent, EntityPlayer player, IGridStack stack) {
        super(parent, new ContainerCraftingSettings(player, stack), 172, 99);

        this.stack = stack;
    }

    @Override
    protected String getOkButtonText() {
        return t("misc.refinedstorage:start");
    }

    @Override
    protected String getTitle() {
        return t("container.crafting");
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
        return stack instanceof GridStackFluid ? Fluid.BUCKET_VOLUME : 1;
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
            RS.INSTANCE.network.sendToServer(new MessageGridCraftingPreview(stack.getHash(), quantity, shiftDown, stack instanceof GridStackFluid));

            okButton.enabled = false;
        }
    }
}
