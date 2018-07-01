package com.raoulvdberge.refinedstorage.gui.grid.stack;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageTrackerEntry;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GridStackItem implements IGridStack {
    private int hash;
    private ItemStack stack;
    private String cachedName;
    private boolean craftable;
    private boolean displayCraftText;
    private String[] oreIds = null;
    @Nullable
    private IStorageTracker.IStorageTrackerEntry entry;

    public GridStackItem(ItemStack stack) {
        this.stack = stack;
    }

    public GridStackItem(ByteBuf buf) {
        this.stack = StackUtils.readItemStack(buf);
        this.hash = buf.readInt();
        this.craftable = buf.readBoolean();

        setDisplayCraftText(buf.readBoolean());

        if (buf.readBoolean()) {
            this.entry = new StorageTrackerEntry(buf);
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean isCraftable() {
        return craftable;
    }

    public boolean doesDisplayCraftText() {
        return displayCraftText;
    }

    public void setDisplayCraftText(boolean displayCraftText) {
        this.displayCraftText = displayCraftText;

        if (displayCraftText) {
            this.stack.setCount(1);
        }
    }

    @Override
    public int getHash() {
        return hash;
    }

    @Override
    public String getName() {
        try {
            if (cachedName == null) {
                cachedName = stack.getDisplayName();
            }

            return cachedName;
        } catch (Throwable t) {
            return "";
        }
    }

    @Override
    public String getModId() {
        return stack.getItem().getCreatorModId(stack);
    }

    @Override
    public String[] getOreIds() {
        if (oreIds == null) {
            if (stack.isEmpty()) {
                oreIds = new String[]{};
            } else {
                oreIds = Arrays.stream(OreDictionary.getOreIDs(stack)).mapToObj(OreDictionary::getOreName).toArray(String[]::new);
            }
        }

        return oreIds;
    }

    @Override
    public String getTooltip() {
        try {
            return StackUtils.getItemTooltip(stack).stream().collect(Collectors.joining("\n"));
        } catch (Throwable t) {
            return "";
        }
    }

    @Override
    public int getQuantity() {
        return doesDisplayCraftText() ? 0 : stack.getCount();
    }

    @Override
    public String getFormattedFullQuantity() {
        return API.instance().getQuantityFormatter().format(getQuantity());
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        String text = null;

        if (displayCraftText) {
            text = I18n.format("gui.refinedstorage:grid.craft");
        } else if (stack.getCount() > 1) {
            text = API.instance().getQuantityFormatter().formatWithUnits(getQuantity());
        }

        gui.drawItem(x, y, stack, true, text);
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
