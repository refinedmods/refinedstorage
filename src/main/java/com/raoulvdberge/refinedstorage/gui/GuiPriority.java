package com.raoulvdberge.refinedstorage.gui;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import org.apache.commons.lang3.tuple.Pair;

public class GuiPriority extends GuiAmountSpecifying {
    private TileDataParameter<Integer, ?> priority;

    public GuiPriority(GuiBase parent, TileDataParameter<Integer, ?> priority) {
        super(parent, new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 164, 92);

        this.priority = priority;
    }

    @Override
    protected int getDefaultAmount() {
        return priority.getValue();
    }

    @Override
    protected String getOkButtonText() {
        return t("misc.refinedstorage:set");
    }

    @Override
    protected String getTitle() {
        return t("misc.refinedstorage:priority");
    }

    @Override
    protected String getTexture() {
        return "gui/priority.png";
    }

    @Override
    protected Pair<Integer, Integer> getAmountPos() {
        return Pair.of(18 + 1, 47 + 1);
    }

    @Override
    protected Pair<Integer, Integer> getOkCancelPos() {
        return Pair.of(107, 30);
    }

    @Override
    protected boolean canAmountGoNegative() {
        return true;
    }

    @Override
    protected int getMaxAmount() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int[] getIncrements() {
        return new int[]{
            1, 5, 10,
            -1, -5, -10
        };
    }

    @Override
    protected void onOkButtonPressed(boolean noPreview) {
        Integer amount = Ints.tryParse(amountField.getText());

        if (amount != null) {
            TileDataManager.setParameter(priority, amount);

            close();
        }
    }
}
