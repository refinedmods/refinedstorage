package com.raoulvdberge.refinedstorage.gui.grid.stack;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class GridStackFluid implements IGridStack {
    private int hash;
    private FluidStack stack;
    @Nullable
    private IStorageTracker.IStorageTrackerEntry entry;
    private boolean craftable;
    private boolean displayCraftText;

    public GridStackFluid(Pair<Integer, FluidStack> data, @Nullable IStorageTracker.IStorageTrackerEntry entry, boolean craftable, boolean displayCraftText) {
        this.hash = data.getLeft();
        this.stack = data.getRight();
        this.entry = entry;
        this.craftable = craftable;
        this.displayCraftText = displayCraftText;
    }

    public FluidStack getStack() {
        return stack;
    }

    @Override
    public boolean isCraftable() {
        return craftable;
    }

    @Override
    public boolean doesDisplayCraftText() {
        return displayCraftText;
    }

    @Override
    public void setDisplayCraftText(boolean displayCraftText) {
        this.displayCraftText = displayCraftText;
    }

    @Override
    public int getHash() {
        return hash;
    }

    @Override
    public String getName() {
        return stack.getFluid().getLocalizedName(stack);
    }

    @Override
    public String getModId() {
        return stack.getFluid().getStill(stack).getNamespace();
    }

    @Override
    public String[] getOreIds() {
        return new String[]{stack.getFluid().getName()};
    }

    @Override
    public String getTooltip() {
        return stack.getFluid().getLocalizedName(stack);
    }

    @Override
    public int getQuantity() {
        return stack.amount;
    }

    @Override
    public String getFormattedFullQuantity() {
        return API.instance().getQuantityFormatter().format(getQuantity()) + " mB";
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        GuiBase.FLUID_RENDERER.draw(gui.mc, x, y, stack);

        String text;

        if (displayCraftText) {
            text = I18n.format("gui.refinedstorage:grid.craft");
        } else {
            text = API.instance().getQuantityFormatter().formatInBucketFormWithOnlyTrailingDigitsIfZero(getQuantity());
        }

        gui.drawQuantity(x, y, text);
    }

    @Override
    public Object getIngredient() {
        return stack;
    }

    @Nullable
    @Override
    public IStorageTracker.IStorageTrackerEntry getTrackerEntry() {
        return entry;
    }

    @Override
    public void setTrackerEntry(@Nullable IStorageTracker.IStorageTrackerEntry entry) {
        this.entry = entry;
    }
}
