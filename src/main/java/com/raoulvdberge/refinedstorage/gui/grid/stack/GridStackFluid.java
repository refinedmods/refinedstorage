package com.raoulvdberge.refinedstorage.gui.grid.stack;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class GridStackFluid implements IGridStack {
    private int hash;
    private FluidStack stack;
    @Nullable
    private IStorageTracker.IStorageTrackerEntry entry;
    private boolean craftable;
    private boolean displayCraftText;
    private String modId;
    private String modName;

    public GridStackFluid(int hash, FluidStack stack, @Nullable IStorageTracker.IStorageTrackerEntry entry, boolean craftable, boolean displayCraftText) {
        this.hash = hash;
        this.stack = stack;
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
        return stack.getDisplayName().getFormattedText(); // TODO correct method usage
    }

    @Override
    public String getModId() {
        if (modId == null) {
            // modId = FluidRegistry.getModId(stack);
            modId = "dinosaur";//TODO

            if (modId == null) {
                modId = "???";
            }
        }

        return modId;
    }

    @Override
    public String getModName() {
        if (modName == null) {
            modName = GridStackItem.getModNameByModId(getModId());

            if (modName == null) {
                modName = "???";
            }
        }

        return modName;
    }

    @Override
    public String[] getOreIds() {
        return new String[]{};
        //return new String[]{stack.getFluid().getName()};
    }

    @Override
    public String getTooltip() {
        return stack.getDisplayName().getFormattedText();//TODO
    }

    @Override
    public int getQuantity() {
        return stack.getAmount();
    }

    @Override
    public String getFormattedFullQuantity() {
        return API.instance().getQuantityFormatter().format(getQuantity()) + " mB";
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        GuiBase.FLUID_RENDERER.draw(gui.getMinecraft(), x, y, stack);

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
