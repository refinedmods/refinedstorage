package com.raoulvdberge.refinedstorage.screen.grid.stack;

import com.raoulvdberge.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.render.FluidRenderer;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class FluidGridStack implements IGridStack {
    private UUID id;
    private FluidStack stack;
    @Nullable
    private StorageTrackerEntry entry;
    private boolean craftable;
    private boolean displayCraftText;
    private String modId;
    private String modName;

    public FluidGridStack(UUID id, FluidStack stack, @Nullable StorageTrackerEntry entry, boolean craftable, boolean displayCraftText) {
        this.id = id;
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
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return stack.getDisplayName().getFormattedText();
    }

    @Override
    public String getModId() {
        if (modId == null) {
            ResourceLocation registryName = stack.getFluid().getRegistryName();

            if (registryName != null) {
                modId = registryName.getNamespace();
            } else {
                modId = "???";
            }
        }

        return modId;
    }

    @Override
    public String getModName() {
        if (modName == null) {
            modName = ItemGridStack.getModNameByModId(getModId());

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
        return stack.getDisplayName().getFormattedText();
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
    public void draw(BaseScreen gui, int x, int y) {
        FluidRenderer.INSTANCE.render(x, y, stack);

        String text;

        if (displayCraftText) {
            text = I18n.format("gui.refinedstorage.grid.craft");
        } else {
            text = API.instance().getQuantityFormatter().formatInBucketFormWithOnlyTrailingDigitsIfZero(getQuantity());
        }

        gui.renderQuantity(x, y, text);
    }

    @Override
    public Object getIngredient() {
        return stack;
    }

    @Nullable
    @Override
    public StorageTrackerEntry getTrackerEntry() {
        return entry;
    }

    @Override
    public void setTrackerEntry(@Nullable StorageTrackerEntry entry) {
        this.entry = entry;
    }
}
