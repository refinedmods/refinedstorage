package com.raoulvdberge.refinedstorage.gui.grid.stack;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GridStackItem implements IGridStack {
    private int hash;
    private ItemStack stack;
    private boolean craftable;
    private boolean displayCraftText;
    private String[] oreIds = null;

    public GridStackItem(ByteBuf buf) {
        this.stack = RSUtils.readItemStack(buf);
        this.hash = buf.readInt();
        this.craftable = buf.readBoolean();

        setDisplayCraftText(buf.readBoolean());
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
        return stack.getDisplayName();
    }

    @Override
    public String getModId() {
        return Item.REGISTRY.getNameForObject(stack.getItem()).getResourceDomain();
    }

    @Override
    public String[] getOreIds() {
        if (oreIds == null) {
            oreIds = Arrays.stream(OreDictionary.getOreIDs(stack)).mapToObj(OreDictionary::getOreName).collect(Collectors.toList()).toArray(new String[0]);
        }

        return oreIds;
    }

    @Override
    public String getTooltip() {
        List<String> lines = stack.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

        // From GuiScreen#renderToolTip
        for (int i = 0; i < lines.size(); ++i) {
            if (i == 0) {
                lines.set(i, stack.getRarity().rarityColor + lines.get(i));
            } else {
                lines.set(i, TextFormatting.GRAY + lines.get(i));
            }
        }

        return lines.stream().collect(Collectors.joining("\n"));
    }

    @Override
    public int getQuantity() {
        return stack.getCount();
    }

    private String getQuantityForDisplay(boolean advanced) {
        int qty = stack.getCount();

        if (displayCraftText) {
            return I18n.format("gui.refinedstorage:grid.craft");
        } else if (advanced && qty > 1) {
            return String.valueOf(qty);
        } else if (qty == 1) {
            return null;
        }

        return RSUtils.formatQuantity(qty);
    }

    @Override
    public void draw(GuiBase gui, int x, int y, boolean isOverWithShift) {
        gui.drawItem(x, y, stack, true, getQuantityForDisplay(isOverWithShift));
    }

    @Override
    public Object getIngredient() {
        return stack;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IGridStack && ((GridStackItem) obj).getHash() == hash;
    }
}
