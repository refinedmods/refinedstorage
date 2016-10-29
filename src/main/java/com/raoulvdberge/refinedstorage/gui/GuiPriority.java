package com.raoulvdberge.refinedstorage.gui;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.gui.grid.GuiCraftingStart;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.Tuple;

public class GuiPriority extends GuiCraftingStart {
    private TileDataParameter<Integer> priority;

    public GuiPriority(GuiBase parent, TileDataParameter<Integer> priority) {
        super(parent, null, new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, 164, 92);

        this.priority = priority;
    }

    @Override
    protected int getAmount() {
        return priority.getValue();
    }

    @Override
    protected String getStartButtonText() {
        return "Set";
    }

    @Override
    protected String getTitle() {
        return "Priority";
    }

    @Override
    protected String getTexture() {
        return "gui/priority.png";
    }

    @Override
    protected Tuple<Integer, Integer> getAmountPos() {
        return new Tuple<>(18 + 1, 47 + 1);
    }

    @Override
    protected Tuple<Integer, Integer> getIncrementButtonPos(int x, int y) {
        return new Tuple<>(6 + (x * (30 + 3)), y + (y == 0 ? 20 : 64));
    }

    @Override
    protected Tuple<Integer, Integer> getStartCancelPos() {
        return new Tuple<>(107, 30);
    }

    @Override
    protected boolean canAmountGoNegative() {
        return true;
    }

    @Override
    protected int[] getIncrements() {
        return new int[]{
            1, 5, 10,
            -1, -5, -10
        };
    }

    @Override
    protected void startRequest() {
        Integer amount = Ints.tryParse(amountField.getText());

        if (amount != null) {
            TileDataManager.setParameter(priority, amount);

            close();
        }
    }
}
