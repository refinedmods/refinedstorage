package com.raoulvdberge.refinedstorage.gui.grid.stack;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageTrackerEntry;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
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
    private String modId;
    private String modName;
    private String tooltip;

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

    @Nullable
    static String getModNameByModId(String modId) {
        ModContainer container = Loader.instance().getActiveModList().stream()
            .filter(m -> m.getModId().toLowerCase().equals(modId))
            .findFirst()
            .orElse(null);

        return container == null ? null : container.getName();
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isCraftable() {
        return craftable;
    }

    @Override
    public void setCraftable(boolean craftable) {
        this.craftable = craftable;
    }

    @Override
    public boolean doesDisplayCraftText() {
        return displayCraftText;
    }

    @Override
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
        if (modId == null) {
            modId = stack.getItem().getCreatorModId(stack);

            if (modId == null) {
                modId = "???";
            }
        }

        return modId;
    }

    @Override
    public String getModName() {
        if (modName == null) {
            modName = getModNameByModId(getModId());

            if (modName == null) {
                modName = "???";
            }
        }

        return modName;
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
        if (tooltip == null) {
            try {
                tooltip = RenderUtils.getItemTooltip(stack).stream().collect(Collectors.joining("\n"));
            } catch (Throwable t) {
                tooltip = "";
            }
        }

        return tooltip;
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
